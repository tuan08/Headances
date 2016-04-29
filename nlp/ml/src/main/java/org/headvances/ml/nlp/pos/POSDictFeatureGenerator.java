package org.headvances.ml.nlp.pos;

import org.headvances.ml.nlp.feature.FeatureGenerator;
import org.headvances.ml.nlp.feature.TokenFeatureHolder;
import org.headvances.nlp.token.IToken;
import org.headvances.nlp.token.tag.PosTag;
import org.headvances.util.text.StringUtil;

public class POSDictFeatureGenerator implements FeatureGenerator {
	public void generate(IToken[] token, int pos, TokenFeatureHolder holder) {
		//addMeaning(token, pos - 4, "meaning:-4:", holder) ;
		//addMeaning(token, pos - 3, "meaning:-3:", holder) ;
		addMeaning(token, pos - 2, "p2:", holder) ;
		addMeaning(token, pos - 1, "p1:", holder) ;
		addMeaning(token, pos    , "p0:", holder) ;
		//addMeaning(token, pos + 1, "meaning:1:", holder) ;
		//addMeaning(token, pos + 2, "meaning:2:", holder) ;
		//addMeaning(token, pos + 3, "meaning:3:", holder) ;
		//addMeaning(token, pos + 4, "meaning:4:", holder) ;		
	}
	
  void addMeaning(IToken[] token, int pos, String label, TokenFeatureHolder holder) {
    if(pos < 0 || pos >= token.length) return ;   
    PosTag ptag = token[pos].getFirstTagType(PosTag.class) ;
    if(ptag != null) {
    	String[] posTag = ptag.getPosTag() ;
    	for (int i = 0; i < posTag.length; i++)
    		holder.addFeature(label + posTag[i]) ;
    }
    String value = StringUtil.joinStringArray(token[pos].getWord(), "+") ;
    holder.addFeature(label + value) ;
  } 
}