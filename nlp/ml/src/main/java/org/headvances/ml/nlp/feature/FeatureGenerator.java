package org.headvances.ml.nlp.feature;

import org.headvances.nlp.token.IToken;
/**
 * $Author: Tuan Nguyen$ 
 **/
public interface FeatureGenerator {
	public void generate(IToken[] token, int pos, TokenFeatureHolder holder) ;
}
