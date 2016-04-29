package org.headvances.nlp.token.analyzer;

import org.headvances.nlp.token.IToken;
import org.headvances.nlp.token.TokenCollection;
import org.headvances.nlp.token.TokenException;
/**
 * $Author: Tuan Nguyen$ 
 **/
public interface TokenCollectionAnalyzer {
	public TokenCollection[] analyze(IToken[] tokens) throws TokenException ;
}