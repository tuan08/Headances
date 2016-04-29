package org.headvances.search.swui.index;

import java.awt.BorderLayout;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.util.Iterator;
import java.util.Map;

import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTree;
import javax.swing.tree.DefaultMutableTreeNode;
import javax.swing.tree.TreeNode;
import javax.swing.tree.TreePath;

import org.elasticsearch.action.admin.indices.status.IndexShardStatus;
import org.elasticsearch.action.admin.indices.status.IndexStatus;
import org.headvances.search.ESClient;
import org.headvances.search.swui.ESClientListener;
import org.headvances.search.swui.ElasticSearchContext;
import org.headvances.search.swui.ElasticSearchPane;
import org.headvances.search.swui.ElasticSearchPlugin;
import org.headvances.search.swui.TreeNodeListener;
import org.headvances.swingui.SwingUtil;
/**
 * $Author: Tuan Nguyen$ 
 **/
public class IndexInfoPanel extends JPanel {
	private JTree tree;
  private DefaultMutableTreeNode model;
  private IndexStatus[] istatus;
  private ESClient esclient;
  
	public IndexInfoPanel() {
		setLayout(new BorderLayout()); 
	}
	
  public void onEvent(ESClient client, int event) {
  	removeAll() ;
  	this.esclient = client;
  	istatus = esclient.getIndexStatus() ;
  	model = new DefaultMutableTreeNode();
  	for(int i = 0; i < istatus.length; i++) {
  		DefaultMutableTreeNode node = new DefaultMutableTreeNode(istatus[i].getIndex()) ;
  		add(node, istatus[i], i) ;
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
            	SwingUtil.showError(IndexInfoPanel.this, "Node Info Error", ex) ;
            }
  				}
  			}
  		}
  	};
  	tree.addMouseListener(ml);
  	for(int i = 0; i < tree.getRowCount(); i++)
  	  tree.expandRow(i);
  	add(new JScrollPane(tree), BorderLayout.CENTER) ;
  }
  
  public void update() throws Exception{
    ElasticSearchContext.getInstance().broadcast(ESClientListener.UPDATE);
  }

  private void add(DefaultMutableTreeNode indexNode, final IndexStatus iStatus, final int index) {
  	indexNode.add(new InfoNode("Index Status") {
  		public void onSelect() throws Exception {
  			ElasticSearchPane pane = ElasticSearchPlugin.getInstance().getElasticSearchPane() ;
  			org.headvances.search.swui.WorkingWorkspace wspace = pane.getWorkingWorkspace() ;
  			wspace.addTabView("Index Status", new IndexStatusPanel(iStatus)) ;
  		}
  	}) ;
  	
  	DefaultMutableTreeNode shards = new DefaultMutableTreeNode("Shards") ;
  	indexNode.add(shards) ;
  	Map<Integer, IndexShardStatus> shardMap = iStatus.getShards() ;
  	Iterator<IndexShardStatus> i = shardMap.values().iterator() ;
  	while(i.hasNext()) {
  		final IndexShardStatus ishardStatus = i.next() ;
  		shards.add(new InfoNode("Shard " + ishardStatus.getShardId().getId()) {
    		public void onSelect() throws Exception {
    			ElasticSearchPane pane = ElasticSearchPlugin.getInstance().getElasticSearchPane() ;
    			org.headvances.search.swui.WorkingWorkspace wspace = pane.getWorkingWorkspace() ;
    			wspace.addTabView("Shard Status", new IndexShardStatusPanel(iStatus, ishardStatus)) ;
    		}
    	}) ;
  	}
  }
}