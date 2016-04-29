package org.headvances.nlp.ml.classify.xhtml;

import java.util.Random;

import org.headvances.data.Document;
import org.headvances.html.dom.TDocument;
import org.headvances.json.JSONMultiFileReader;
import org.headvances.json.JSONWriter;
import org.headvances.nlp.ml.classify.CrossValidationReporter;
import org.headvances.nlp.ml.classify.Predict;
import org.headvances.util.CommandParser;
import org.headvances.util.FileUtil;
import org.headvances.util.JVMInfoUtil;
import org.headvances.util.TimeReporter;
/**
 * $Author: Tuan Nguyen$ 
 **/
public class CrossValidator {
	private String trainDataFile ;
	private String testDataFile  ;
	private String modelFile ;

	private TimeReporter timeReporter = new TimeReporter();
	
	public void run(String[] args) throws Exception {
	  System.out.println(JVMInfoUtil.getMemoryUsageInfo());

	  CommandParser command = new CommandParser("cross-validator:") ;
		command.addMandatoryOption("data", true, "The input labeled data") ;
		command.addOption("outdir",        true, "The output data directory") ;
		command.addOption("method",        true, "The algorithm:  ann, svm") ;
		if(!command.parse(args)) return ;
		command.printHelp() ;

		String dataDir = command.getOption("data", null) ;  
		String outDir  = command.getOption("outdir", "target/validation") ;
		String method  = command.getOption("method", "ann");

		FileUtil.removeIfExist(outDir) ;
		FileUtil.mkdirs(outDir) ;
		trainDataFile = outDir  + "/train-data.json.gzip" ;
		testDataFile  = outDir  + "/test-data.json.gzip" ;
		modelFile     = outDir  + "/model." +  method  ;

		TimeReporter.Time totalTime = timeReporter.getTime("CrossValidator: Total");
		TimeReporter.Time splitTime = timeReporter.getTime("CrossValidator: Data spliting");
		TimeReporter.Time classifyTime;

		totalTime.start();
		
		splitTime.start();
		split(dataDir) ;
		splitTime.stop();

		train(new MaxentTrainer());

		classifyTime = timeReporter.getTime("MaxentDocumentClassifier: Classify");
		classifyTime.start();

		classify(new MaxEntClassifier("file:" + modelFile));

		classifyTime.stop();
		totalTime.stop();
		timeReporter.report(System.out);
	}

	void split(String dataDir) throws Exception {
		System.out.println("Start split data!!!");
		JSONMultiFileReader reader = new JSONMultiFileReader(dataDir);
		JSONWriter trainDataWriter = new JSONWriter(trainDataFile) ;
		JSONWriter testDataWriter  = new JSONWriter(testDataFile) ;
		Document doc ;
		Random randomGen = new Random() ;
		int count = 0 ;
		while ((doc = reader.next(Document.class)) != null) {
			if(doc.hasLabel("content:error") || doc.getContent().length() < 1000) continue ;
			float random = randomGen.nextFloat() ;
			if(random < 0.2) testDataWriter.write(doc) ;
			else trainDataWriter.write(doc) ;
			count++ ;
			if(count > 0 && count % 1000 == 0) System.out.print('.') ;
		}
		trainDataWriter.close() ;
		testDataWriter.close()  ;
		System.out.println("\nSplit data done!!!");
	}

	void train(MaxentTrainer trainer) throws Exception{
		System.out.println("Start train data!!!");
		trainer.train(trainDataFile, modelFile) ;
		System.out.println("Train data done!!!");
	}

	void classify(MaxEntClassifier classifier) throws Exception {
		System.out.println("Start test data!!!");
		JSONMultiFileReader reader = new JSONMultiFileReader(this.testDataFile);
		CrossValidationReporter reporter = new CrossValidationReporter();
		int count = 0;
		Document doc;
		while ((doc = reader.next(Document.class)) != null) {
			TDocument tdoc = TDocument.create(doc) ;
			Predict predict = classifier.predict(tdoc) ;
			String  expectLabel = MaxentTrainer.getLabel(doc) ;
			reporter.report(predict, expectLabel) ;
			count++ ;
			
			if(count > 0 && count % 100 == 0) System.out.print('.') ;
		}
		System.out.println() ;
		reporter.getStatisticMap().report(System.out) ;
	}
	
	static public void main(String[] args) throws Exception {
		if(args.length == 0) {
			args = new String[] {
			  "-data",   "d:/ml-data/xhtml",
				"-outdir", "target/validation",
				"-method", "maxent"
			};
		}
		CrossValidator validator = new CrossValidator() ;
		validator.run(args) ;
	}
}