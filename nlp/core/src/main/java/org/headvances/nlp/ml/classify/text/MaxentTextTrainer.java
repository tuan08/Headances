package org.headvances.nlp.ml.classify.text;

import java.io.FileOutputStream;
import java.io.ObjectOutputStream;

import org.headvances.nlp.ml.classify.FeatureHolder;
import org.headvances.nlp.ml.classify.FeaturesGenerator;
import org.headvances.nlp.ml.classify.MalletMaxentFeatureInstanceFactory;
import org.headvances.util.CommandParser;
import org.headvances.util.FileUtil;
import org.headvances.util.JVMInfoUtil;
import org.headvances.util.TimeReporter;

import cc.mallet.classify.Classifier;
import cc.mallet.classify.MaxEntTrainer;
import cc.mallet.types.InstanceList;

public class MaxentTextTrainer {
	private TimeReporter timeReporter;
	private FeaturesGenerator<TextDocument> fgenerator ;
	
	public MaxentTextTrainer() throws Exception{
	  this.timeReporter = new TimeReporter();
	  fgenerator = new FeaturesGenerator<TextDocument>() ;
		fgenerator.add(new TextFeatureGenerator()) ;		
	}
	
  public void train(String dataDir, String modelFile) throws Exception {
    TimeReporter.Time trainTime = timeReporter.getTime("MaxenTextTrainer: Train");
    trainTime.start();
    MalletMaxentFeatureInstanceFactory factory = new MalletMaxentFeatureInstanceFactory() ;
    InstanceList trainingData = new InstanceList(factory.getDataAlphabet(), factory.getTargetAlphabet());
    
    MultiTextFileReader reader = new MultiTextFileReader(dataDir);
    TextDocument doc;
    int idx = 0 ;
    while ((doc = reader.next()) != null) {
      String label = doc.getLabel();
      FeatureHolder fholder = fgenerator.generate(doc) ;
      trainingData.add(factory.createTrainInstance(fholder.getFeature(), label)) ;
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
    
    String modelFile     = outDir  + "/model.maxent" ;
    train(dataDir, modelFile);
  }
  
  public static void main(String[] args) throws Exception{
    if(args.length == 0) {
      args = new String[] {
        "-data",   "d:/ml-data/text",
        "-outdir", "target/text",
      };
    }
    System.out.println(JVMInfoUtil.getMemoryUsageInfo());
    MaxentTextTrainer trainer = new MaxentTextTrainer();
    trainer.run(args);
  }
}