package org.headvances.nlp.ml.token;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

import org.headvances.nlp.ml.token.MLTester;
import org.headvances.nlp.ml.token.MLTrainer;
import org.headvances.nlp.ml.token.MalletCRFTester;
import org.headvances.nlp.ml.token.MalletCRFTrainer;
import org.headvances.nlp.ml.token.OpenNLPTester;
import org.headvances.nlp.ml.token.OpenNLPTrainer;
import org.headvances.nlp.ml.token.TokenFeaturesGenerator;
import org.headvances.nlp.wtag.WTagDocumentSet;
import org.headvances.util.CommandParser;
/**
 * $Author: Tuan Nguyen$ 
 **/
public class ML {
	private String lib = "crf" ;
	private TokenFeaturesGenerator generator ;

	public ML() {} 

	public ML(String lib, TokenFeaturesGenerator generator) {
		this.lib = lib ;
		this.generator = generator ;
	}

	public void train(String[] trainFiles, String modelFile, int iter) throws Exception {
		MLTrainer trainer = null ;
		if("opennlp".equals(lib)) trainer = new OpenNLPTrainer(generator) ;
		else trainer = new MalletCRFTrainer(generator) ;

		trainer.train(trainFiles, modelFile, iter) ;
	}

	public void test(String[] testFiles, String modelFile) throws Exception {
		MLTester tester = null; 
		if("opennlp".equals(lib)) tester = new OpenNLPTester(generator, modelFile) ;
		else tester = new MalletCRFTester(generator, modelFile) ;
		for(String testFile : testFiles) {
			tester.test(testFile) ;
		}
	}

	public void crossValidation(String[] files, String modelFile, int iter) throws Exception {
		List<String> trainFiles = new ArrayList<String>() ;
		List<String> testFiles = new ArrayList<String>() ;
		for(String sel : files) {
			if(Math.random() > 0.8) trainFiles.add(sel) ;
			else testFiles.add(sel) ; 
		}
		train(trainFiles.toArray(new String[trainFiles.size()]), modelFile, iter) ;
		test(testFiles.toArray(new String[testFiles.size()]), modelFile) ;
	}

	static public void run(String[] args, TokenFeaturesGenerator generator) throws Exception {
		CommandParser command = new CommandParser("ml:") ;
		command.addMandatoryOption("data", true, "The taggeg file or directory, the file extension should be tagged") ;
		command.addMandatoryOption("model", true, "The output model file") ;
		command.addOption("lib", true, "Use crf or opennlp") ;
		command.addOption("iteration", true, "The maximum frequency of iteration, default is 300") ;

		command.addOption("train", false, "Turn on the trainning mode");
		command.addOption("test", false, "Turn on the test mode");
		command.addOption("cross", false, "Turn on the cross validation mode");
		if(!command.parse(args)) return ;
		command.printHelp() ;

		String lib = command.getOption("lib", "crf") ;

		String data = command.getOption("data", null) ;
		String model   = command.getOption("model", null) ;
		int iteration = command.getOptionAsInt("iteration", 300);

		String[] files = getFiles(data);
		ML mlpos = new ML(lib, generator) ;
		if(command.hasOption("train")) {
			System.out.println("Run train mode............................");
			mlpos.train(files, model, iteration) ;
		} else if(command.hasOption("test")) {
			System.out.println("Run test mode.............................");
			mlpos.test(files, model) ;
		} else {
			System.out.println("Run cross validation mode.................");
			mlpos.crossValidation(files, model, iteration) ;
		}
	}

	static public String[] getFiles(String sample) throws Exception{
		if(new File(sample).isDirectory()) {
			WTagDocumentSet set = new WTagDocumentSet(sample, ".*\\.(tagged|wtag)") ;
			List<String> dataFiles = new ArrayList<String>() ;
			String[] files = set.getFiles() ;
			for(int i = 0; i < files.length; i++) { dataFiles.add(files[i]); }

			return dataFiles.toArray(new String[dataFiles.size()]);
		} else { return new String[]{sample}; }
	}
}