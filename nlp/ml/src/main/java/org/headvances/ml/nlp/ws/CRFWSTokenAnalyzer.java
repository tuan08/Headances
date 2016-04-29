package org.headvances.ml.nlp.ws;

import java.util.ArrayList;
import java.util.List;

import org.headvances.ml.nlp.feature.MalletFeatureVectorFactory;
import org.headvances.ml.nlp.feature.TokenFeaturesGenerator;
import org.headvances.nlp.NLPResource;
import org.headvances.nlp.token.IToken;
import org.headvances.nlp.token.Token;
import org.headvances.nlp.token.TokenException;
import org.headvances.nlp.token.analyzer.TokenAnalyzer;

import cc.mallet.fst.CRF;
import cc.mallet.fst.MaxLatticeDefault;
import cc.mallet.fst.Transducer;
import cc.mallet.types.Alphabet;
import cc.mallet.types.Instance;
import cc.mallet.types.LabelAlphabet;
import cc.mallet.types.Sequence;
/**
 * $Author: Tuan Nguyen$ 
 **/
public class CRFWSTokenAnalyzer implements TokenAnalyzer {
	private TokenFeaturesGenerator featuresGenerator ;
	private CRF crf ;
	private MalletFeatureVectorFactory factory ;
	
	public CRFWSTokenAnalyzer() throws Exception {
		this(null) ;
	}
	
	public CRFWSTokenAnalyzer(String crfModelFile) throws Exception  {
		if(crfModelFile == null) crfModelFile = "classpath:ml/nlp/vnws.crf" ;
		featuresGenerator = MLWS.newTokenFeaturesGenerator() ;
    crf = NLPResource.getInstance().getObject(crfModelFile) ;
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
    
    List<IToken> holder = new ArrayList<IToken>() ;
    int i = 0;
    while(i < token.length) {
      String target = outputs[0].get(i).toString() ;
      if(WSTargetFeatureGenerator.BWORD.equals(target)) {
        int start = i ;
        i++ ;
        while(i < token.length && 
            WSTargetFeatureGenerator.IWORD.equals(outputs[0].get(i).toString())) {
          i++ ;
        }
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
