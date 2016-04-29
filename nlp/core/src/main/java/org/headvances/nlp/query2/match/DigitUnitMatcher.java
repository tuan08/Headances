package org.headvances.nlp.query2.match;

import java.util.List;

import org.headvances.nlp.token.IToken;
import org.headvances.nlp.token.tag.DigitTag;
import org.headvances.nlp.token.tag.TokenTag;
import org.headvances.nlp.util.ParamHolder;

public class DigitUnitMatcher extends UnitMatcher {
	public DigitUnitMatcher(ParamHolder holder, int allowNextMatchDistance) throws Exception {
		setAllowNextMatchDistance(allowNextMatchDistance) ;
	}
	
	public UnitMatch matches(IToken[] token, int pos) {
		List<TokenTag> tags = token[pos].getTag() ;
		if(tags == null) return null ;
		for(int i = 0; i < tags.size(); i++) {
			TokenTag tag = tags.get(i) ;
			if(!(tag instanceof DigitTag)) continue ;
			return new UnitMatch(token[pos].getNormalizeForm(), pos, pos + 1) ;
		}
		return null ;
	}
}