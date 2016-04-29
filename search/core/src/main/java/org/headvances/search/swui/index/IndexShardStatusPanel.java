package org.headvances.search.swui.index;

import javax.swing.JComponent;
import javax.swing.JSplitPane;

import org.elasticsearch.action.admin.indices.status.IndexShardStatus;
import org.elasticsearch.action.admin.indices.status.IndexStatus;
import org.elasticsearch.action.admin.indices.status.ShardStatus;
import org.headvances.search.ESClient;
import org.headvances.search.swui.ElasticSearchContext;
import org.headvances.swingui.component.TableView;
import org.headvances.swingui.component.UpdatableJPanel;
/**
 * $Author: Tuan Nguyen$ 
 **/
public class IndexShardStatusPanel extends UpdatableJPanel {
	private IndexStatus iStatus;
  private IndexShardStatus ishardStatus ;
	private TableView indexShardPanel ;
	private TableView shardStatusPanel ;
	
	public IndexShardStatusPanel(IndexStatus istatus, IndexShardStatus ishardStatus) throws Exception {
	  this.iStatus = istatus;
	  this.ishardStatus = ishardStatus;
		update() ;
	}

	protected JComponent createBodyPane() {
		indexShardPanel  = new TableView() ;
		shardStatusPanel = new TableView() ;
		JSplitPane splitPane = 
			new JSplitPane(JSplitPane.VERTICAL_SPLIT, indexShardPanel, shardStatusPanel);
		splitPane.setOneTouchExpandable(true);
		return splitPane ;
	}
	
	protected void update() throws Exception {
	  ESClient client = ElasticSearchContext.getInstance().getESClient() ;
    iStatus = client.getIndexStatus(iStatus.getIndex());
    ishardStatus = iStatus.getShards().get(ishardStatus.getShardId().getId());
	  
		String[]   header = { "Name", "Value" } ;
		
		Object[][] data = new Object[][] {
			{"Shard Id",  ishardStatus.getShardId().getId() } ,
			{"Store Size",  ishardStatus.getStoreSize() } ,
			{"Primary Store Size",  ishardStatus.getPrimaryStoreSize() } ,
			{"", ""},
			{"Doc Statistic",  ishardStatus.getDocs().getNumDocs() } ,
			{"Doc Max",  ishardStatus.getDocs().getMaxDoc() } ,
			{"Doc Delete",  ishardStatus.getDocs().getDeletedDocs() } ,
			{"", ""},
			{"Merge Current",  ishardStatus.getMergeStats().current() } ,
			{"Merge Total",  ishardStatus.getMergeStats().total() } ,
			{"Merge Total Time",  ishardStatus.getMergeStats().totalTimeInMillis() } ,
			{"", ""},
			{"Refresh Total",  ishardStatus.getRefreshStats().total() } ,
			{"Refresh Total Time",  ishardStatus.getRefreshStats().totalTimeInMillis() } ,
		}; 
		indexShardPanel.setData(header, data) ;
		
		ShardStatus[] sStatus = ishardStatus.getShards() ;
		String[] shardStatusHeader = { 
		  "Node"
		} ;
		data = new Object[sStatus.length][] ;
		for(int i = 0; i < sStatus.length; i++) {
			data[i] = new Object[] {
				sStatus[i].getShardRouting().shortSummary()
			};
		}
		shardStatusPanel.setData(shardStatusHeader, data) ;
	}
}