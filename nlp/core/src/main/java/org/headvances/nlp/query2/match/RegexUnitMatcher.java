package org.headvances.nlp.query2.match;

import org.headvances.nlp.token.IToken;
import org.headvances.util.text.RegexMatcher;

public class RegexUnitMatcher extends UnitMatcher {
	private RegexMatcher regex ;

	public RegexUnitMatcher(String regex, int allowNextMatchDistance) throws Exception {
		setAllowNextMatchDistance(allowNextMatchDistance) ;
		this.regex = RegexMatcher.createAutomaton(regex.trim()) ;
	}
	
	public UnitMatch matches(IToken[] token, int pos) {
		String string = token[pos].getNormalizeForm() ;
		if(regex.matches(string)) {
			return new UnitMatch(token[pos].getNormalizeForm(), pos, pos + 1) ;
		}
		return null ;
	}
}