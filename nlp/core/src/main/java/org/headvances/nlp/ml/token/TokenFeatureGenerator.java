package org.headvances.nlp.ml.token;

import org.headvances.nlp.token.IToken;
/**
 * $Author: Tuan Nguyen$ 
 **/
public interface TokenFeatureGenerator {
	public void generate(IToken[] token, int pos, TokenFeatureHolder holder) ;
}
