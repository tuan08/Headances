package org.headvances.nlp.token.analyzer;

import org.headvances.nlp.token.IToken;
import org.headvances.nlp.token.TokenException;
/**
 * $Author: Tuan Nguyen$ 
 **/
public interface TokenAnalyzer {
	static public TokenAnalyzer NONE = new TokenAnalyzer() {
    public IToken[] analyze(IToken[] unit) throws TokenException {
	    return unit;
    }
	};
	
	public IToken[] analyze(IToken[] unit) throws TokenException ;
}