package org.headvances.ml.classify.text;

import java.io.ObjectInputStream;

import org.headvances.ml.Classifier;
import org.headvances.ml.CrossValidationReporter;
import org.headvances.ml.Predict;
import org.headvances.ml.feature.FeatureDictionary;
import org.headvances.ml.feature.FeatureExtractor;
import org.headvances.ml.feature.FeatureGenerator;
import org.headvances.ml.maxent.FeatureInstanceFactory;
import org.headvances.util.CommandParser;
import org.headvances.util.IOUtil;
import org.headvances.util.JVMInfoUtil;
import org.headvances.util.statistic.StatisticsSet;

import cc.mallet.classify.MaxEnt;
import cc.mallet.types.Instance;
import cc.mallet.types.Labeling;

public class MaxentTextClassifier implements Classifier<TextDocument> {
  private cc.mallet.classify.Classifier maxent ;
  private FeatureInstanceFactory factory  ;
  private FeatureGenerator<TextDocument> featureGenerator ;
  private FeatureExtractor<TextDocument> featureExtractor ;
  
  private double trustThreshold = 0.85;

  public MaxentTextClassifier() throws Exception {
    this("classpath:ml/text/text.maxent.model", "classpath:ml/text/text.maxent.dict") ;
  }

  public MaxentTextClassifier(String modelRes, String dictRes) throws Exception {
    ObjectInputStream s = new ObjectInputStream(IOUtil.loadRes(modelRes));
    init((MaxEnt) s.readObject(), FeatureDictionary.load(IOUtil.loadRes(dictRes)));
  }

  private void init(MaxEnt maxent, FeatureDictionary dict) throws Exception {
    this.maxent = maxent ;
    factory = new FeatureInstanceFactory(maxent.getAlphabet(), maxent.getLabelAlphabet()) ;
    this.featureGenerator = new TextFeatureGenerator();
    featureExtractor = MaxentTextTrainer.createFeatureExtractor(featureGenerator, dict) ; ;
  }

  public void setTrustThreshold(double val) { this.trustThreshold = val ; }

  public boolean isTrusted(Predict predict) {
    return predict.probability > trustThreshold ;
  }

  public Predict predict(TextDocument doc) throws Exception {
    Predict[] predict = classify(doc) ;
    Predict best = Predict.getBestPredict(predict) ;
    return best ;
  }

  public Predict[] classify(TextDocument tdoc) throws Exception {
    Instance instance = factory.createDecodeInstance(featureExtractor.extract(tdoc)) ;
    Labeling labeling = maxent.classify(instance).getLabeling();
    // print the labels with their weights in descending order (ie best first)                     
    Predict[] predict = new Predict[labeling.numLocations()] ;
    for (int rank = 0; rank < labeling.numLocations(); rank++){
      String label = labeling.getLabelAtRank(rank).toString();
      double probability = labeling.getValueAtRank(rank) ;
      predict[rank] = new Predict(label, probability) ;
      //System.out.print(label + ":" + probability + "\n");
    }
    return predict ;
  }

  public void report(StatisticsSet map, TextDocument tdoc) {
  }

  public void run(String[] args) throws Exception{
    CommandParser command = new CommandParser("Classifier:") ;
    command.addMandatoryOption("data", true, "The input labeled data") ;
    if(!command.parse(args)) return ;
    command.printHelp() ;

    String dataDir = command.getOption("data", null) ;  
    
    System.out.println("Start test data!!!");
    MultiTextFileReader reader = new MultiTextFileReader(dataDir);
    CrossValidationReporter reporter = new CrossValidationReporter();
    int count = 0;
    TextDocument doc;
    while ((doc = reader.next()) != null) {
      Predict predict = predict(doc) ;
      String  expectLabel = doc.getLabel();
      reporter.report(predict, expectLabel) ;
      count++ ;
      if(count > 0 && count % 100 == 0) System.out.print('.') ;
    }
    System.out.println() ;
    reporter.getStatisticMap().report(System.out) ;
  }
  
  public static void main(String[] args) throws Exception{
    if(args.length == 0) {
      args = new String[] {
        "-data",   "d:/ml-data/text",
      };
    }
    System.out.println(JVMInfoUtil.getMemoryUsageInfo());
    MaxentTextClassifier classifier = new MaxentTextClassifier();
    classifier.run(args);
  }
}