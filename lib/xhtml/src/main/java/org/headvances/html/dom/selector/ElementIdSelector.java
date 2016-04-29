package org.headvances.html.dom.selector;

import org.headvances.html.dom.TNode;
import org.headvances.util.text.StringMatcher;

/**
 * $Author: Tuan Nguyen$ 
 **/
public class ElementIdSelector implements Selector {
	private StringMatcher[] matcher ;

	public ElementIdSelector(String exp) {
		this.matcher = new StringMatcher[] { new StringMatcher(exp.toLowerCase()) } ;
	}

	public ElementIdSelector(String[] exp) {
		this.matcher = new StringMatcher[exp.length] ;
		for(int i = 0; i < matcher.length; i++) {
			matcher[i] = new StringMatcher(exp[i].toLowerCase()) ;
		}
	}

	public boolean isSelected(TNode node) {
		String eleId = node.getElementId() ;
		if(eleId == null) return false ;
		for(StringMatcher sel : matcher) {
			if(sel.matches(eleId)) return true ;
		}
		return false ;
	}
}