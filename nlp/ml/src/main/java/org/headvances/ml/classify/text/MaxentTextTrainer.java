package org.headvances.ml.classify.text;

import java.io.FileOutputStream;
import java.io.ObjectOutputStream;

import org.headvances.ml.Trainer;
import org.headvances.ml.feature.Feature;
import org.headvances.ml.feature.FeatureDictionary;
import org.headvances.ml.feature.FeatureExtractor;
import org.headvances.ml.feature.FeatureExtractorDictionary;
import org.headvances.ml.feature.FeatureGenerator;
import org.headvances.ml.maxent.FeatureInstanceFactory;
import org.headvances.util.CommandParser;
import org.headvances.util.FileUtil;
import org.headvances.util.JVMInfoUtil;
import org.headvances.util.TimeReporter;

import cc.mallet.classify.Classifier;
import cc.mallet.classify.MaxEntTrainer;
import cc.mallet.types.InstanceList;

public class MaxentTextTrainer implements Trainer {
	private TimeReporter timeReporter;
	
	public MaxentTextTrainer(TimeReporter timeReporter){
	  this.timeReporter = timeReporter;
	}
	
  static public FeatureExtractor<TextDocument> createFeatureExtractor(FeatureGenerator<TextDocument> generator, FeatureDictionary dict) {
		FeatureExtractor<TextDocument> featureExtractor = 
    	new FeatureExtractorDictionary<TextDocument>(generator, dict) ;
		featureExtractor.setUseMapFeatureHolder(true) ;
		return featureExtractor ; 
	}
	
  public void train(String dataDir, String dictFile, String modelFile) throws Exception {
    TimeReporter.Time genDictTime = timeReporter.getTime("MaxentTextTrainer: GenDict");
    genDictTime.start();
    FeatureGenerator<TextDocument> generator = new TextFeatureGenerator();
    TextFeatureDictionaryBuilder dictBuilder = new TextFeatureDictionaryBuilder(generator);
    dictBuilder.process(dataDir);
    FeatureDictionary dict = dictBuilder.getIDFDictionary();
    dict.save(dictFile);
    genDictTime.stop();
    
    TimeReporter.Time trainTime = timeReporter.getTime("MaxenTextTrainer: Train");
    trainTime.start();
    FeatureExtractor<TextDocument> featureExtractor = createFeatureExtractor(generator, dict) ;
    FeatureInstanceFactory factory = new FeatureInstanceFactory() ;
    InstanceList trainingData = new InstanceList(factory.getDataAlphabet(), factory.getTargetAlphabet());
    
    MultiTextFileReader reader = new MultiTextFileReader(dataDir);
    TextDocument doc;
    int idx = 0 ;
    while ((doc = reader.next()) != null) {
      String label = doc.getLabel();
      Feature[] features = featureExtractor.extract(doc);
      trainingData.add(factory.createTrainInstance(features, label)) ;
      idx++ ;
      if(idx % 1000 == 0) {
        System.out.println("load " + idx + " documents");
      }
    }

    MaxEntTrainer trainer = new MaxEntTrainer();
    Classifier classifier = trainer.train(trainingData);
    
    ObjectOutputStream out = new ObjectOutputStream(new FileOutputStream (modelFile));
    out.writeObject (classifier);
    out.close();
    
    trainTime.stop();
  }
  
  public void run(String[] args) throws Exception{
    CommandParser command = new CommandParser("Trainer:") ;
    command.addMandatoryOption("data", true, "The input labeled data") ;
    command.addOption("outdir",        true, "The output data directory") ;
    if(!command.parse(args)) return ;
    command.printHelp() ;

    String dataDir = command.getOption("data", null) ;  
    String outDir = command.getOption("outdir", "target/text") ;
    
    FileUtil.removeIfExist(outDir) ;
    FileUtil.mkdirs(outDir) ;
    
    String dictFile      = outDir  + "/text.features.dict" ;
    String modelFile     = outDir  + "/text.features.model" ;
    
    train(dataDir, dictFile, modelFile);
  }
  
  public static void main(String[] args) throws Exception{
    if(args.length == 0) {
      args = new String[] {
        "-data",   "d:/ml-data/text",
        "-outdir", "target/text",
      };
    }
    System.out.println(JVMInfoUtil.getMemoryUsageInfo());
    
    TimeReporter timeReporter = new TimeReporter();
    MaxentTextTrainer trainer = new MaxentTextTrainer(timeReporter);
    trainer.run(args);
  }
}