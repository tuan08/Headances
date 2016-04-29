package org.headvances.ml.classify.xhtml;

import java.io.FileOutputStream;
import java.io.ObjectOutputStream;

import org.headvances.data.Document;
import org.headvances.html.dom.TDocument;
import org.headvances.json.JSONMultiFileReader;
import org.headvances.ml.Trainer;
import org.headvances.ml.classify.xhtml.feature.FeatureCollector;
import org.headvances.ml.feature.FeatureDictionary;
import org.headvances.ml.feature.FeatureExtractor;
import org.headvances.ml.feature.FeatureExtractorDictionary;
import org.headvances.ml.feature.FeatureGenerator;
import org.headvances.ml.maxent.FeatureInstanceFactory;
import org.headvances.util.CommandParser;
import org.headvances.util.JVMInfoUtil;
import org.headvances.util.TimeReporter;

import cc.mallet.classify.MaxEnt;
import cc.mallet.classify.MaxEntTrainer;
import cc.mallet.types.InstanceList;
/**
 * $Author: Tuan Nguyen$
 **/
public class MaxentDocumentTrainer implements Trainer {
	private TimeReporter timeReporter ;
	
	public MaxentDocumentTrainer(TimeReporter timeReporter) {
		this.timeReporter = timeReporter ;
	}
	
  static public FeatureExtractor<TDocument> createFeatureExtractor(FeatureGenerator<TDocument> generator, FeatureDictionary dict) {
    FeatureExtractor<TDocument> featureExtractor = new FeatureExtractorDictionary<TDocument>(generator, dict) ;
    featureExtractor.setUseMapFeatureHolder(true) ;
    return featureExtractor ; 
  }

  public void train(String dataDir, String dictFile, String modelFile) throws Exception {
  	TimeReporter.Time genDictTime = timeReporter.getTime("MaxentTrainer: GenDict") ;
  	genDictTime.start() ;
  	FeatureGenerator<TDocument> generator = new DocumentFeatureGenerator();
    FeatureDictionaryBuilder dictBuilder = new FeatureDictionaryBuilder(generator);
    dictBuilder.setThreshold(0.1);
    dictBuilder.process(dataDir);
    FeatureDictionary dict = dictBuilder.getIDFDictionary();
    dict.save(dictFile);
    genDictTime.stop() ;
    
    TimeReporter.Time trainTime = timeReporter.getTime("MaxentTrainer: Train") ;
    trainTime.start() ;
    FeatureExtractor<TDocument> fextractor = createFeatureExtractor(generator, dict) ;
    FeatureInstanceFactory factory = new FeatureInstanceFactory() ;
    InstanceList trainingData = new InstanceList(factory.getDataAlphabet(), factory.getTargetAlphabet());

    JSONMultiFileReader reader = new JSONMultiFileReader(dataDir);
    Document doc;
    int idx = 0 ;
    while ((doc = reader.next(Document.class)) != null) {
    	if(!FeatureDictionaryBuilder.acceptDocument(doc)) continue ;
      TDocument tdoc = TDocument.create(doc) ;
      String label = FeatureCollector.getLabel(doc);
      trainingData.add(factory.createTrainInstance(fextractor.extract(tdoc), label)) ;
      idx++ ;
      if(idx % 1000 == 0) {
        System.out.println("load " + idx + " documents");
      }
    }

    MaxEntTrainer trainer = new MaxEntTrainer();
    MaxEnt classifier = trainer.train(trainingData);
    ObjectOutputStream out = new ObjectOutputStream(new FileOutputStream (modelFile));
    out.writeObject (classifier);
    out.close();
    trainTime.stop() ;
  }

  static public void main(String[] args) throws Exception {
    if(args.length == 0) {
      args = new String[] {
          "-dict",  "target/maxent.dict.json",
          "-model", "target/maxent.model",
          "-data", "d:/ml-data/xhtml"
      };
    }
    System.out.println(JVMInfoUtil.getMemoryUsageInfo());
    CommandParser command = new CommandParser("content:") ;
    command.addOption("dict", true, "The output dictionary file") ;
    command.addOption("model", true, "The output model file") ;
    command.addMandatoryOption("data", true, "The data file in json format") ;
    if(!command.parse(args)) return ;
    command.printHelp() ;

    String dictFile = command.getOption("dict", "ContentType.dict.json") ;
    String modelFile = command.getOption("model", "ContentType.model") ;
    String dataDir = command.getOption("data", null) ;

    TimeReporter timeReporter = new TimeReporter() ;
    MaxentDocumentTrainer trainer = new MaxentDocumentTrainer(timeReporter) ;    
    trainer.train(dataDir, dictFile, modelFile) ;
    timeReporter.report(System.out) ;
  }
}