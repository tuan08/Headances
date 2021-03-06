package org.headvances.html.dom.selector;

import org.headvances.html.dom.TNode;

/**
 * $Author: Tuan Nguyen$ 
 **/
public class TagSelector implements Selector {
	private String[] tag ;

	public TagSelector(String ... tag) {
		this.tag = tag  ;
	}

	public boolean isSelected(TNode node) {
		if(tag.length == 1) return node.hasTag(tag[0]) ;
		return node.hasTag(tag) ;
	}
}