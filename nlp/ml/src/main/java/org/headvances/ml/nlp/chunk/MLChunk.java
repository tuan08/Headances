package org.headvances.ml.nlp.chunk;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

import org.headvances.ml.nlp.ML;
import org.headvances.ml.nlp.MalletML;
import org.headvances.ml.nlp.OpenNLPML;
import org.headvances.ml.nlp.feature.TokenFeaturesGenerator;
import org.headvances.nlp.token.analyzer.CommonTokenAnalyzer;
import org.headvances.nlp.token.analyzer.TokenAnalyzer;
import org.headvances.nlp.wtag.WTagDocumentReader;
import org.headvances.nlp.wtag.WTagDocumentSet;
import org.headvances.util.CommandParser;
import org.headvances.util.TimeReporter;
/**
 * $Author: Tuan Nguyen$ 
 **/
public class MLChunk {

  static public class MalletImpl extends MalletML {
    public MalletImpl(String model, boolean loadModel) throws Exception {
      super(model, loadModel);
    }

    protected TokenFeaturesGenerator createTokenFeaturesGenerator() throws Exception {
      return newTokenFeaturesGenerator() ;
    }
  }

  static public class OpennlpImpl extends OpenNLPML {
    public OpennlpImpl(String modelRes, boolean loadModel) throws Exception {
      super(modelRes, loadModel);
    }

    protected TokenFeaturesGenerator createTokenFeaturesGenerator() throws Exception {
      return newTokenFeaturesGenerator();
    }
  }

  protected TokenFeaturesGenerator createTokenFeaturesGenerator() throws Exception {
    return newTokenFeaturesGenerator() ;
  }

  static public TokenFeaturesGenerator newTokenFeaturesGenerator() throws Exception {
    WTagDocumentReader reader = new WTagDocumentReader() ;
    
    TokenAnalyzer[] tokenAnalyzers = new TokenAnalyzer[] {
        new CommonTokenAnalyzer() 
    };
    reader.setTokenAnalyzer(tokenAnalyzers) ;
    
    TokenFeaturesGenerator featuresGenerator = new TokenFeaturesGenerator(reader) ;
    featuresGenerator.add(new ChunkFeatureGenerator()) ;
    featuresGenerator.setTargetFeatureGenerator(new ChunkTargetFeatureGenerator()) ;
    return featuresGenerator ;
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

  static public void main(String[] args) throws Exception {
    if(args == null || args.length == 0) { 
      args = new String[] {
          "-sample", "d:/ml-data/chunk",
          "-model",  "target/chunk.opennlp.maxent",
          "-lib",  "opennlp",
          "-train",  "train"
      };
    }
    CommandParser command = new CommandParser("Phrase Chunking:") ;
    command.addMandatoryOption("sample", true, "The sample file or directory, the file extension should be iob2 or tagged") ;
    command.addMandatoryOption("model", true, "The output model file") ;
    command.addOption("lib", true, "Use Mallet-CRF or OpenNLP-Maxent library") ;
    command.addOption("iteration", true, "The maximum number of iteration, default is 300") ;
    command.addOption("train", false, "Turn on the trainning mode");
    command.addOption("test", false, "Turn on the test mode");
    if(!command.parse(args)) return ;
    command.printHelp() ;

    String lib = command.getOption("lib", "opennlp") ;
    String sample = command.getOption("sample", null) ;
    String model   = command.getOption("model", null) ;
    int iteration = command.getOptionAsInt("iteration", 2000);

    TimeReporter timeReporter = new TimeReporter();
    String[] files = MLChunk.getFiles(sample);

    ML mlchunk = null;

    if(lib.equals("opennlp")){
      if(command.hasOption("test")){ mlchunk = new OpennlpImpl(model, true) ; } 
      else mlchunk = new OpennlpImpl(model, false);
    } else {
      if(command.hasOption("test")){ mlchunk = new MalletImpl(model, true); }
      else mlchunk = new MalletImpl(model, false);
    }

    mlchunk.setTimeReporter(timeReporter);

    if(command.hasOption("train")){ mlchunk.train(files, model, iteration); } 
    else if(command.hasOption("test")){ mlchunk.test(files, false); } 
    else { mlchunk.crossValidation(sample, 0.2, iteration) ;} 

    timeReporter.report(System.out);
  }
}
