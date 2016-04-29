package org.headvances.nlp.token;
/**
 * $Author: Tuan Nguyen$ 
 **/
public interface TokenSequenceIterator extends TokenIterator {
	public TokenCollection next() throws TokenException ;
}
