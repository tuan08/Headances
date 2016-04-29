package org.headvances.ml.nlp.ent;

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
import org.headvances.nlp.token.analyzer.TokenAnalyzer;
import org.headvances.nlp.wtag.WTagDocumentSet;
import org.headvances.nlp.wtag.WTagDocumentReader;
import org.headvances.util.CommandParser;
import org.headvances.util.TimeReporter;
/**
 * $Author: Tuan Nguyen$ 
 **/
public class MLENT {
  static public String configName ;

  static public class MalletImpl extends MalletML {
    public MalletImpl(String model, String _configName, boolean loadModel) throws Exception{
      configName = _configName;
      init(model, loadModel);
    }

    protected TokenFeaturesGenerator createTokenFeaturesGenerator() throws Exception {
      EntitySetConfig config = EntitySetConfig.getConfig(configName) ;
      return MLENT.newTokenFeaturesGenerator(config) ;
    }
  }

  static public class OpennlpImpl extends OpenNLPML {
    public OpennlpImpl(String modelRes, String _configName, boolean loadModel) throws Exception {
      configName = _configName;
      init(modelRes, loadModel);
    }

    protected TokenFeaturesGenerator createTokenFeaturesGenerator() throws Exception {
      EntitySetConfig config = EntitySetConfig.getConfig(configName) ;
      return MLENT.newTokenFeaturesGenerator(config) ;
    }
  }

  static public TokenFeaturesGenerator createTokenFeaturesGenerator(EntitySetConfig _config)throws Exception {
    return newTokenFeaturesGenerator(_config) ;
  }

  static public TokenFeaturesGenerator newTokenFeaturesGenerator(EntitySetConfig config) throws Exception {
    Dictionary dict = NLPResource.getInstance().getDictionary(Dictionary.DICT_RES) ;
    WTagDocumentReader reader = new WTagDocumentReader() ;
    TokenAnalyzer posAnalyzer = TokenAnalyzer.NONE ;
    TokenAnalyzer[] tokenAnalyzers = new TokenAnalyzer[] {
      new DictionaryTaggingAnalyzer(dict), posAnalyzer
    };
    reader.setTokenAnalyzer(tokenAnalyzers) ;

    TokenFeaturesGenerator featuresGenerator = new TokenFeaturesGenerator(reader) ;
    featuresGenerator.add(new TokenTypeFeatureGenerator()) ;
    featuresGenerator.add(new CapLetterFeatureGenerator()) ;
    featuresGenerator.add(new MeaningFeatureGenerator(dict, config)) ;
    featuresGenerator.setTargetFeatureGenerator(new EntityTargetFeatureGenerator(config)) ;
    return featuresGenerator ;
  }

  public void setConfigName(String _configName){ configName = _configName; }
  public String getConfigName(String configName){ return configName; }
  
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
          "-sample", "d:/ml-data/wtag",
          "-model",  "target/ml/entity.opennlp.maxent",
          "-config", "all",
          "-train", ""
      };
    }
    CommandParser command = new CommandParser("Entity recognition:") ;
    command.addMandatoryOption("sample", true, "The sampple file or directory, the file extension should be iob2 or tagged") ;
    command.addMandatoryOption("model", true, "The output model file") ;
    command.addOption("lib", true, "Use Mallet-CRF or OpenNLP-Maxent library") ;
    command.addOption("iteration", true, "The maximum frequency of iteration, default is 1000") ;
    command.addOption("config", true, "The entity type");
    command.addOption("train", false, "Turn on the trainning mode");
    command.addOption("test", false, "Turn on the test mode");
    if(!command.parse(args)) return ;
    command.printHelp() ;

    String lib = command.getOption("lib", "opennlp") ;
    String sample = command.getOption("sample", null) ;
    String model   = command.getOption("model", null) ;
    String configName = command.getOption("config", "np");
    int iteration = command.getOptionAsInt("iteration", 300);

    TimeReporter timeReporter = new TimeReporter();
    String[] files = MLENT.getFiles(sample);

    ML mlent = null;
    
    if(lib.equals("opennlp")){
      if(command.hasOption("test")){ mlent = new OpennlpImpl(model, configName, true) ; } 
      else mlent = new OpennlpImpl(model, configName, false);
    } else {
      if(command.hasOption("test")){ mlent = new MalletImpl(model, configName, true); }
      else mlent = new MalletImpl(model, configName, false);
    }

    mlent.setTimeReporter(timeReporter);

    if(command.hasOption("train")){ mlent.train(files, model, iteration); } 
    else if(command.hasOption("test")){ mlent.test(files, false); } 
    else { mlent.crossValidation(sample, 0.2, iteration) ;} 

    timeReporter.report(System.out);
  }
}
