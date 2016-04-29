package org.headvances.crawler.swui.fetcher;

import java.awt.BorderLayout;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.net.InetSocketAddress;
import java.util.Iterator;
import java.util.Set;

import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTree;
import javax.swing.tree.DefaultMutableTreeNode;
import javax.swing.tree.TreeNode;
import javax.swing.tree.TreePath;

import org.headvances.cluster.ClusterClient;
import org.headvances.crawler.swui.CrawlerApplicationPlugin;
import org.headvances.crawler.swui.CrawlerPane;
import org.headvances.crawler.swui.WorkingWorkspace;
import org.headvances.crawler.swui.comp.ComponentNode;
import org.headvances.crawler.swui.comp.QueueNode;
import org.headvances.crawler.swui.comp.TreeNodeListener;
import org.headvances.crawler.swui.jvm.ServerJVMInfoPanel;
import org.headvances.crawler.swui.site.SiteContextPanel;
import org.headvances.swingui.SwingUtil;

import com.hazelcast.core.Member;
/**
 * $Author: Tuan Nguyen$ 
 **/
public class FetcherControlPanel extends JPanel {
	private JTree tree;
  private DefaultMutableTreeNode model;
  
	public FetcherControlPanel() {
		setLayout(new BorderLayout()); 
	}

  public void onConnect(ClusterClient client) {
  	removeAll() ;
  	Set<Member> set = client.getMembers("fetcher") ;
  	Iterator<Member> i = set.iterator() ;
  	model = new DefaultMutableTreeNode();
  	while(i.hasNext()) {
  		Member member = i.next() ;
  		InetSocketAddress addr = member.getInetSocketAddress() ;
  		String name = addr.getHostName() + ":" + addr.getPort() ;
  		DefaultMutableTreeNode memberNode = new DefaultMutableTreeNode(name) ;
  		memberNode.setUserObject(member) ;
  		add(memberNode, member) ;
  		model.add(memberNode) ;
  	}
  	tree = new JTree(model);
  	tree.setRootVisible(false);
  	MouseListener ml = new MouseAdapter() {
  		public void mousePressed(MouseEvent e) {
  			if(e.getClickCount() > 1) {
  				TreePath selPath = tree.getPathForLocation(e.getX(), e.getY());
  				if(selPath == null) return ;
  				TreeNode node = (TreeNode)selPath.getLastPathComponent() ;
  				if(node instanceof TreeNodeListener) {
  					TreeNodeListener listener = (TreeNodeListener) node ;
  					try {
	            listener.onSelect() ;
            } catch (Exception ex) {
            	SwingUtil.showError(FetcherControlPanel.this, "Component Info Error", ex) ;
            }
  				}
  			}
  		}
  	};
  	tree.addMouseListener(ml);
  	add(new JScrollPane(tree), BorderLayout.CENTER) ;
  }

  private void add(DefaultMutableTreeNode memberNode, Member member) {
  	DefaultMutableTreeNode qcat = new DefaultMutableTreeNode("Queue") ;
  	memberNode.add(qcat) ;
  	qcat.add(new QueueNode(member, "URL Fetch", "URLDatumFetchQueueScheduler", "URLDatumFetchQueueListener")) ;
  	qcat.add(new QueueNode(member, "Fetch Data", "JMSChannelGateway", "DocumentConsumerQueueListener")) ;
  	
  	memberNode.add(new ComponentNode(member, "SiteContextManager", "") {
  		public void onSelect() throws Exception {
  			CrawlerPane pane = CrawlerApplicationPlugin.getInstance().getCrawlerPane() ;
  			WorkingWorkspace wspace = pane.getWorkingWorkspace() ;
  			wspace.addTabView("Site Config Manager", new SiteContextPanel(this)) ;
  		}
  	}) ;
  	memberNode.add(new ComponentNode(member, "HttpFetcherManager", "") {
  		public void onSelect() throws Exception {
  			CrawlerPane pane = CrawlerApplicationPlugin.getInstance().getCrawlerPane() ;
  			WorkingWorkspace wspace = pane.getWorkingWorkspace() ;
  			wspace.addTabView("Http Fetcher Manager", new HttpFetcherPanel(this)) ;
  		}
  	}) ;
  	memberNode.add(new ComponentNode(member, "JVMInfo", "") {
  		public void onSelect() throws Exception {
  			CrawlerPane pane = CrawlerApplicationPlugin.getInstance().getCrawlerPane() ;
  			WorkingWorkspace wspace = pane.getWorkingWorkspace() ;
  			wspace.addTabView("JVM Info", new ServerJVMInfoPanel(this)) ;
  		}
  	}) ;
  }
}