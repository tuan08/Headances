package org.headvances.crawler.swui.comp;

import java.awt.BorderLayout;
import java.util.List;

import javax.swing.BoxLayout;
import javax.swing.JComponent;
import javax.swing.JLabel;
import javax.swing.JPanel;

import org.headvances.cluster.ClusterClient;
import org.headvances.crawler.cluster.task.GetQueueInfoTask;
import org.headvances.crawler.swui.CrawlerContext;
import org.headvances.jms.queue.QueueCallInfo;
import org.headvances.jms.queue.QueueInfo;
import org.headvances.swingui.component.TableView;

/**
 * $Author: Tuan Nguyen$ 
 **/
public class QueueInfoPanel extends ComponentPanel {
	private QueueNode queueNode ;
	private QueueCallInfoPanel enqueueInfoPanel ;
	private QueueCallInfoPanel dequeueInfoPanel ;

	public QueueInfoPanel(QueueNode queueNode) throws Exception {
		super() ;
		this.queueNode = queueNode ;
		update() ;
	}
	
	protected JComponent createBodyPane() {
		JPanel pane = new JPanel() ;
		pane.setLayout(new BoxLayout(pane, BoxLayout.Y_AXIS));

		enqueueInfoPanel = new QueueCallInfoPanel() ;
		pane.add(enqueueInfoPanel) ;
		dequeueInfoPanel = new QueueCallInfoPanel() ;
		pane.add(dequeueInfoPanel) ;
		return pane ;
	}
	
	protected void update() throws Exception {
		GetQueueInfoTask task = 
			new GetQueueInfoTask(queueNode.getSchedulerId(), queueNode.getListenerId()) ;
		ClusterClient client = CrawlerContext.getInstance().getCrawlerClient() ;
		QueueInfo info = client.execute(task, queueNode.getMember()) ;
		populate(info) ;
	}
	
	public void populate(QueueInfo info) {
		enqueueInfoPanel.populate(info.getEnqueueInfo()) ;
		dequeueInfoPanel.populate(info.getDequeueInfo()) ;
	}
	
	public class QueueCallInfoPanel extends JPanel {
		private JLabel title ;
		private TableView infoTable ;

		public QueueCallInfoPanel() {
			setLayout(new BorderLayout()) ;
			this.title = new JLabel() ;
			add(title, BorderLayout.NORTH) ;
			this.infoTable = new TableView() ;
			add(infoTable, BorderLayout.CENTER) ;
		}
		
		public void populate(QueueCallInfo info) {
			if(info == null) return ;
			title.setText(info.getComponentId()) ;
			String[] header = { "Name", "Value" } ;
			List<String> messages = info.getMessages() ;
			Object[][] data = new Object[messages.size() + 2][] ;
			data[0] = new Object[] { "Call", info.getCount() } ;
			data[1] = new Object[] { "Error", info.getErrorCount() } ;
			for(int i = 0; i < messages.size(); i++) {
				data[i + 2] = new Object[] { "Message", messages.get(i) } ;
			}
			infoTable.setData(header, data) ;
		}
	}
}