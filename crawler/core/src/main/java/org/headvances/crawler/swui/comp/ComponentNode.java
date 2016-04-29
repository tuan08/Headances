package org.headvances.crawler.swui.comp;

import javax.swing.tree.DefaultMutableTreeNode;

import com.hazelcast.core.Member;
/**
 * $Author: Tuan Nguyen$ 
 **/
public class ComponentNode extends DefaultMutableTreeNode implements TreeNodeListener {
	private Member member ;
	private String componentId ;
	private String desc ;

	public ComponentNode(Member member, String componentId, String desc) {
	  super(componentId);
	  this.member = member ;
	  this.componentId = componentId ;
	  this.desc = desc ;
  }

	public ComponentNode(String name, Member member, String componentId, String desc) {
	  super(name);
	  this.member = member ;
	  this.componentId = componentId ;
	  this.desc = desc ;
  }
	
	public Member getMember() { return this.member ; }
	
	public String getComponentId() { return componentId ; }
	
	public String getDescription() { return this.desc ; }
	
	public void onSelect() throws Exception {
	}
}