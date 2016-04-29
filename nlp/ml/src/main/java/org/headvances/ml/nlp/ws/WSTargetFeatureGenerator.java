package org.headvances.ml.nlp.ws;

import org.headvances.ml.nlp.feature.FeatureGenerator;
import org.headvances.ml.nlp.feature.TokenFeatureHolder;
import org.headvances.nlp.token.IToken;
import org.headvances.nlp.token.tag.TokenTag;
import org.headvances.nlp.util.CharacterSet;
import org.headvances.nlp.wtag.WTagBoundaryTag;
/**
 * $Author: Tuan Nguyen$ 
 **/

public class WSTargetFeatureGenerator implements FeatureGenerator {
	final static public String BWORD = "B_W" ;
	final static public String IWORD = "I_W" ;
	
	public WSTargetFeatureGenerator() {
	}

	public void generate(IToken[] token, int pos, TokenFeatureHolder holder) {
		char[] buf = token[pos].getNormalizeFormBuf() ;
		if(CharacterSet.isNewLine(buf[0])){
			holder.setTargetFeature("O") ;
			return ;
		}
		
		if(buf.length == 1 && CharacterSet.isIn(buf[0], CharacterSet.PUNCTUATION) && 
			 token[pos].hasTagType(WTagBoundaryTag.TYPE)) {
			holder.setTargetFeature("O") ;
			return ;
		}
		WTagBoundaryTag btag = token[pos].getFirstTagType(WTagBoundaryTag.class) ;
		for(String sel : btag.getFeatures()) {
			if(sel.equals(BWORD) || sel.equals(IWORD)) {
				holder.setTargetFeature(sel) ;
				break ;
			}
		}
	}	
}