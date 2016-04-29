package org.headvances.analysis.swui;

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

import org.headvances.analysis.swui.xhtml.XHTMLAnalysisHistoriesPanel;
import org.headvances.cluster.ClusterClient;
import org.headvances.swingui.SwingUtil;

import com.hazelcast.core.Member;
/**
 * $Author: Tuan Nguyen$ 
 **/
public class RoleControlPanel extends JPanel implements ClientListener {
	private String role ;
	private JTree tree;
  private DefaultMutableTreeNode model;
  
	public RoleControlPanel(String role) {
		this.role = role ;
		setLayout(new BorderLayout()); 
	}

  public void onConnect(ClusterClient client) {
  	removeAll() ;
  	Set<Member> set = client.getMembers(role) ;
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
            	SwingUtil.showError(RoleControlPanel.this, "Component Info Error", ex) ;
            }
  				}
  			}
  		}
  	};
  	tree.addMouseListener(ml);
  	add(new JScrollPane(tree), BorderLayout.CENTER) ;
  	updateUI() ;
  }

  private void add(DefaultMutableTreeNode memberNode, Member member) {
    DefaultMutableTreeNode scat = new DefaultMutableTreeNode("TokenFrequency") ;
    memberNode.add(scat) ;
    scat.add(new ComponentNode(member, "Html Document TokenFrequency", "DocumentTagStatistic") {
      public void onSelect() throws Exception {
        AnalysisPane pane = AnalysisApplicationPlugin.getInstance().getAnalysisPane() ;
        WorkingWorkspace wspace = pane.getWorkingWorkspace() ;
        wspace.addTabView("Html Document TokenFrequency", new AnalysisStatisticPanel(this)) ;
      } 
    });
    scat.add(new ComponentNode(member, "Content Type TokenFrequency", "ContentTypeStatistic"){
      public void onSelect() throws Exception {
        AnalysisPane pane = AnalysisApplicationPlugin.getInstance().getAnalysisPane() ;
        WorkingWorkspace wspace = pane.getWorkingWorkspace() ;
        wspace.addTabView("Content Type TokenFrequency", new AnalysisStatisticPanel(this)) ;
      } 
    });
    
  	memberNode.add(new ComponentNode(member, "Analysis Service", "AnalysisService") {
  		public void onSelect() throws Exception {
  			AnalysisPane pane = AnalysisApplicationPlugin.getInstance().getAnalysisPane() ;
  			WorkingWorkspace wspace = pane.getWorkingWorkspace() ;
  			wspace.addTabView("Analysis Service", new AnalysisServiceInfoPanel(this)) ;
  		}
  	}) ;
  	
  	memberNode.add(new ComponentNode(member, "XHTML Analysis History", "AnalysisHistory") {
  		public void onSelect() throws Exception {
  			AnalysisPane pane = AnalysisApplicationPlugin.getInstance().getAnalysisPane() ;
  			WorkingWorkspace wspace = pane.getWorkingWorkspace() ;
  			wspace.addTabView("XHTML Analysis History", new XHTMLAnalysisHistoriesPanel(this)) ;
  		}
  	}) ;
  	
  	memberNode.add(new ComponentNode(member, "JVMInfo", "") {
  		public void onSelect() throws Exception {
  			AnalysisPane pane = AnalysisApplicationPlugin.getInstance().getAnalysisPane() ;
  			WorkingWorkspace wspace = pane.getWorkingWorkspace() ;
  			wspace.addTabView("JVM Info", new ServerJVMInfoPanel(this)) ;
  		}
  	}) ;
  }
}