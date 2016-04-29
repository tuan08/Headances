package org.headvances.ml.nlp.ws;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.PrintStream;
import java.util.ArrayList;
import java.util.List;

import opennlp.model.GenericModelReader;
import opennlp.model.MaxentModel;
import opennlp.model.PlainTextFileDataReader;
import opennlp.tools.util.BeamSearch;
import opennlp.tools.util.BeamSearchContextGenerator;
import opennlp.tools.util.Sequence;
import opennlp.tools.util.SequenceValidator;

import org.headvances.ml.nlp.feature.TokenFeatures;
import org.headvances.ml.nlp.feature.TokenFeaturesGenerator;
import org.headvances.nlp.token.IToken;
import org.headvances.nlp.token.Token;
import org.headvances.nlp.token.TokenException;
import org.headvances.nlp.token.analyzer.TokenAnalyzer;
import org.headvances.util.ConsoleUtil;
import org.headvances.util.IOUtil;

public class OpenNLPWSTokenAnalyzer implements TokenAnalyzer {
  class WordSearchGenerator implements BeamSearchContextGenerator<IToken> {
    private TokenFeatures[] tfeatures;
    
    public WordSearchGenerator(IToken[] token) {
      tfeatures = featuresGenerator.generate(token, false) ;
    }    
    
    public String[] getContext(int index, IToken[] sequence,
        String[] priorDecisions, Object[] additionalContext) {
      return tfeatures[index].getFeatures();
    }
  }

  class ChunkSequenceValidator implements SequenceValidator<IToken>{
    private boolean validOutcome(String outcome, String prevOutcome) {      
      if (outcome.equals(WSTargetFeatureGenerator.IWORD)) {
        if (prevOutcome == null) {
          return (false);
        }
        else {
          if (prevOutcome.equals("O")) {
            return (false);
          }
        }
      }
      return true;
    }

    protected boolean validOutcome(String outcome, String[] sequence) {
      String prevOutcome = null;
      if (sequence.length > 0) {
        prevOutcome = sequence[sequence.length-1];
      }
      return validOutcome(outcome,prevOutcome);
    }
    
    public boolean validSequence(int i, IToken[] sequence, String[] s, String outcome) {
      return validOutcome(outcome, s);
    }
  }
  
  public int DEFAULT_BEAM_SIZE = 3;
  private BeamSearch<IToken> beam;  
  private TokenFeaturesGenerator featuresGenerator ;
  private MaxentModel model;

  public OpenNLPWSTokenAnalyzer() throws Exception{ 
    this(null);
  }

  public OpenNLPWSTokenAnalyzer(String modelRes) throws Exception{
    if(modelRes == null) modelRes = "classpath:ml/nlp/vnws.opennlp.maxent";

    featuresGenerator = MLWS.newTokenFeaturesGenerator() ;

    InputStream input = IOUtil.loadRes(modelRes);
    BufferedReader reader = new BufferedReader(new InputStreamReader(input, "UTF-8")) ;
    model = new GenericModelReader(new PlainTextFileDataReader(reader)).getModel();
  }

  public IToken[] analyze(IToken[] token) throws TokenException {
    if(token.length == 0) return token;
    WordSearchGenerator generator = new WordSearchGenerator(token);    
    beam = new BeamSearch<IToken>(DEFAULT_BEAM_SIZE, generator, model, new ChunkSequenceValidator(), 0);
    Sequence bestSequence = beam.bestSequence(token, null);
    List<String> outcomes = bestSequence.getOutcomes();    
    if(outcomes.size() != token.length) throw new TokenException("Failed to get outcomes");
    
    List<IToken> holder = new ArrayList<IToken>() ;
    int i = 0;
    while(i < token.length) {
      String target = outcomes.get(i);
      if(WSTargetFeatureGenerator.BWORD.equals(target)) {
        int start = i ;
        i++ ;
        while(i < token.length && WSTargetFeatureGenerator.IWORD.equals(outcomes.get(i))) { i++ ; }
        if(i - start == 1) {
          holder.add(token[start]);
        } else {
          IToken newToken = new Token(token, start, i) ;
          holder.add(newToken);
        }
      } else {
        holder.add(token[i]) ;
        i++ ;
      }
    }
    return holder.toArray(new IToken[holder.size()]);
  }
  
  public void printPredict(IToken[] token, String[] predict) throws Exception {
		PrintStream out = ConsoleUtil.getUTF8SuportOutput() ;
		for(int i = 0; i < token.length; i++) {
			out.append(' ').append(token[i].getNormalizeForm()).append(' ').append(predict[i]).println() ;
		}
	}
}
