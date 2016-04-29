package org.headvances.ml.nlp.pos;

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

import org.headvances.ml.nlp.feature.TokenFeatures;
import org.headvances.ml.nlp.feature.TokenFeaturesGenerator;
import org.headvances.nlp.token.IToken;
import org.headvances.nlp.token.TokenException;
import org.headvances.nlp.token.analyzer.TokenAnalyzer;
import org.headvances.nlp.token.tag.PosTag;
import org.headvances.util.IOUtil;

public class OpenNLPPOSTokenAnalyzer implements TokenAnalyzer {
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

  public int DEFAULT_BEAM_SIZE = 3;
  private BeamSearch<IToken> beam;
  private TokenFeaturesGenerator featuresGenerator ;
  private MaxentModel model;

  public OpenNLPPOSTokenAnalyzer() throws Exception{ 
    this(null);
  }

  public OpenNLPPOSTokenAnalyzer(String modelRes) throws Exception{
    if(modelRes == null) modelRes = "classpath:ml/nlp/pos.opennlp.maxent";

    featuresGenerator = MLPOS.newTokenFeaturesGenerator() ;

    InputStream input = IOUtil.loadRes(modelRes);
    BufferedReader reader = new BufferedReader(new InputStreamReader(input, "UTF-8")) ;
    model = new GenericModelReader(new PlainTextFileDataReader(reader)).getModel();
  }

  public IToken[] analyze(IToken[] token) throws TokenException {
    if(token.length == 0) return token;

    ChunkSearchGenerator generator = new ChunkSearchGenerator(token);    
    beam = new BeamSearch<IToken>(DEFAULT_BEAM_SIZE, generator, model, null, 0);
    Sequence bestSequence = beam.bestSequence(token, null);
    List<String> outcomes = bestSequence.getOutcomes();    
    
    if(outcomes.size() != token.length) throw new TokenException("Failed to get outcomes");
    
    int i = 0;
    while(i < token.length) {
      String target = outcomes.get(i);
      token[i].removeTagType(PosTag.class) ;
      token[i].add(new PosTag(token[i].getNormalizeForm(), target)) ;
      i++ ;
    }
    return token;
  }
}
