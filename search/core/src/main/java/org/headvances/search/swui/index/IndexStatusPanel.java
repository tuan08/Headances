package org.headvances.search.swui.index;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.JButton;
import javax.swing.JComponent;
import javax.swing.JOptionPane;
import javax.swing.JTabbedPane;
import javax.swing.JToolBar;

import org.elasticsearch.action.admin.indices.status.IndexStatus;
import org.headvances.search.ESClient;
import org.headvances.search.swui.ESClientListener;
import org.headvances.search.swui.ElasticSearchContext;
import org.headvances.search.swui.ElasticSearchPane;
import org.headvances.search.swui.ElasticSearchPlugin;
import org.headvances.swingui.SwingUtil;
import org.headvances.swingui.component.TableView;
import org.headvances.swingui.component.UpdatableJPanel;
/**
 * $Author: Tuan Nguyen$ 
 **/
public class IndexStatusPanel extends UpdatableJPanel {
	private IndexStatus istatus ;
	private TableView table ;
	
	public IndexStatusPanel(IndexStatus istatus) throws Exception {
		this.istatus = istatus;
		update() ;
	}
	
	protected void initJToolBar(JToolBar toolbar) {
		JButton optimizeBtn = new JButton("Opimize");
		optimizeBtn.addActionListener(new ActionListener() {
      public void actionPerformed(ActionEvent event) {
        try {
          int choice = JOptionPane.showConfirmDialog( IndexStatusPanel.this, "Do you wish to optimize this index ?", "Confirm", JOptionPane.YES_NO_OPTION);
          if(choice == JOptionPane.YES_OPTION){
            ESClient client = ElasticSearchContext.getInstance().getESClient();
            client.optimizeIndex(istatus.getIndex()) ;
          }
        } catch (Exception ex) {
          SwingUtil.showError(IndexStatusPanel.this, "Component Optimize", ex);
        }
      }
    });
    toolbar.add(optimizeBtn);
    
		
		JButton deleteBtn = new JButton("Delete");
    deleteBtn.addActionListener(new ActionListener() {
      public void actionPerformed(ActionEvent event) {
        try {
          int choice = JOptionPane.showConfirmDialog( IndexStatusPanel.this, "Do you wish to delete this index ?", "Confirm", JOptionPane.YES_NO_OPTION);
          if(choice == JOptionPane.YES_OPTION){
            ESClient client = ElasticSearchContext.getInstance().getESClient();
            client.removeIndex(istatus.getIndex()) ;
            
            ElasticSearchPane pane = ElasticSearchPlugin.getInstance().getElasticSearchPane() ;
            org.headvances.search.swui.WorkingWorkspace wspace = pane.getWorkingWorkspace() ;
            JTabbedPane jTabpane = wspace.getJTabbedPane();
            int i = jTabpane.getSelectedIndex();
            jTabpane.remove(i);
            
            ElasticSearchContext.getInstance().broadcast(ESClientListener.UPDATE) ;
          }
        } catch (Exception ex) {
          SwingUtil.showError(IndexStatusPanel.this, "Component Delete", ex);
        }
      }
    });
    toolbar.add(deleteBtn);
  }
	
	protected JComponent createBodyPane() {
		table = new TableView() ;
		return table ;
	}
	
	protected void update() throws Exception{
	  ESClient client = ElasticSearchContext.getInstance().getESClient() ;
    istatus = client.getIndexStatus(istatus.getIndex());
    
		String[]   header = { "Name", "Value" } ;
		Object[][] data = new Object[][] {
			{"Index Name",  istatus.getIndex() } ,
			{"Statistic Of Shards",  istatus.getShards().size() } ,
			{"Doc Statistic",  istatus.getDocs().getNumDocs() } ,
			{"Doc Max",  istatus.getDocs().getMaxDoc() } ,
			{"Doc Delete",  istatus.getDocs().getDeletedDocs() } ,
		}; 
		table.setData(header, data) ;
	}
}