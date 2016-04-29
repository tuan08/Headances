package org.headvances.search.swui.node;

import java.awt.BorderLayout;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.util.Iterator;

import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTree;
import javax.swing.tree.DefaultMutableTreeNode;
import javax.swing.tree.TreeNode;
import javax.swing.tree.TreePath;

import org.elasticsearch.cluster.ClusterState;
import org.elasticsearch.cluster.node.DiscoveryNode;
import org.elasticsearch.cluster.node.DiscoveryNodes;
import org.headvances.search.ESClient;
import org.headvances.search.swui.ElasticSearchPane;
import org.headvances.search.swui.ElasticSearchPlugin;
import org.headvances.search.swui.TreeNodeListener;
import org.headvances.swingui.SwingUtil;
/**
 * $Author: Tuan Nguyen$ 
 * $Revision$
 * $Date$
 * $LastChangedBy$
 * $LastChangedDate$
 * $URL$
 **/
public class NodeInfoPanel extends JPanel {
	private JTree tree;
  private DefaultMutableTreeNode model;
  
	public NodeInfoPanel() {
		setLayout(new BorderLayout()); 
	}

  public void onEvent(ESClient client, int event) {
  	removeAll() ;
  	ClusterState state = client.getClusterState() ;
  	DiscoveryNodes nodes = state.getNodes() ;
  	Iterator<DiscoveryNode> i = nodes.iterator() ;
  	model = new DefaultMutableTreeNode();
  	while(i.hasNext()) {
  		DiscoveryNode discoveryNode = i.next() ;
  		DefaultMutableTreeNode node = new DefaultMutableTreeNode(discoveryNode.getAddress()) ;
  		add(node, discoveryNode) ;
  		model.add(node) ;
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
            	SwingUtil.showError(NodeInfoPanel.this, "Node Info Error", ex) ;
            }
  				}
  			}
  		}
  	};
  	tree.addMouseListener(ml);
  	add(new JScrollPane(tree), BorderLayout.CENTER) ;
  }

  private void add(DefaultMutableTreeNode memberNode, DiscoveryNode node) {
  	DefaultMutableTreeNode settings = new DefaultMutableTreeNode("Settings") ;
  	memberNode.add(settings) ;
  	
  	memberNode.add(new InfoNode("OS Info", node) {
  		public void onSelect() throws Exception {
  			ElasticSearchPane pane = ElasticSearchPlugin.getInstance().getElasticSearchPane() ;
  			org.headvances.search.swui.WorkingWorkspace wspace = pane.getWorkingWorkspace() ;
  			wspace.addTabView("OS Info", new OSInfoPanel(this)) ;
  		}
  	}) ;
  
  	memberNode.add(new InfoNode("JVM Info", node) {
  		public void onSelect() throws Exception {
  			ElasticSearchPane pane = ElasticSearchPlugin.getInstance().getElasticSearchPane() ;
  			org.headvances.search.swui.WorkingWorkspace wspace = pane.getWorkingWorkspace() ;
  			wspace.addTabView("JVM Info", new JVMInfoPanel(this)) ;
  		}
  	}) ;
  	
  	memberNode.add(new InfoNode("Process Info", node) {
  		public void onSelect() throws Exception {
  			ElasticSearchPane pane = ElasticSearchPlugin.getInstance().getElasticSearchPane() ;
  			org.headvances.search.swui.WorkingWorkspace wspace = pane.getWorkingWorkspace() ;
  			wspace.addTabView("Process Info", new ProcessInfoPanel(this)) ;
  		}
  	}) ;
  	
  	memberNode.add(new InfoNode("Network Info", node) {
  		public void onSelect() throws Exception {
  			ElasticSearchPane pane = ElasticSearchPlugin.getInstance().getElasticSearchPane() ;
  			org.headvances.search.swui.WorkingWorkspace wspace = pane.getWorkingWorkspace() ;
  			wspace.addTabView("Network Info", new NetworkInfoPanel(this)) ;
  		}
  	}) ;
  	
  	DefaultMutableTreeNode transportInfo = new DefaultMutableTreeNode("Transport Info") ;
  	memberNode.add(transportInfo) ;
  }
}