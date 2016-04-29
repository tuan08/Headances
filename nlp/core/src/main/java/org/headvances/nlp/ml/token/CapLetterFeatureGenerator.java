package org.headvances.nlp.ml.token;

import org.headvances.nlp.token.IToken;

/**
 * $Author: Tuan Nguyen$ 
 **/
public class CapLetterFeatureGenerator implements TokenFeatureGenerator {
	public void generate(IToken[] token, int pos, TokenFeatureHolder holder) {
		addFeature(token, pos - 2, "p2:", holder) ;
		addFeature(token, pos - 1, "p1:", holder) ;
		addFeature(token, pos,     "p0:", holder) ;
		addFeature(token, pos + 1, "n1:", holder) ;
		addFeature(token, pos + 2, "n2:", holder) ;
	}
	
	public void addFeature(IToken[] token, int pos, String prefix, TokenFeatureHolder holder) {
		if(pos < 0) return ;
		if(pos >= token.length) return ;
		String orig = token[pos].getOriginalForm() ;
		if(orig.length() == 0) return ;
		if(!Character.isUpperCase(orig.charAt(0))) return ;
		for(int i = 1; i < orig.length(); i++) {
			if(!Character.isUpperCase(orig.charAt(i))) {
				holder.addFeature(prefix + "ic") ;
				return ;
			}
		}
		holder.addFeature(prefix + "ac") ;
	}
}