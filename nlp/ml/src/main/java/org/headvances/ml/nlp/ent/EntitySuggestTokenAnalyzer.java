package org.headvances.ml.nlp.ent;

import org.headvances.ml.nlp.SuggestTag;
import org.headvances.nlp.token.IToken;
import org.headvances.nlp.token.TokenException;
import org.headvances.nlp.token.analyzer.TokenAnalyzer;
/**
 * $Author: Tuan Nguyen$ 
 **/
public class EntitySuggestTokenAnalyzer implements TokenAnalyzer {
	EntityTokenAnalyzer entityAnalyzer ;
	
	public EntitySuggestTokenAnalyzer(EntityTokenAnalyzer entityAnalyzer) {
		this.entityAnalyzer = entityAnalyzer ;
	}
	
	public IToken[] analyze(IToken[] token) throws TokenException {
		String[] tag = entityAnalyzer.tags(token) ;
		for(int i = 0; i < token.length; i++) {
			if(!"O".equals(tag[i])) {
				token[i].add(new SuggestTag(tag[i])) ;
			}
		}
		return token;
	}

	protected void setEntityTag(String type, IToken token) {}
}
