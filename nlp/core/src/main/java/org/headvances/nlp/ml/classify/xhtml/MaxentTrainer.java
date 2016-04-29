package org.headvances.nlp.ml.classify.xhtml;

import java.io.FileOutputStream;
import java.io.ObjectOutputStream;

import org.headvances.data.Document;
import org.headvances.html.dom.TDocument;
import org.headvances.json.JSONMultiFileReader;
import org.headvances.nlp.ml.classify.FeatureHolder;
import org.headvances.nlp.ml.classify.FeaturesGenerator;
import org.headvances.nlp.ml.classify.MalletMaxentFeatureInstanceFactory;
import org.headvances.util.CommandParser;
import org.headvances.util.JVMInfoUtil;
import org.headvances.util.TimeReporter;

import cc.mallet.classify.MaxEnt;
import cc.mallet.classify.MaxEntTrainer;
import cc.mallet.types.InstanceList;
/**
 * $Author: Tuan Nguyen$
 **/
public class MaxentTrainer  {
	private TimeReporter timeReporter ;
	private FeaturesGenerator<TDocument> fgenerator ;
	
	public MaxentTrainer() throws Exception {
		timeReporter = new TimeReporter() ;
		fgenerator = new FeaturesGenerator<TDocument>() ;
		fgenerator.add(new URLFeatureGenerator()) ;		
		fgenerator.add(new ShortTextFeatureGenerator()) ;
		fgenerator.add(new DOMFeatureGenerator()) ;
	}
	
  public void train(String dataDir, String modelFile) throws Exception {
    TimeReporter.Time trainTime = timeReporter.getTime("MaxentTrainer: Train") ;
    trainTime.start() ;
    MalletMaxentFeatureInstanceFactory factory = new MalletMaxentFeatureInstanceFactory() ;
    InstanceList trainingData = new InstanceList(factory.getDataAlphabet(), factory.getTargetAlphabet());
    JSONMultiFileReader reader = new JSONMultiFileReader(dataDir);
    Document doc;
    int idx = 0 ;
    while ((doc = reader.next(Document.class)) != null) {
    	if(!acceptDocument(doc)) continue ;
      TDocument tdoc = TDocument.create(doc) ;
      String label = getLabel(doc);
      FeatureHolder fholder = fgenerator.generate(tdoc) ;
      trainingData.add(factory.createTrainInstance(fholder.getFeature(), label)) ;
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

  static public String getLabel(Document doc) {
		String labelName = doc.getLabels()[0];
		labelName = labelName.replace(":detail", "");
		labelName = labelName.replace(":list", "");
		return labelName ;
	}
  
  static public boolean acceptDocument(Document doc) {
  	if(doc.hasLabel("content:error") || doc.getContent().length() < 1000) return false ; 
  	return true ;
  }
  
  static public void main(String[] args) throws Exception {
    if(args.length == 0) {
      args = new String[] {
        "-model", "target/model.maxent",
        "-data", "d:/ml-data/xhtml"
      };
    }
    System.out.println(JVMInfoUtil.getMemoryUsageInfo());
    CommandParser command = new CommandParser("content:") ;
    command.addOption("model", true, "The output model file") ;
    command.addMandatoryOption("data", true, "The data file in json format") ;
    if(!command.parse(args)) return ;
    command.printHelp() ;

    String modelFile = command.getOption("model", "ContentType.model") ;
    String dataDir = command.getOption("data", null) ;

    TimeReporter timeReporter = new TimeReporter() ;
    MaxentTrainer trainer = new MaxentTrainer() ;    
    trainer.train(dataDir, modelFile) ;
    timeReporter.report(System.out) ;
  }
}