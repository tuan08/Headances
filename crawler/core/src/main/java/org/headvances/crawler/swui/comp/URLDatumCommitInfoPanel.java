package org.headvances.crawler.swui.comp;

import java.util.Date;
import java.util.List;

import javax.swing.JComponent;
import javax.swing.JScrollPane;

import org.headvances.cluster.ClusterClient;
import org.headvances.crawler.cluster.task.GetURLDatumCommitInfoTask;
import org.headvances.crawler.master.URLDatumCommitInfo;
import org.headvances.crawler.swui.CrawlerContext;
import org.headvances.swingui.component.TableView;
/**
 * $Author: Tuan Nguyen$ 
 **/
public class URLDatumCommitInfoPanel extends ComponentPanel {
	private ComponentNode    compNode ;
	private TableView table ;
	
	private JScrollPane scrollPanel;
	
	public URLDatumCommitInfoPanel(ComponentNode compNode) throws Exception {
		this.compNode = compNode ;
		update() ;
	}
	
	protected JComponent createBodyPane() {
		table = new TableView() ;
		scrollPanel = new JScrollPane(table);
		return scrollPanel ;
	}
	
	protected void update() throws Exception {
		GetURLDatumCommitInfoTask task = new GetURLDatumCommitInfoTask() ;
		ClusterClient client = CrawlerContext.getInstance().getCrawlerClient() ;
		List<URLDatumCommitInfo> infos = client.execute(task, compNode.getMember()) ;
		String[]   header = {
			"Time", "Exec Time", "Commit URL", "New URL", "New URL List", "New URL Detail"
		} ;
		Object[][] data = new Object[infos.size()][] ;
		int idx = 0 ;
		for(URLDatumCommitInfo sel : infos) {
			data[idx++] = new Object[] {
			  new Date(sel.getTime()), 
			  sel.getExecTime(),
			  sel.getURLCommitCount(),
			  sel.getNewURLFoundCount(),
			  sel.getNewURLTypeList(),
			  sel.getNewURLTypeDetail()
			};
		}
		table.setData(header, data) ;
	}
}