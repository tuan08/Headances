package org.headvances.ml.classify.xhtml;

import java.util.Random;

import org.headvances.data.Document;
import org.headvances.html.dom.TDocument;
import org.headvances.json.JSONMultiFileReader;
import org.headvances.json.JSONWriter;
import org.headvances.ml.Classifier;
import org.headvances.ml.CrossValidationReporter;
import org.headvances.ml.Predict;
import org.headvances.ml.Trainer;
import org.headvances.ml.classify.xhtml.feature.FeatureCollector;
import org.headvances.util.CommandParser;
import org.headvances.util.FileUtil;
import org.headvances.util.JVMInfoUtil;
import org.headvances.util.TimeReporter;
/**
 * $Author: Tuan Nguyen$ 
 **/
public class DocumentCrossValidator {
	private String trainDataFile ;
	private String testDataFile  ;
	private String dictFile, modelFile ;

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
		dictFile      = outDir  + "/ContentTypeDictionary." + method + ".json";
		modelFile     = outDir  + "/ContentType." +  method + ".model" ;

		TimeReporter.Time totalTime = timeReporter.getTime("CrossValidator: Total");
		TimeReporter.Time splitTime = timeReporter.getTime("CrossValidator: Data spliting");
		TimeReporter.Time classifyTime;

		totalTime.start();
		
		splitTime.start();
		split(dataDir) ;
		splitTime.stop();
		
		if("svm".equals(method)){
			train(new SVMDocumentTrainer(timeReporter));
		
			classifyTime = timeReporter.getTime("SVMDocumentClassifier: Classify");
			classifyTime.start();
			
			classify(new SVMDocumentClassifier("file:" + modelFile, "file:" + dictFile));
		
			classifyTime.stop();
		} else if("maxent".equals(method)){
			train(new MaxentDocumentTrainer(timeReporter));

			classifyTime = timeReporter.getTime("MaxentDocumentClassifier: Classify");
			classifyTime.start();
			
			classify(new MaxEntDocumentClassifier("file:" + modelFile, "file:" + dictFile));
			
			classifyTime.stop();
		} else {
			throw new Exception("Unknown algorithm " + method) ;
		}
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
			if(count % 5 == 0) random = 0.1f ;
			else random = 1f ;
			if(random < 0.2) testDataWriter.write(doc) ;
			else trainDataWriter.write(doc) ;
			count++ ;
			if(count > 0 && count % 1000 == 0) System.out.print('.') ;
		}
		trainDataWriter.close() ;
		testDataWriter.close()  ;
		System.out.println("\nSplit data done!!!");
	}

	void train(Trainer trainer) throws Exception{
		System.out.println("Start train data!!!");
		trainer.train(trainDataFile, dictFile, modelFile) ;
		System.out.println("Train data done!!!");
	}

	void classify(Classifier<TDocument> classifier) throws Exception {
		System.out.println("Start test data!!!");
		JSONMultiFileReader reader = new JSONMultiFileReader(this.testDataFile);
		CrossValidationReporter reporter = new CrossValidationReporter();
		int count = 0;
		Document doc;
		while ((doc = reader.next(Document.class)) != null) {
			TDocument tdoc = TDocument.create(doc) ;
			Predict predict = classifier.predict(tdoc) ;
			String  expectLabel = FeatureCollector.getLabel(doc) ;
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
		DocumentCrossValidator validator = new DocumentCrossValidator() ;
		validator.run(args) ;
	}
}