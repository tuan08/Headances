package org.headvances.nlp.ml.token;

import org.headvances.nlp.NLPResource;
import org.headvances.nlp.token.IToken;
import org.headvances.nlp.token.TokenException;
import org.headvances.nlp.token.analyzer.TokenAnalyzer;
import org.headvances.util.text.StringUtil;

import cc.mallet.fst.CRF;
import cc.mallet.fst.MaxLatticeDefault;
import cc.mallet.fst.Transducer;
import cc.mallet.types.Alphabet;
import cc.mallet.types.Instance;
import cc.mallet.types.LabelAlphabet;
import cc.mallet.types.Sequence;

abstract public class MalletCRFTokenAnalyzer implements TokenAnalyzer {
  private TokenFeaturesGenerator generator ;
  private CRF crf ;
  private MalletCRFFeatureVectorFactory factory ;
  
  public MalletCRFTokenAnalyzer(TokenFeaturesGenerator generator, String crfModelFile) throws Exception {
    this.generator = generator ;
    this.crf = NLPResource.getInstance().getObject(crfModelFile);
    Alphabet dataAlphabet = crf.getInputAlphabet() ;
    LabelAlphabet labelAlphabet = (LabelAlphabet) crf.getOutputAlphabet() ;
    this.factory = new MalletCRFFeatureVectorFactory(dataAlphabet, labelAlphabet) ;
  }
  
  public IToken[] analyze(IToken[] token) throws TokenException {
  	if(token.length == 0) return token ;
  	TokenFeatures[] tfeatures = generator.generate(token, false) ;
  	Instance instance = factory.createDecodeInstance(tfeatures) ;
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
    	tag(token[i], target) ;
    	i++ ;
    }
    return token;
  }

  public String[] predict(IToken[] token) throws TokenException {
  	if(token.length == 0) return StringUtil.EMPTY_ARRAY ;
  	TokenFeatures[] tfeatures = generator.generate(token, false) ;
    Instance instance = factory.createDecodeInstance(tfeatures) ;
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

  abstract protected void tag(IToken token, String predict) ;
  
  static public Sequence[] apply(Transducer model, Sequence input, int nBest) {
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
