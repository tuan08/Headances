package org.headvances.crawler.swui.fetcher;

import java.awt.Point;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;

import javax.swing.JComponent;
import javax.swing.JScrollPane;
import javax.swing.JSplitPane;
import javax.swing.JTabbedPane;

import org.headvances.cluster.ClusterClient;
import org.headvances.crawler.cluster.task.GetFetcherInfoTask;
import org.headvances.crawler.fetch.http.FetcherInfo;
import org.headvances.crawler.swui.CrawlerContext;
import org.headvances.crawler.swui.comp.ComponentNode;
import org.headvances.crawler.swui.comp.ComponentPanel;
import org.headvances.swingui.component.JTablePagination;
import org.headvances.swingui.component.TableView;
import org.headvances.xhtml.url.URLDatum;
/**
 * $Author: Tuan Nguyen$ 
 * REVIEW: Use the JTablePagination and make all the column sortable.
 **/
public class HttpFetcherPanel extends ComponentPanel {
	private ComponentNode    compNode ;
	private List<FetcherInfo> fetcherInfos ;
	
	private JTablePagination listPanel ;
	private DetailPanel detailPanel ;
	
	private JScrollPane listScrollPanel;
	
	public HttpFetcherPanel(ComponentNode compNode) throws Exception {
		this.compNode = compNode ;
		update() ;
	}
	
	protected JComponent createBodyPane() {
		listPanel = new JTablePagination() ;
		listPanel.getJTable().addMouseListener(new MouseAdapter(){     
      public void mouseClicked(MouseEvent e) {
        Point p = e.getPoint();
        int viewRow = listPanel.getJTable().rowAtPoint(p);
        if (viewRow >= 0) {
          detailPanel.update(fetcherInfos.get(viewRow));
        }
        System.out.println("select row: " + listPanel.getJTable().convertRowIndexToModel(viewRow));
      }
    });
		
		detailPanel = new DetailPanel() ;
		//REVIEW:  add JScrollPane for listPanel and detailPanel. !! PS: detailPanel's JScrollPane is exist.
		listScrollPanel = new JScrollPane(listPanel);
		JSplitPane splitPane = 
			new JSplitPane(JSplitPane.VERTICAL_SPLIT, listPanel, detailPanel);
		splitPane.setOneTouchExpandable(true);
    splitPane.setDividerLocation(250);
		return splitPane ;
	}
	
	protected void update() throws Exception {
		GetFetcherInfoTask task = new GetFetcherInfoTask() ;
		ClusterClient client = CrawlerContext.getInstance().getCrawlerClient() ;
		fetcherInfos = client.execute(task, compNode.getMember()) ;
		populate() ;
	}
	
	public void populate() {
		if(fetcherInfos == null) return ;
		String[]   header = {
		  "#", "Fetch", "RC 100", "RC 200", "RC 300", "RC 400", "RC 500", "RC Other"
		} ;
		Object[][] data   = new Object[fetcherInfos.size()][] ;
		for(int i = 0; i < fetcherInfos.size(); i++) {
			FetcherInfo config = fetcherInfos.get(i) ;
			data[i] = new Object[] {
				i,
				config.getFetchCount(),
				config.getResponseCodeGroup100(),
				config.getResponseCodeGroup200(),
				config.getResponseCodeGroup300(),
				config.getResponseCodeGroup400(),
				config.getResponseCodeGroup500(),
				config.getResponseCodeGroup10000(),
			};
		}
		listPanel.setData(header, data, 50) ;
		detailPanel.update(fetcherInfos.get(0)) ;
	}
	
	static public class DetailPanel extends JTabbedPane {
		URLDatumFetchHistoryPanel urldatumHistoryPanel ;
		public DetailPanel() {
			setTabLayoutPolicy(JTabbedPane.SCROLL_TAB_LAYOUT);
			urldatumHistoryPanel = new URLDatumFetchHistoryPanel() ;
			add("URL", new JScrollPane(urldatumHistoryPanel));
		}
		
		public void update(FetcherInfo info) {
			urldatumHistoryPanel.update(info) ;
		}
	}
	
	static public class URLDatumFetchHistoryPanel extends TableView {
		public void update(FetcherInfo info) {
			String[] header = { "URL" } ;
			LinkedList<URLDatum> urls = info.getRecentFetchUrl() ;
			Object[][] data = new Object[urls.size()][] ;
			Iterator<URLDatum> i = urls.iterator() ;
			int idx = 0 ;
			while(i.hasNext()) {
				URLDatum urldatum = i.next() ;
				data[idx] = new Object[] {
					urldatum.getOriginalUrl()
				};
				idx++ ;
			}
			setData(header, data) ;
		}
	}
}