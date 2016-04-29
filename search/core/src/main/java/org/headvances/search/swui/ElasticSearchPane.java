package org.headvances.search.swui;

import java.awt.BorderLayout;
import java.awt.Dimension;
import java.awt.event.ActionEvent;
import java.util.Date;

import javax.swing.AbstractAction;
import javax.swing.Action;
import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.JSplitPane;

import org.headvances.data.Document;
import org.headvances.search.ESDocumentClient;
import org.headvances.search.swui.index.ImportFolderPanel;
import org.headvances.search.swui.search.SearchPanel;
import org.headvances.swingui.SwingUtil;
import org.headvances.swingui.component.JMenuBarExt;
import org.headvances.util.IOUtil;
import org.headvances.util.text.DateUtil;
/**
 * $Author: Tuan Nguyen$ 
 **/
public class ElasticSearchPane extends JPanel {
	private JMenuBarExt menuBar ;
	private ControlWorkspace cspace ;
	private WorkingWorkspace wspace ;
	
	public ElasticSearchPane() {
		setLayout(new BorderLayout()) ;
		
		menuBar = initMenuBar() ;
		add(menuBar, BorderLayout.NORTH) ;
		
		cspace  = new ControlWorkspace() ;
		wspace  = new WorkingWorkspace() ;
		JSplitPane splitPane = new JSplitPane(JSplitPane.HORIZONTAL_SPLIT, cspace, wspace);
		splitPane.setOneTouchExpandable(true);
		splitPane.setDividerLocation(300);
		splitPane.setPreferredSize(new Dimension(1000, 650));
		add(splitPane, BorderLayout.CENTER) ;
	}
	
	public ControlWorkspace getControlWorkspace() { return this.cspace ; }
	
	public WorkingWorkspace getWorkingWorkspace() { return this.wspace ; }
	
	private JMenuBarExt initMenuBar() {
		JMenuBarExt menuBar = new JMenuBarExt() ;
		Action startSingleNode = new AbstractAction("Single Node") {
      public void actionPerformed(ActionEvent e) {
      	try {
      		ElasticSearchContext.getInstance().startElasticSearchCluster(1) ;
      	} catch (Throwable ex) {
      		SwingUtil.showError(ElasticSearchPane.this, "Start Single Node", ex) ;
      	}
      }
		};
		menuBar.registerMenuAction("Cluster > Start", startSingleNode) ;
		
		Action start3Node = new AbstractAction("3 Nodes") {
      public void actionPerformed(ActionEvent e) {
      	try {
      		ElasticSearchContext.getInstance().startElasticSearchCluster(3) ;
      	} catch (Throwable ex) {
      		SwingUtil.showError(ElasticSearchPane.this, "Start 3 Nodes", ex) ;
      	}
      }
		};
		menuBar.registerMenuAction("Cluster > Start", start3Node) ;
		
		Action connectCrawlerClient = new AbstractAction("Connect ESClient") {
      public void actionPerformed(ActionEvent e) {
      	try {
      		JFrame jframe = SwingUtil.findAncestorOfType(ElasticSearchPane.this, JFrame.class) ;
      		ESClientConnectionPanel connPanel = new ESClientConnectionPanel(jframe) ;
      	} catch (Throwable ex) {
      		SwingUtil.showError(ElasticSearchPane.this, "ESClient Connection", ex) ;
      	}
      }
		};
		menuBar.registerMenuAction("Cluster", connectCrawlerClient) ;
		
		menuBar.registerMenuAction("Data > Generate", new GenerateAction("50   IDocumentTest Record",  50)) ;
		menuBar.registerMenuAction("Data > Generate", new GenerateAction("100  IDocumentTest Record", 100)) ;
		menuBar.registerMenuAction("Data > Generate", new GenerateAction("1000 IDocumentTest Record", 1000)) ;
		
		Action importFolder = new AbstractAction("Import From Folder") {
      public void actionPerformed(ActionEvent e) {
      	try {
      		JFrame jframe = SwingUtil.findAncestorOfType(ElasticSearchPane.this, JFrame.class) ;
      		ImportFolderPanel importPanel = new ImportFolderPanel(jframe) ;
      	} catch (Throwable ex) {
      		SwingUtil.showError(ElasticSearchPane.this, "Import", ex) ;
      	}
      }
		};
		menuBar.registerMenuAction("Data > Import", importFolder) ;
		
		Action query = new AbstractAction("Query") {
      public void actionPerformed(ActionEvent e) {
      	try {
      		wspace.addTabView("Query", new SearchPanel()) ;
      	} catch (Throwable ex) {
      		SwingUtil.showError(ElasticSearchPane.this, "Query", ex) ;
      	}
      }
		};
		menuBar.registerMenuAction("Data", query) ;
		return menuBar ;
	}
	
	public JMenuBarExt getMenuBar() { return this.menuBar ; }
	
	public class GenerateAction extends AbstractAction {
		private int number ;
		public GenerateAction(String label, int number) {
			super(label) ;
			this.number = number ;
		}
		public void actionPerformed(ActionEvent e) {
			new Thread() {
				public void run() {
					try {
						ESDocumentClient<Document> esclient = 
							new ESDocumentClient<Document>(ElasticSearchContext.getInstance().getESClient(), "test", Document.class) ;
						if(!esclient.hasIndex(esclient.getIndex())) {
							esclient.createIndex(
								esclient.getIndex(),
								IOUtil.getResourceAsString("TDocument.setting.json", "UTF8")
							) ;
							esclient.updateMapping(IOUtil.getResourceAsString("TDocument.mapping.json", "UTF8")) ;
						}
						Date currTime = new Date() ;
						Date pastTime = DateUtil.parseCompactDate("1/1/2011") ;
						for(int i = 0; i < number; i++) {
							Date selTime = currTime ;
							if(i % 2 == 0) selTime = pastTime ;
							Document idoc = Document.createSample(i, "system", selTime, "generate sample content") ;
							esclient.put(idoc) ;
						}
						ElasticSearchContext.getInstance().broadcast(ESClientListener.UPDATE) ;
					} catch (Throwable ex) {
						ex.printStackTrace() ;
						SwingUtil.showError(ElasticSearchPane.this, "Generate", ex) ;
					}
				}
			}.start() ;
		}
	}
}