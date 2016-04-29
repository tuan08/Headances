package org.headvances.html.dom.selector;

import org.headvances.html.dom.TNode;
import org.headvances.util.text.StringMatcher;

/**
 * $Author: Tuan Nguyen$ 
 **/
public class URLPatternSelector implements Selector {
	private StringMatcher[] matcher ;
	
	public URLPatternSelector(String ... pattern) {
		matcher = new StringMatcher[pattern.length] ;
		for(int i = 0; i < pattern.length; i++) {
			matcher[i] = new StringMatcher(pattern[i].toLowerCase()) ;
		}
	}
	
	public boolean isSelected(TNode node) {
		String nodeName = node.getNodeName() ;
		if(!"a".equals(nodeName)) return false ;
		String url = node.getAttribute("href") ;
		if(url == null) return false; 
		for(StringMatcher sel : matcher) {
			if(sel.matches(url)) return true ;
		}
		return false ;
	}
}