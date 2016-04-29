package org.headvances.nlp.query2.match;

import org.headvances.nlp.util.ParamHolder;

/**
 * $Author: Tuan Nguyen$ 
 **/
public class WordUnitMatcher extends TreeWordMatcher {
	private String[] word ;

	public WordUnitMatcher(ParamHolder pholder, int allowNextMatchDistance) {
		setAllowNextMatchDistance(allowNextMatchDistance) ;
		this.word = pholder.getFieldValue("word") ;
		addWord(word) ;
	}
}