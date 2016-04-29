package org.headvances.ml.nlp.opinion;

import java.io.FileOutputStream;
import java.io.ObjectInputStream;
import java.io.OutputStreamWriter;
import java.io.Writer;
import java.util.List;

import org.headvances.ml.Predict;
import org.headvances.ml.feature.FeatureDictionary;
import org.headvances.ml.feature.FeatureExtractor;
import org.headvances.ml.maxent.FeatureInstanceFactory;
import org.headvances.util.CommandParser;
import org.headvances.util.FileUtil;
import org.headvances.util.IOUtil;
import org.headvances.util.JVMInfoUtil;

import cc.mallet.classify.MaxEnt;
import cc.mallet.types.Instance;
import cc.mallet.types.Labeling;

public class OpinionMaxentClassifier {
	private String[] keyword ;
  private cc.mallet.classify.Classifier maxent ;
  private FeatureInstanceFactory factory  ;
  private FeatureExtractor<Opinion> featureExtractor ;
  
  private double trustThreshold = 0.8;

  public OpinionMaxentClassifier(String[] keyword) throws Exception {
    this(keyword, "classpath:ml/opinion/opinion.maxent.model", "classpath:ml/opinion/opinion.maxent.dict") ;
    //this("file:target/opinion/opinion.maxent.model", "file:target/opinion/opinion.maxent.dict") ;
  }

  public OpinionMaxentClassifier(String[] keyword, String modelRes, String dictRes) throws Exception {
    this.keyword = keyword ;
  	ObjectInputStream s = new ObjectInputStream(IOUtil.loadRes(modelRes));
    init((MaxEnt) s.readObject(), FeatureDictionary.load(IOUtil.loadRes(dictRes)));
  }

  private void init(MaxEnt maxent, FeatureDictionary dict) throws Exception {
    this.maxent = maxent ;
    factory = new FeatureInstanceFactory(maxent.getAlphabet(), maxent.getLabelAlphabet()) ;
    featureExtractor = OpinionMaxentTrainer.createFeatureExtractor(dict, keyword);
  }

  public void setTrustThreshold(double val) { this.trustThreshold = val ; }

  public boolean isTrusted(Predict predict) {
    return predict.probability > trustThreshold ;
  }
  
  public Predict predict(Opinion op) throws Exception {
    Predict[] predict = classify(op) ;
    Predict best = Predict.getBestPredict(predict) ;
    
    return best ;
  }

  public Predict[] classify(Opinion op) throws Exception {
    Instance instance = factory.createDecodeInstance(featureExtractor.extract(op)) ;
    Labeling labeling = maxent.classify(instance).getLabeling();
    Predict[] predict = new Predict[labeling.numLocations()] ;
    for (int rank = 0; rank < labeling.numLocations(); rank++){
      String label = labeling.getLabelAtRank(rank).toString();
      double probability = labeling.getValueAtRank(rank) ;
      predict[rank] = new Predict(label, probability) ;
    }
    return predict ;
  }
  
  public void predict(List<Opinion> ops, String outFile) throws Exception{
  	Writer writer = new OutputStreamWriter(new FileOutputStream(outFile), "UTF-8");
  	for(Opinion op: ops){
      Predict predict = predict(op);
      writer.append(predict.label).append('\t').append(op.getOpinion()).append('\n');
  	}
  	writer.close() ;
  	System.out.println("Predict " + ops.size());
  }
  
  public void predict(String outDir, List<Opinion> ops) throws Exception {
  	OpinionWriter owriter = new OpinionWriter(outDir) ;
  	FileUtil.mkdirs(outDir) ;
    int count = 0;
    for(Opinion op: ops) {
      Predict predict = predict(op);
      String targetLabel = op.getLabel();
    	if(targetLabel.equals(predict.label)) {
    		count++ ;
    		owriter.write("good", op) ;
    	} else {
    		owriter.write("false", op) ;
    	}
    	owriter.write("label", op) ;
    }
    owriter.closeWriter() ;
    double accurancy = (double) count/ops.size();
    System.out.println("\n >> Accurancy: " + accurancy);
  }
  
  public void review(List<Opinion> ops, String outDir) throws Exception {
  	FileUtil.mkdirs(outDir) ;
  	Writer goodWriter = new OutputStreamWriter(new FileOutputStream(outDir + "/good.txt"), "UTF-8");
  	Writer reviewWriter = new OutputStreamWriter(new FileOutputStream(outDir + "/review.txt"), "UTF-8");
  	int goodCount = 0 ;
  	for(Opinion op: ops){
  		Predict predict = predict(op);
      String targetLabel = op.getLabel();
    	if(targetLabel.equals(predict.label)) {
    		goodWriter.append(predict.label).append("\t").append(op.getOpinion()).append("\n") ;
    		goodCount++ ;
    	} else {
    		reviewWriter.
    		  append(predict.label).append("/").
    		  append(targetLabel).append("\t").append(op.getOpinion()).append("\n") ;
    	}
  	}
  	goodWriter.close() ;
  	reviewWriter.close() ;
  	System.out.println("Predict " + ops.size() + ", Accuracy: " + ((double)goodCount/ops.size()));
  }
  
  public static void main(String[] args) throws Exception{
    if(args.length == 0) {
      args = new String[] {
          "-sample",   "file:src/data/opinion/neg_review.txt",
          "-dict",  "file:target/opinion/opinion.maxent.dict",
          "-model", "file:target/opinion/opinion.maxent.model",
      };
    }
    CommandParser command = new CommandParser("Classifier:") ;
    command.addMandatoryOption("sample", true, "The sample labeled data") ;
    command.addOption("dict",        true, "The dictionary file") ;
    command.addOption("model",        true, "The model file") ;
    if(!command.parse(args)) return ;
    command.printHelp() ;

    String sample = command.getOption("sample", null) ;  
    String dict = command.getOption("dict", null) ;
    String model = command.getOption("model", null) ;
    
    System.out.println(JVMInfoUtil.getMemoryUsageInfo());
    String[] keyword = {"product"} ;
    OpinionMaxentClassifier classifier = new OpinionMaxentClassifier(keyword, model, dict);
    List<Opinion> ops = MLUtil.readSampleOpinion(keyword, sample) ;
//    classifier.predict("target/predict", ops) ;
    classifier.review(ops, "target/review") ;
    //classifier.predict(ops, "target/todo.txt") ;
  }
}