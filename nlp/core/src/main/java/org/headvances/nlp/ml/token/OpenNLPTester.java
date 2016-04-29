package org.headvances.nlp.ml.token;

import java.io.InputStream;
import java.io.PrintStream;
import java.text.DecimalFormat;
import java.util.List;

import opennlp.model.DataReader;
import opennlp.model.GenericModelReader;
import opennlp.model.MaxentModel;
import opennlp.model.PlainTextFileDataReader;
import opennlp.tools.util.BeamSearch;
import opennlp.tools.util.BeamSearchContextGenerator;
import opennlp.tools.util.Sequence;

import org.headvances.util.IOUtil;
import org.headvances.util.TimeReporter;
/**
 * $Author: Tuan Nguyen$ 
 **/
public class OpenNLPTester extends MLTester {  
  private MaxentModel model;
  private TokenFeaturesGenerator featuresGenerator;
  public int DEFAULT_BEAM_SIZE = 3;
  private BeamSearch<TokenFeatures> beam;
  
  private TimeReporter timeReporter;
  private Appendable out = System.out ;

  public OpenNLPTester(TokenFeaturesGenerator generator, String modelRes) throws Exception{
  	featuresGenerator = generator ;
  	if(modelRes.indexOf(':') < 0) modelRes = "file:" + modelRes ;
  	InputStream input = IOUtil.loadRes(modelRes);
  	DataReader dataReader = new PlainTextFileDataReader(input);
  	model = new GenericModelReader(dataReader).getModel();
  }

  public MLTestLog test(String wtagFile) throws Exception {
  	long start = System.currentTimeMillis() ;
  	String content = IOUtil.getFileContentAsString(wtagFile) ;
  	String[] sentence = content.split("\n") ;
  	MLTestLog allLog = new MLTestLog(0, 0, 0) ;
  	for(String selSentence : sentence) {
  		MLTestLog log = testSentence(selSentence) ;
  		allLog.merge(log) ;
  	}
    DecimalFormat formater = new DecimalFormat("#.00%") ;
		String ratioStr = formater.format((double)allLog.hit/allLog.token) ;
		System.out.println("Decode in " + (System.currentTimeMillis() - start) + "ms");
    out.append("Correct ratio: " + allLog.hit + "/" + allLog.token + " " + ratioStr + "\n");
		out.append("-------------------------------------------------------------------\n");
		return allLog;
  }
  
  private MLTestLog testSentence(String sentence) throws Exception {
    TokenFeatures[] tokenFeatures = featuresGenerator.generate(sentence);
    ChunkSearchGenerator generator = new ChunkSearchGenerator();    
    
    beam = new BeamSearch<TokenFeatures>(DEFAULT_BEAM_SIZE, generator, model, null, 0);
    long start = System.currentTimeMillis() ;
    Sequence bestSequence = beam.bestSequence(tokenFeatures, null);
    //System.out.println("Decode in " + (System.currentTimeMillis() - start) + "ms" + ", features" + tokenFeatures.length);
    
    List<String> outcomes = bestSequence.getOutcomes();    
    if(outcomes.size() != tokenFeatures.length) return new MLTestLog(0, 0, 0) ;
    
    int total = 0, hit = 0;
    for (int i = 0; i < outcomes.size(); i++) {
      String target = outcomes.get(i) ;
      String expectTarget = tokenFeatures[i].getTargetFeature() ; 

      if(expectTarget.equals(target)) hit++;
      total++;
    }  
    return new MLTestLog(total, hit, total - hit);
  }
  
  public void report(PrintStream out){ timeReporter.report(out); }
  
  class ChunkSearchGenerator implements BeamSearchContextGenerator<TokenFeatures> {    
    public String[] getContext(int index, TokenFeatures[] sequence, String[] priorDecisions, Object[] additionalContext) {
      return sequence[index].getFeatures();
    }    
  }
}
