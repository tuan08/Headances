package org.headvances.ml.nlp.ws;

import java.util.ArrayList;
import java.util.List;

import org.headvances.ml.nlp.OpenNLPML;
import org.headvances.ml.nlp.feature.CapLetterFeatureGenerator;
import org.headvances.ml.nlp.feature.TokenTypeFeatureGenerator;
import org.headvances.ml.nlp.feature.TokenFeaturesGenerator;
import org.headvances.nlp.NLPResource;
import org.headvances.nlp.dict.Dictionary;
import org.headvances.nlp.wtag.WTagDocumentSet;
import org.headvances.nlp.wtag.WTagDocumentReader;
import org.headvances.util.CommandParser;
import org.headvances.util.TimeReporter;

public class OpenNLPWS extends OpenNLPML {

  public OpenNLPWS(String modelRes, boolean loadModel) throws Exception {
    super(modelRes, loadModel);
  }

  public TokenFeaturesGenerator createTokenFeaturesGenerator() throws Exception {
    WTagDocumentReader reader = new WTagDocumentReader() ;
    reader.setTokenAnalyzer(MLWS.WS_ANALYZER) ;

    Dictionary dict = NLPResource.getInstance().getDictionary(Dictionary.DICT_RES) ;
    TokenFeaturesGenerator featuresGenerator = new TokenFeaturesGenerator(reader) ;
    featuresGenerator.add(new TokenTypeFeatureGenerator()) ;
    featuresGenerator.add(new CapLetterFeatureGenerator()) ;
    featuresGenerator.add(new WSDictFeatureGenerator(dict)) ;

    featuresGenerator.setTargetFeatureGenerator(new WSTargetFeatureGenerator()) ;
    return featuresGenerator ;
  }

  public void train(String dataDir, String modelRes, int iterations) throws Exception{
    WTagDocumentSet set = new WTagDocumentSet(dataDir, ".*\\.(tagged|wtag)") ;
    List<String> trainFiles = new ArrayList<String>() ;
    String[] files = set.getFiles() ;
    for(int i = 0; i < files.length; i++) { trainFiles.add(files[i]); }

    train(files, modelRes, iterations);
  }

  public void test(String dataDir, boolean printToken) throws Exception{
    WTagDocumentSet set = new WTagDocumentSet(dataDir, ".*\\.(tagged|wtag)") ;
    List<String> trainFiles = new ArrayList<String>() ;
    String[] files = set.getFiles() ;
    for(int i = 0; i < files.length; i++) { trainFiles.add(files[i]); }

    test(files, printToken);
  }

  public static void main(String[] args) throws Exception{
    if(args == null || args.length == 0) { 
      args = new String[] {
          "-sample", "d:/ml-data/wtag",
          "-train",
          "-model",  "target/ml/opennlp.model",
      };
    }
    CommandParser command = new CommandParser("OpenNLP:") ;
    command.addMandatoryOption("sample", true, "The sampple directory, the file extension should be iob2 or tagged") ;
    command.addMandatoryOption("model", true, "The output model file") ;
    command.addOption("iteration", true, "The maximum frequency of iteration, default is 100") ;
    command.addOption("train", false, "Turn on the trainning mode");
    command.addOption("test", false, "Turn on the test mode");
    if(!command.parse(args)) return ;
    command.printHelp() ;

    String sample = command.getOption("sample", null) ;
    String model   = command.getOption("model", null) ;
    int iteration = command.getOptionAsInt("iteration", 100);

    OpenNLPWS NLPWS = null;
    TimeReporter timeReporter = new TimeReporter();
    
    if(command.hasOption("train")){
      NLPWS = new OpenNLPWS(model, false);;
      NLPWS.setTimeReporter(timeReporter);
      NLPWS.train(sample, model, iteration);
    } else if( command.hasOption("test")){
      NLPWS = new OpenNLPWS(model, true);
      NLPWS.setTimeReporter(timeReporter);
      NLPWS.test(sample, true);
    } else {
      NLPWS = new OpenNLPWS(model, false);
      NLPWS.setTimeReporter(timeReporter);
      NLPWS.crossValidation(sample, 0.2, iteration); 
    }
    NLPWS.report(System.out);
  }

}
