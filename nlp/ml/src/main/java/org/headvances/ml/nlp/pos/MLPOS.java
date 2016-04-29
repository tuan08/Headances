package org.headvances.ml.nlp.pos;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

import org.headvances.ml.nlp.ML;
import org.headvances.ml.nlp.MalletML;
import org.headvances.ml.nlp.OpenNLPML;
import org.headvances.ml.nlp.feature.CapLetterFeatureGenerator;
import org.headvances.ml.nlp.feature.TokenTypeFeatureGenerator;
import org.headvances.ml.nlp.feature.TokenFeaturesGenerator;
import org.headvances.nlp.NLPResource;
import org.headvances.nlp.dict.Dictionary;
import org.headvances.nlp.dict.DictionaryTaggingAnalyzer;
import org.headvances.nlp.token.analyzer.CommonTokenAnalyzer;
import org.headvances.nlp.token.analyzer.TokenAnalyzer;
import org.headvances.nlp.wtag.WTagDocumentSet;
import org.headvances.nlp.wtag.WTagDocumentReader;
import org.headvances.util.CommandParser;
import org.headvances.util.TimeReporter;
/**
 * $Author: Tuan Nguyen$ 
 **/
public class MLPOS {

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

    Dictionary dict = NLPResource.getInstance().getDictionary(Dictionary.DICT_RES) ;
    TokenAnalyzer[] tokenAnalyzers = new TokenAnalyzer[] {
      new DictionaryTaggingAnalyzer(dict), new CommonTokenAnalyzer() 
    };
    reader.setTokenAnalyzer(tokenAnalyzers) ;

    TokenFeaturesGenerator featuresGenerator = new TokenFeaturesGenerator(reader) ;
    featuresGenerator.add(new TokenTypeFeatureGenerator()) ;
    featuresGenerator.add(new POSDictFeatureGenerator()) ;
    featuresGenerator.add(new CapLetterFeatureGenerator()) ;
    featuresGenerator.setTargetFeatureGenerator(new POSTargetFeatureGenerator()) ;
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
          "-sample", "d:/ml-data/pos",
          "-model",  "pos.crf",
          "-lib",  "",
          "-train",  "train"
      };
    }
    CommandParser command = new CommandParser("POS:") ;
    command.addMandatoryOption("sample", true, "The sample file or directory, the file extension should be iob2 or tagged") ;
    command.addMandatoryOption("model", true, "The output model file") ;
    command.addOption("lib", true, "Use Mallet-CRF or OpenNLP-Maxent library") ;
    command.addOption("iteration", true, "The maximum frequency of iteration, default is 300") ;
    command.addOption("train", false, "Turn on the trainning mode");
    command.addOption("test", false, "Turn on the test mode");
    if(!command.parse(args)) return ;
    command.printHelp() ;

    String lib = command.getOption("lib", "opennlp") ;
    String sample = command.getOption("sample", null) ;
    String model   = command.getOption("model", null) ;
    int iteration = command.getOptionAsInt("iteration", 500);

    TimeReporter timeReporter = new TimeReporter();
    String[] files = MLPOS.getFiles(sample);

    ML mlpos = null;

    if(lib.equals("opennlp")){
      if(command.hasOption("test")){ mlpos = new OpennlpImpl(model, true) ; } 
      else mlpos = new OpennlpImpl(model, false);
    } else {
      if(command.hasOption("test")){ mlpos = new MalletImpl(model, true); }
      else mlpos = new MalletImpl(model, false);
    }

    mlpos.setTimeReporter(timeReporter);

    if(command.hasOption("train")){ mlpos.train(files, model, iteration); } 
    else if(command.hasOption("test")){ mlpos.test(files, false); } 
    else { mlpos.crossValidation(sample, 0.2, iteration) ;} 

    timeReporter.report(System.out);
  }
}
