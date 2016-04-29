package org.headvances.ml.classify.text;

import java.util.Vector;

import libsvm.svm_node;

import org.headvances.ml.Trainer;
import org.headvances.ml.feature.Feature;
import org.headvances.ml.feature.FeatureDictionary;
import org.headvances.ml.feature.FeatureExtractor;
import org.headvances.ml.feature.FeatureGenerator;
import org.headvances.ml.feature.Labels;
import org.headvances.ml.svm.SVMNodeBuilder;
import org.headvances.ml.svm.SVMTrainer;
import org.headvances.util.TimeReporter;

public class SVMTextTrainer implements Trainer {
  private double topFeatures = 0.1 ;
  private TimeReporter timeReporter;
  
  public SVMTextTrainer(TimeReporter timeReporter){
    this.timeReporter = timeReporter;
  }
  
  public void train(String dataDir, String dictFile, String modelFile) throws Exception {
    TimeReporter.Time genDictTime = timeReporter.getTime("SVMTextTrainer: GenDict");
    genDictTime.start();
    FeatureGenerator<TextDocument> generator = new TextFeatureGenerator();
    TextFeatureDictionaryBuilder dictBuilder = new TextFeatureDictionaryBuilder(generator);
    dictBuilder.setThreashold(topFeatures);
    dictBuilder.process(dataDir);
    FeatureDictionary dictionary = dictBuilder.getDictionary();
    dictionary.save(dictFile);
    genDictTime.stop();
    
    TimeReporter.Time trainTime = timeReporter.getTime("SVMTextTrainer: Train");
    trainTime.start();
    Labels labels = dictionary.getLabels() ;
    Vector<Double>     vy = new Vector<Double>();
    Vector<svm_node[]> vx = new Vector<svm_node[]>();

    FeatureExtractor<TextDocument> fextractor = new FeatureExtractor<TextDocument>(generator) ;
    SVMNodeBuilder svmNodeBuilder = new SVMNodeBuilder(dictionary) ;
    MultiTextFileReader reader = new MultiTextFileReader(dataDir);
    TextDocument doc;
    while ((doc = reader.next()) != null) {
      if(doc.getContent().length() < 100) continue;
      
      String label = doc.getLabel();
      int labelId = labels.getLabelId(label) ;
      Feature[] feature = fextractor.extract(doc) ;
      vx.add(svmNodeBuilder.getSVMVector(feature));
      vy.add((double) labelId);
    }

    SVMTrainer svm = new SVMTrainer(vx, vy, dictionary.getFeatures().length);
    svm.train(modelFile);
    trainTime.stop();
  }
}