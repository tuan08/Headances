package org.headvances.crawler.swui.comp;

import javax.swing.tree.DefaultMutableTreeNode;

import org.headvances.crawler.swui.CrawlerApplicationPlugin;
import org.headvances.crawler.swui.CrawlerPane;
import org.headvances.crawler.swui.WorkingWorkspace;

import com.hazelcast.core.Member;
/**
 * $Author: Tuan Nguyen$ 
 **/
public class QueueNode extends DefaultMutableTreeNode implements TreeNodeListener {
	private Member member ;
	private String schedulerId ;
	private String listenerId ;

	public QueueNode(Member member, String name, String schedulerId, String listenerId) {
	  super(name);
	  this.member = member ;
	  this.schedulerId = schedulerId ;
	  this.listenerId = listenerId ;
  }

	public Member getMember() { return this.member ; }
	
	public String getSchedulerId() { return schedulerId ; }
	
	public String getListenerId() { return this.listenerId ; }
	
	public void onSelect() throws Exception {
		CrawlerPane pane = CrawlerApplicationPlugin.getInstance().getCrawlerPane() ;
		WorkingWorkspace wspace = pane.getWorkingWorkspace() ;
		wspace.addTabView("Queue", new QueueInfoPanel(this)) ;
	}
}