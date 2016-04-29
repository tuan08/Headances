package org.headvances.html.dom.tagger;

import org.headvances.html.dom.TDocument;
import org.headvances.html.dom.TNode;
import org.headvances.html.dom.selector.CssClassSelector;
import org.headvances.html.dom.selector.ElementIdSelector;
import org.headvances.html.dom.selector.OrSelector;
import org.headvances.html.dom.selector.Selector;
import org.headvances.html.dom.selector.TNodeSelector;
/**
 * $Author: Tuan Nguyen$ 
 **/
public class JunkTextBlockTagger extends Tagger {
	final static public String BLOCK_TEXT_JUNK = "block:text:junk" ;
	
	final static public String[] PATTERN = {
		"*navigation*", "*nav-*", "*nav_*",  "*-nav*", "*_nav*", 
		"*menu*", "*breadcumbs*", "*sidebar*",
		"*banner*", "*footer*" 
	};

	final static public String[] JUNK_TEXT_NODE_NAME = {
		"head", "meta", "input", "select", "textarea", "button"
	};
	
	private Selector selector ;
	
	public JunkTextBlockTagger() {
		Selector cssSelector = new CssClassSelector(PATTERN) ;
		Selector idSelector  = new ElementIdSelector(PATTERN) ;
		Selector nodeNameSelector  = new TNodeSelector(JUNK_TEXT_NODE_NAME) ;
		selector = new OrSelector(cssSelector, idSelector, nodeNameSelector) ;
	}
	
	public TNode[] tag(TDocument tdoc, TNode node) {
		TNode[] nodes = node.select(selector) ;
		for(TNode sel : nodes) {
			sel.addTag(BLOCK_TEXT_JUNK) ;
		}
		return nodes ;
	}
}