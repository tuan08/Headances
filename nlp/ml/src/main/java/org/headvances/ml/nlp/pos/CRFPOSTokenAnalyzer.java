package org.headvances.ml.nlp.pos;

import org.headvances.ml.nlp.feature.TokenFeaturesGenerator;
import org.headvances.ml.nlp.feature.MalletFeatureVectorFactory;
import org.headvances.nlp.NLPResource;
import org.headvances.nlp.token.IToken;
import org.headvances.nlp.token.TokenException;
import org.headvances.nlp.token.analyzer.TokenAnalyzer;
import org.headvances.nlp.token.tag.PosTag;
import org.headvances.util.text.StringUtil;

import cc.mallet.fst.CRF;
import cc.mallet.fst.MaxLatticeDefault;
import cc.mallet.fst.Transducer;
import cc.mallet.types.Alphabet;
import cc.mallet.types.Instance;
import cc.mallet.types.LabelAlphabet;
import cc.mallet.types.Sequence;

public class CRFPOSTokenAnalyzer implements TokenAnalyzer {
  private TokenFeaturesGenerator featuresGenerator ;
  private CRF crf ;
  private MalletFeatureVectorFactory factory ;
  
  public CRFPOSTokenAnalyzer() throws Exception  {
    this("classpath:ml/nlp/pos.crf") ;
  }
  
  public CRFPOSTokenAnalyzer(String crfModelFile) throws Exception {
    featuresGenerator = MLPOS.newTokenFeaturesGenerator() ;
    crf = NLPResource.getInstance().getObject(crfModelFile);
    Alphabet dataAlphabet = crf.getInputAlphabet() ;
    LabelAlphabet labelAlphabet = (LabelAlphabet) crf.getOutputAlphabet() ;
    factory = new MalletFeatureVectorFactory(dataAlphabet, labelAlphabet, featuresGenerator) ;
  }
  
  public IToken[] analyze(IToken[] token) throws TokenException {
  	if(token.length == 0) return token ;
    Instance instance = factory.createDecodeInstance(token) ;
    Sequence input = (Sequence) instance.getData();
    Sequence[] outputs = apply(crf, input, /*nBestOption.value*/ 1);
    for (int a = 0; a < outputs.length; a++) {
      if (outputs[a].size() != input.size()) {
        throw new TokenException("Failed to decode input sequence, answer " + a);
      }
    }
    
    int i = 0;
    while(i < token.length) {
    	String target = outputs[0].get(i).toString() ;
    	token[i].removeTagType(PosTag.class) ;
    	token[i].add(new PosTag(token[i].getNormalizeForm(), target)) ;
    	i++ ;
    }
    return token;
  }

  public String[] tags(IToken[] token) throws TokenException {
  	if(token.length == 0) return StringUtil.EMPTY_ARRAY ;
    Instance instance = factory.createDecodeInstance(token) ;
    Sequence input = (Sequence) instance.getData();
    Sequence[] outputs = apply(crf, input, /*nBestOption.value*/ 1);
    for (int a = 0; a < outputs.length; a++) {
      if (outputs[a].size() != input.size()) {
        throw new TokenException("Failed to decode input sequence, answer " + a);
      }
    }
    
    String[] tag = new String[token.length] ;
    int i = 0;
    while(i < token.length) {
      tag[i] = outputs[0].get(i).toString() ;
      i++ ;
    }
    return tag ;
  }

  private Sequence[] apply(Transducer model, Sequence input, int nBest) {
    Sequence[] answers;
    if (nBest == 1) {
      answers  = new Sequence[] {model.transduce (input) };
    } else {
      MaxLatticeDefault lattice = new MaxLatticeDefault (model, input, null, /*cache-size*/ 100000);
      answers = lattice.bestOutputSequences(nBest).toArray(new Sequence[0]);
    }
    return answers;
  }
}
