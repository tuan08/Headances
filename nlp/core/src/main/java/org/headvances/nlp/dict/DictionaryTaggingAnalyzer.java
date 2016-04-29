package org.headvances.nlp.dict;

import org.headvances.nlp.token.IToken;
import org.headvances.nlp.token.TokenException;
import org.headvances.nlp.token.analyzer.TokenAnalyzer;
import org.headvances.nlp.token.tag.WordTag;
/**
 * $Author: Tuan Nguyen$ 
 **/
public class DictionaryTaggingAnalyzer implements TokenAnalyzer {
	private Dictionary dict ;
	
	public DictionaryTaggingAnalyzer(Dictionary dict) {
		this.dict = dict ;
	}
	
	public IToken[] analyze(IToken[] tokens) throws TokenException {
		for(int i = 0; i < tokens.length; i++) {
			Entry entry = dict.getEntry(tokens[i].getNormalizeForm()) ;
			if(entry != null) {
				tokens[i].removeTagType(WordTag.class) ;
				tokens[i].add(entry.getTag()) ;
			}
		}
		return tokens ;
	}
}