package org.headvances.html.dom.selector;

import org.headvances.html.dom.TNode;
import org.headvances.util.text.StringUtil;

/**
 * $Author: Tuan Nguyen$ 
 **/
public class TextLengthSelector implements Selector {
	private int minLength = 100000, maxLength ;

	public TextLengthSelector(int min, int max) {
		this.minLength = min ;
		this.maxLength = max ;
	}

	public boolean isSelected(TNode node) {
		String ntext = node.getNodeValue() ;
		if(StringUtil.isEmpty(ntext)) return false ;
		if(ntext.length() >= minLength && ntext.length() <= maxLength) return true ;
		return false;
	}
}