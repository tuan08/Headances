package org.headvances.html.dom.tagger;

import org.headvances.html.dom.TDocument;
import org.headvances.html.dom.TNode;
import org.headvances.html.dom.TNodeGroup;
import org.headvances.html.dom.TNodeGroupByXPath;
import org.headvances.html.dom.selector.Selector;
/**
 * $Author: Tuan Nguyen$ 
 **/
abstract public class Tagger {
	static public TNode[] EMPTY = {} ;
	
	abstract public TNode[] tag(TDocument tdoc, TNode node) ;
	
	protected TNodeGroupByXPath[] doSelect(TNode root, Selector selector, int maxDiffDistance) {
		TNode[] nodes = root.select(selector) ;
		return TNodeGroupByXPath.group(nodes, maxDiffDistance) ;
	}
	
	protected TNodeGroup[] select(TNode root, Selector selector, int maxDiffDistance) {
		TNode[] nodes = root.select(selector, false) ;
		return TNodeGroup.groupByCommonAncestor(nodes, maxDiffDistance) ;
	}
	
	protected void dump(TNodeGroupByXPath[] groups) {
		System.out.println("Group Count: " + groups.length);
		for(TNodeGroupByXPath group : groups) {
			System.out.println("Group: " + group.xpath);
			for(TNode sel : group.getTNodes()) {
				System.out.println("       " + sel.getXPath().toString());
			}
			System.out.println(group.getCommonAncestor().getTextContent());
		}
	}
	
	protected void dump(TNodeGroup[] groups) {
		System.out.println("Group Count: " + groups.length);
		for(TNodeGroup group : groups) {
			System.out.println("Group: " + group.getCommonAncestor().getXPath());
			for(TNode sel : group.getTNodes()) {
				System.out.println("       " + sel.getXPath().toString());
			}
			System.out.println(group.getCommonAncestor().getTextContent());
		}
	}
}