package org.headvances.ml.nlp.chunk;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
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
import org.headvances.nlp.token.TokenException;
import org.headvances.nlp.token.analyzer.TokenAnalyzer;
import org.headvances.util.IOUtil;

public class OpenNLPChunkTokenAnalyzer implements TokenAnalyzer {
  class ChunkSearchGenerator implements BeamSearchContextGenerator<IToken> {
    private TokenFeatures[] tfeatures;
    
    public ChunkSearchGenerator(IToken[] token) {
      tfeatures = featuresGenerator.generate(token, false) ;
    }    
    
    public String[] getContext(int index, IToken[] sequence,
        String[] priorDecisions, Object[] additionalContext) {
      return tfeatures[index].getFeatures();
    }
  }

  class ChunkSequenceValidator implements SequenceValidator<IToken>{
    private boolean validOutcome(String outcome, String prevOutcome) {      
      if (outcome.endsWith(":I")) {
        if (prevOutcome == null) {
          return (false);
        }
        else {
          if (prevOutcome.equals("chunk:O")) {
            return (false);
          }
          if (!prevOutcome.substring(0, 8).equals(outcome.substring(0, 8))) {
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

  public OpenNLPChunkTokenAnalyzer() throws Exception{ 
    this(null);
  }

  public OpenNLPChunkTokenAnalyzer(String modelRes) throws Exception{
    if(modelRes == null) modelRes = "classpath:ml/nlp/chunk.opennlp.maxent";

    featuresGenerator = MLChunk.newTokenFeaturesGenerator() ;

    InputStream input = IOUtil.loadRes(modelRes);
    BufferedReader reader = new BufferedReader(new InputStreamReader(input, "UTF-8")) ;
    model = new GenericModelReader(new PlainTextFileDataReader(reader)).getModel();
  }

  public IToken[] analyze(IToken[] token) throws TokenException {
    if(token.length == 0) return token;
    ChunkSearchGenerator generator = new ChunkSearchGenerator(token);    
    beam = new BeamSearch<IToken>(DEFAULT_BEAM_SIZE, generator, model, new ChunkSequenceValidator(), 0);
    Sequence bestSequence = beam.bestSequence(token, null);
    List<String> outcomes = bestSequence.getOutcomes();    
    
    if(outcomes.size() != token.length) throw new TokenException("Failed to get outcomes");
    
    int i = 0;
    while(i < token.length) {
      String target = outcomes.get(i);
      token[i].removeTagType(ChunkTag.class) ;
      token[i].add(new ChunkTag(token[i].getNormalizeForm(), target)) ;
      i++ ;
    }
    return token;
  }
}
