package org.headvances.ml.nlp.ent;

import java.util.ArrayList;
import java.util.List;

import org.headvances.ml.nlp.feature.MalletFeatureVectorFactory;
import org.headvances.ml.nlp.feature.TokenFeaturesGenerator;
import org.headvances.nlp.NLPResource;
import org.headvances.nlp.token.IToken;
import org.headvances.nlp.token.TokenCollection;
import org.headvances.nlp.token.TokenException;
import org.headvances.util.text.StringUtil;

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
abstract public class CRFEntityTokenAnalyzer extends EntityTokenAnalyzer {
	private TokenFeaturesGenerator featuresGenerator ;
	private CRF crf ;
	private MalletFeatureVectorFactory factory ;
	
	public CRFEntityTokenAnalyzer(String crfModelFile, EntitySetConfig config) throws Exception {
		featuresGenerator = MLENT.createTokenFeaturesGenerator(config) ;
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

    List<IToken> holder = new ArrayList<IToken>() ;
    int i = 0;
    while(i < token.length) {
    	String target = outputs[0].get(i).toString() ;
    	if(target.endsWith(":B")) {
    		int start = i ;
    		i++ ;
    		while(i < token.length && outputs[0].get(i).toString().endsWith(":I")) {
    			i++ ;
    		}
    		String entityType = target.substring(0, target.length() - 2);
    		if(i - start > 1) {
    			IToken newToken = new TokenCollection(token, start, i) ;
    			setEntityTag(entityType, newToken) ;
    			holder.add(newToken);
    		} else {
    			setEntityTag(entityType, token[start]) ;
    			holder.add(token[start]);
    		}
    	} else {
    		holder.add(token[i]) ;
    		i++ ;
    	}
    }
  	return holder.toArray(new IToken[holder.size()]);
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
  
  abstract protected void setEntityTag(String type, IToken token) ;
}
