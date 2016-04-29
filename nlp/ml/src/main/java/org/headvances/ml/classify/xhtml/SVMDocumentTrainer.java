package org.headvances.ml.classify.xhtml;

import java.util.Vector;

import libsvm.svm_node;

import org.headvances.data.Document;
import org.headvances.html.dom.TDocument;
import org.headvances.json.JSONMultiFileReader;
import org.headvances.ml.Trainer;
import org.headvances.ml.classify.xhtml.feature.FeatureCollector;
import org.headvances.ml.feature.Feature;
import org.headvances.ml.feature.FeatureDictionary;
import org.headvances.ml.feature.FeatureExtractor;
import org.headvances.ml.feature.FeatureGenerator;
import org.headvances.ml.svm.SVMNodeBuilder;
import org.headvances.ml.svm.SVMTrainer;
import org.headvances.util.CommandParser;
import org.headvances.util.TimeReporter;
/**
 * $Author: Tuan Nguyen$
 **/
public class SVMDocumentTrainer implements Trainer {
  private double topFeatures = 0.1 ;
  private TimeReporter timeReporter ;

  public SVMDocumentTrainer(TimeReporter timeReporter){
    this.timeReporter = timeReporter;
  }

  public void train(String dataDir, String dictFile, String modelFile) throws Exception {
    TimeReporter.Time genDictTime = timeReporter.getTime("SVMDocumentTrainner: GenDict");
    genDictTime.start();
    FeatureGenerator<TDocument> generator = new DocumentFeatureGenerator(); 
    FeatureDictionaryBuilder dictBuilder = new FeatureDictionaryBuilder(generator);
    dictBuilder.setThreshold(topFeatures);
    dictBuilder.process(dataDir);
    FeatureDictionary dictionary = dictBuilder.getChisquareDictionary();
    dictionary.save(dictFile);
    genDictTime.stop();
    
    TimeReporter.Time trainTime = timeReporter.getTime("SVMDocumentTrainner: Train");
    trainTime.start();
    String[] labels = dictionary.getLabels().getLabels();
    
    Vector<Double>     vy = new Vector<Double>();
    Vector<svm_node[]> vx = new Vector<svm_node[]>();

    FeatureExtractor<TDocument> fextractor = new FeatureExtractor<TDocument>(generator) ;
    SVMNodeBuilder svmNodeBuilder = new SVMNodeBuilder(dictionary) ;
    JSONMultiFileReader reader = new JSONMultiFileReader(dataDir);
    Document doc;
    while ((doc = reader.next(Document.class)) != null) {
      if(doc.hasLabel("content:error") || doc.getContent().length() < 1000) { 
        continue ; 
      }
      String label = FeatureCollector.getLabel(doc);
      int tagId = -1;
      for(int i = 0; i < labels.length; i++) {
        if(label.equals(labels[i])) {
          tagId = i;
          break ;
        }
      }      
      if(tagId < 0) continue ;
      TDocument tdoc = TDocument.create(doc) ;
      Feature[] feature = fextractor.extract(tdoc) ;
      vx.add(svmNodeBuilder.getSVMVector(feature));
      vy.add((double) tagId);
    }

    SVMTrainer svm = new SVMTrainer(vx, vy, dictionary.getFeatures().length);
    svm.train(modelFile);
    trainTime.stop();
  }

  static public void main(String[] args) throws Exception {
    if(args.length == 0) {
      args = new String[] {
          "-dict", "target/ContentType.dict.json",
          "-model", "target/ContentType.model",
          "-data", "D:/headvances/data/new_merged_all_sorted"
      };
    }
    CommandParser command = new CommandParser("content:") ;
    command.addOption("dict", true, "The output dictionary file") ;
    command.addOption("model", true, "The output model file") ;
    command.addMandatoryOption("data", true, "The data file in json format") ;
    if(!command.parse(args)) return ;
    command.printHelp() ;

    String dictFile = command.getOption("dict", "ContentType.dict.json") ;
    String modelFile = command.getOption("model", "ContentType.model") ;
    String dataDir = command.getOption("data", null) ;

    TimeReporter timeReporter = new TimeReporter();
    SVMDocumentTrainer trainer = new SVMDocumentTrainer(timeReporter) ;    
    trainer.train(dataDir, dictFile, modelFile) ;
  }
}