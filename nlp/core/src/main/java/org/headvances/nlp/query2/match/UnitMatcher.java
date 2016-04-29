package org.headvances.nlp.query2.match;

import org.headvances.nlp.token.IToken;
import org.headvances.util.text.StringMatcher;
/**
 * $Author: Tuan Nguyen$ 
 **/
abstract public class UnitMatcher {
	private String name ;
	private int allowNextMatchDistance ;
	
	public String getName() { return this.name ; }
	public void   setName(String name) { this.name = name ; }
	
	public int getAllowNextMatchDistance() { return this.allowNextMatchDistance ; }
	public void setAllowNextMatchDistance(int distance) { allowNextMatchDistance = distance ; }

	abstract public UnitMatch matches(IToken[] token, int pos) ;
	
	final protected boolean matches(StringMatcher[] matcher, String value) {
		if(matcher == null) return true ;
		for(StringMatcher sel : matcher) {
			if(sel.matches(value)) return true ;
		}
		return false  ;
	}

	final protected boolean matches(StringMatcher[] matcher, String[] value) {
		if(matcher == null) return true ;
		for(StringMatcher sel : matcher) {
			for(String selValue : value) {
				if(sel.matches(selValue)) return true ;
			}
		}
		return false  ;
	}
}
