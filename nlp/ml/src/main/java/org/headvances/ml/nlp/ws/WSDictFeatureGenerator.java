package org.headvances.ml.nlp.ws;

import org.headvances.ml.nlp.feature.FeatureGenerator;
import org.headvances.ml.nlp.feature.TokenFeatureHolder;
import org.headvances.nlp.dict.Dictionary;
import org.headvances.nlp.dict.WordTree;
import org.headvances.nlp.token.IToken;
import org.headvances.nlp.token.tag.CharacterTag;
import org.headvances.nlp.token.tag.DigitTag;
import org.headvances.nlp.token.tag.EmailTag;
import org.headvances.nlp.token.tag.NumberTag;
import org.headvances.nlp.token.tag.URLTag;
/**
 * $Author: Tuan Nguyen$ 
 **/
public class WSDictFeatureGenerator implements FeatureGenerator {
	static String[] NORMALIZE_TYPES = {
		DigitTag.TYPE, NumberTag.TYPE, CharacterTag.TYPE, URLTag.TYPE, EmailTag.TYPE
	} ;
	
	private WordTree wtree  ;
	
	public WSDictFeatureGenerator(Dictionary dict) {
		this.wtree = dict.getWordTree() ;
	}
	
	public void generate(IToken[] token, int pos, TokenFeatureHolder holder) {
		//generate(token, pos,  -4, "p4:",  holder) ;
		//generate(token, pos,  -3, "p3:",  holder) ;
		generate(token, pos,  -2, "p2:",  holder) ;
		generate(token, pos,  -1, "p1:",  holder) ;
		generate(token, pos,   0, "p0:" , holder) ;
		generate(token, pos,   1, "n1:" , holder) ;
		generate(token, pos,   2, "n2:" , holder) ;
		generate(token, pos,   3, "n3:" , holder) ;
		generate(token, pos,   4, "n4:" , holder) ;
		holder.addFeature(token[pos].getNormalizeForm()) ;
	}
	
  void generate(IToken[] token, int currPos, int pos, String prefix, TokenFeatureHolder holder) {
		int start = currPos + pos ;
		if(start < 0) return ;
		
		addFeature(token, start, start + 1, prefix, holder) ;
		addFeature(token, start, start + 2, prefix, holder) ;
		addFeature(token, start, start + 3, prefix, holder) ;
		addFeature(token, start, start + 4, prefix, holder) ;
	}
	
	private void addFeature(IToken[] token, int pos, int limit, String prefix, TokenFeatureHolder holder) {
		if(limit > token.length) return ;
		WordTree found = wtree.find(token, pos, limit) ;
		if(found != null) {
			String[] word = found.getEntry().getWord() ;
			StringBuilder b = new StringBuilder() ;
			b.append(prefix);
			for(int i = 0; i < word.length; i++) {
				if(i > 0) b.append('+') ;
				b.append(word[i]) ;
			}
			holder.addFeature(b.toString()) ;
		} 
	}
}