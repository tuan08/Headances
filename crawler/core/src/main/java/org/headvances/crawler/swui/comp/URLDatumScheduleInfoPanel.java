package org.headvances.crawler.swui.comp;

import java.util.Date;
import java.util.List;

import javax.swing.JComponent;
import javax.swing.JScrollPane;

import org.headvances.cluster.ClusterClient;
import org.headvances.crawler.cluster.task.GetURLDatumScheduleInfoTask;
import org.headvances.crawler.master.URLDatumScheduleInfo;
import org.headvances.crawler.swui.CrawlerContext;
import org.headvances.swingui.component.TableView;
/**
 * $Author: Tuan Nguyen$ 
 **/
public class URLDatumScheduleInfoPanel extends ComponentPanel {
	private ComponentNode    compNode ;
	private TableView table ;
	
	public URLDatumScheduleInfoPanel(ComponentNode compNode) throws Exception {
		this.compNode = compNode ;
		update() ;
	}
	
	protected JComponent createBodyPane() {
		table = new TableView() ;
		return  new JScrollPane(table);
	}
	
	protected void update() throws Exception {
		GetURLDatumScheduleInfoTask task = new GetURLDatumScheduleInfoTask() ;
		ClusterClient client = CrawlerContext.getInstance().getCrawlerClient() ;
		List<URLDatumScheduleInfo> infos = client.execute(task, compNode.getMember()) ;
	  
		String[]   header = {
			"Time", "Exec Time", "URL Count", "Schedule", "Delay Schedule", "Pending", "Waiting"
		} ;
		Object[][] data = new Object[infos.size()][] ;
		int idx = 0 ;
		for(URLDatumScheduleInfo sel : infos) {
			data[idx++] = new Object[] {
			  new Date(sel.getTime()), 
			  sel.getExecTime(),
			  sel.getUrlCount(),
			  sel.getScheduleCount(),
			  sel.getDelayScheduleCount(),
			  sel.getPendingCount(),
			  sel.getWaitingCount()
			};
		}
		table.setData(header, data) ;
	}
}