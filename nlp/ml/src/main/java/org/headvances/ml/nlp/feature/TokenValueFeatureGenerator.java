package org.headvances.ml.nlp.feature;

import org.headvances.nlp.token.IToken;
import org.headvances.util.text.StringUtil;
/**
 * $Author: Tuan Nguyen$ 
 **/
public class TokenValueFeatureGenerator implements FeatureGenerator {
	
	public TokenValueFeatureGenerator() {
	}
	
	public void generate(IToken[] token, int pos, TokenFeatureHolder holder) {
	  IToken sel = token[pos] ;
	  holder.addFeature(StringUtil.joinStringArray(sel.getWord(), "_")) ;
	}	
}