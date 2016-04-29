package org.headvances.ml.nlp.ws;

import org.headvances.ml.nlp.feature.FeatureGenerator;
import org.headvances.ml.nlp.feature.TokenFeatureHolder;
import org.headvances.nlp.token.IToken;
import org.headvances.nlp.token.analyzer.VNNameTokenAnalyzer;
/**
 * $Author: Tuan Nguyen$ 
 **/
public class VNNameFeatureGenerator implements FeatureGenerator {
	public void generate(IToken[] token, int pos, TokenFeatureHolder holder) {
		char fletter = token[pos].getOriginalForm().charAt(0) ;
		if(Character.isUpperCase(fletter)) {
			String nform = token[pos].getNormalizeForm() ;
			if(VNNameTokenAnalyzer.LASTNAMES.contains(nform)) {
				holder.addFeature("vnname:last") ;
			} 
		}
	}
}