package org.headvances.analysis.swui;

import javax.swing.tree.DefaultMutableTreeNode;

import com.hazelcast.core.Member;
/**
 * $Author: Tuan Nguyen$ 
 **/
public class ComponentNode extends DefaultMutableTreeNode implements TreeNodeListener {
	private Member member ;
	private String componentId ;

	public ComponentNode(Member member, String name, String componentId) {
	  super(name);
	  this.member = member;
	  this.componentId = componentId ;
	}

	public Member getMember() { return this.member ; }
	
	public String getComponentId() { return componentId ; }
	
	public void onSelect() throws Exception {
	}
	
}