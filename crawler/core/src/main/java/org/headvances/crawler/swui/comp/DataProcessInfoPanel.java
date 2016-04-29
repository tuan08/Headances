package org.headvances.crawler.swui.comp;

import javax.swing.JComponent;

import org.headvances.cluster.ClusterClient;
import org.headvances.crawler.cluster.task.GetDataProcessInfoTask;
import org.headvances.crawler.process.DataProcessInfo;
import org.headvances.crawler.swui.CrawlerContext;
import org.headvances.swingui.component.TableView;
/**
 * $Author: Tuan Nguyen$ 
 **/
public class DataProcessInfoPanel extends ComponentPanel {
	private ComponentNode    compNode ;
	private TableView table ;
	
	public DataProcessInfoPanel(ComponentNode compNode) throws Exception {
		this.compNode = compNode ;
		update() ;
	}
	
	protected JComponent createBodyPane() {
		table = new TableView() ;
		return table ;
	}
	
	protected void update() throws Exception {
		GetDataProcessInfoTask task = new GetDataProcessInfoTask() ;
		ClusterClient client = CrawlerContext.getInstance().getCrawlerClient() ;
		DataProcessInfo info = client.execute(task, compNode.getMember()) ;
		String[]   header = { "Name", "Value" } ;
		double avgHtmlProcessTime = 0 ;
		if(info.getHtmlProcessCount() > 0) {
			avgHtmlProcessTime = info.getSumHtmlProcessTime()/(double)info.getHtmlProcessCount() ;
		}
		
		double avgProcessTime = 0 ;
		if(info.getProcessCount() > 0) {
			avgProcessTime = info.getSumProcessTime()/(double)info.getProcessCount() ;
		}
		
		Object[][] data = new Object[4][] ;
		data[0] = new Object[] {"Process Count", info.getProcessCount()} ;
		data[1] = new Object[] {"HTML Process Count", info.getHtmlProcessCount()} ;
		data[2] = new Object[] {"AVG Html Process Time", avgHtmlProcessTime} ;
		data[3] = new Object[] {"AVG Process Time", avgProcessTime} ;
		table.setData(header, data) ;
	}
}