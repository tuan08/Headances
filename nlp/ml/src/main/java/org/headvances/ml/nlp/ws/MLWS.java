package org.headvances.ml.nlp.ws;

import java.io.File;
import java.util.ArrayList;
import java.util.List;
import java.util.regex.Pattern;

import org.headvances.ml.nlp.ML;
import org.headvances.ml.nlp.MalletML;
import org.headvances.ml.nlp.OpenNLPML;
import org.headvances.ml.nlp.feature.CapLetterFeatureGenerator;
import org.headvances.ml.nlp.feature.TokenTypeFeatureGenerator;
import org.headvances.ml.nlp.feature.TokenFeaturesGenerator;
import org.headvances.nlp.NLPResource;
import org.headvances.nlp.dict.Dictionary;
import org.headvances.nlp.token.IToken;
import org.headvances.nlp.token.Token;
import org.headvances.nlp.token.TokenException;
import org.headvances.nlp.token.analyzer.CommonTokenAnalyzer;
import org.headvances.nlp.token.analyzer.TokenAnalyzer;
import org.headvances.nlp.wtag.WTagBoundaryTag;
import org.headvances.nlp.wtag.WTagDocumentSet;
import org.headvances.nlp.wtag.WTagDocumentReader;
import org.headvances.util.CommandParser;
import org.headvances.util.TimeReporter;
/**
 * $Author: Tuan Nguyen$ 
 **/
public class MLWS {

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

  static public TokenFeaturesGenerator newTokenFeaturesGenerator() throws Exception {
    WTagDocumentReader reader = new WTagDocumentReader() ;
    reader.setTokenAnalyzer(WS_ANALYZER) ;

    Dictionary dict = NLPResource.getInstance().getDictionary(Dictionary.DICT_RES) ;
    TokenFeaturesGenerator featuresGenerator = new TokenFeaturesGenerator(reader) ;
    featuresGenerator.add(new TokenTypeFeatureGenerator()) ;
    featuresGenerator.add(new CapLetterFeatureGenerator()) ;
    featuresGenerator.add(new VNNameFeatureGenerator()) ;
    featuresGenerator.add(new WSDictFeatureGenerator(dict)) ;
    
    featuresGenerator.setTargetFeatureGenerator(new WSTargetFeatureGenerator()) ;
    return featuresGenerator ;
  }

  static Pattern SPLITER = Pattern.compile(" ") ;
  static public WSTokenAnalyzer WS_ANALYZER = new WSTokenAnalyzer() ;

  static class WSTokenAnalyzer implements TokenAnalyzer {
    public IToken[] analyze(IToken[] token) throws TokenException {
      List<IToken> holder = new ArrayList<IToken>() ;
      WTagBoundaryTag BW = new WTagBoundaryTag(WSTargetFeatureGenerator.BWORD) ;
      WTagBoundaryTag IW = new WTagBoundaryTag(WSTargetFeatureGenerator.IWORD) ;
      for(int i = 0; i < token.length; i++) {
        IToken sel = token[i] ;
        if(sel.getWord().length == 1) {
          sel.removeTagType(WTagBoundaryTag.class) ;
          sel.add(CommonTokenAnalyzer.createTag(sel.getNormalizeForm(), sel.getOriginalForm())) ;
          sel.add(BW) ;
          holder.add(sel) ;
        } else {
          String oform = sel.getOriginalForm() ;
          String[] word = SPLITER.split(oform) ;
          for(int j = 0; j < word.length; j++) {
            if(word[j].length() == 0) continue ;
            IToken newToken = new Token(word[j]) ;
            newToken.add(CommonTokenAnalyzer.createTag(newToken.getNormalizeForm(), newToken.getOriginalForm())) ;
            if(j == 0) newToken.add(BW) ;
            else       newToken.add(IW) ;
            holder.add(newToken) ;
          }
        }
      }
      return holder.toArray(new IToken[holder.size()]) ;
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

  static public void main(String[] args) throws Exception {
    if(args == null || args.length == 0) { 
      args = new String[] {
        "-sample", "d:/ml-data/wtag/set.vlsp",
        "-model",  "target/vnws.model",
        "-lib", "opennlp",
        "-train", "train"
      };
    }
    CommandParser command = new CommandParser("Word segmentation:") ;
    command.addMandatoryOption("sample", true, "The sampple file or directory, the file extension should be iob2 or tagged") ;
    command.addMandatoryOption("model", true, "The output model file") ;
    command.addOption("lib", true, "Use Mallet-CRF or OpenNLP-Maxent library") ;
    command.addOption("iteration", true, "The maximum frequency of iteration, default is 1000") ;
    command.addOption("train", false, "Turn on the trainning mode");
    command.addOption("test", false, "Turn on the test mode");
    if(!command.parse(args)) return ;
    command.printHelp() ;

    String lib = command.getOption("lib", "opennlp") ;
    String sample = command.getOption("sample", null) ;
    String model   = command.getOption("model", null) ;
    int iteration = command.getOptionAsInt("iteration", 2000);

    TimeReporter timeReporter = new TimeReporter();
    String[] files = MLWS.getFiles(sample);
    
    ML mlws = null;
    
    if(lib.equals("opennlp")){
      if(command.hasOption("test")){ mlws = new OpennlpImpl(model, true) ; } 
      else mlws = new OpennlpImpl(model, false);
    } else {
      if(command.hasOption("test")){ mlws = new MalletImpl(model, true); }
      else mlws = new MalletImpl(model, false);
    }
    
    mlws.setTimeReporter(timeReporter);
    
    if(command.hasOption("train")){ mlws.train(files, model, iteration); } 
    else if(command.hasOption("test")){ mlws.test(files, false); } 
    else { mlws.crossValidation(sample, 0.2, iteration) ;} 
    
    timeReporter.report(System.out);
  }
}