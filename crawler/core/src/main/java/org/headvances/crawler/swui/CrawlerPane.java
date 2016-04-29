package org.headvances.crawler.swui;

import java.awt.BorderLayout;
import java.awt.Dimension;
import java.awt.event.ActionEvent;
import java.io.File;

import javax.swing.AbstractAction;
import javax.swing.Action;
import javax.swing.JFileChooser;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JSplitPane;

import org.headvances.cluster.ClusterClient;
import org.headvances.cluster.task.TaskResult;
import org.headvances.crawler.cluster.task.ClearSiteConfigTask;
import org.headvances.crawler.cluster.task.ClusterServiceTask;
import org.headvances.crawler.cluster.task.ClusterServiceTask.Command;
import org.headvances.crawler.cluster.task.InjectURLTask;
import org.headvances.crawler.cluster.task.ModifySiteContextTask;
import org.headvances.crawler.integration.DocumentConsumer;
import org.headvances.crawler.integration.DocumentConsumerLogger;
import org.headvances.crawler.integration.FileDocumentConsumer;
import org.headvances.crawler.swui.integration.DocumentConsumerLoggerPanel;
import org.headvances.swingui.SwingUtil;
import org.headvances.swingui.component.JMenuBarExt;
import org.headvances.swingui.component.StatusBar;
/**
 * $Author: Tuan Nguyen$ 
 **/
public class CrawlerPane extends JPanel {
	private JMenuBarExt menuBar ;
	private ControlWorkspace cspace ;
	private WorkingWorkspace wspace ;
	private StatusBar statusBar ;
	
	public CrawlerPane() {
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
		
		//REVIEW: Make the status bigger
		this.statusBar = new StatusBar() ;
		add(statusBar, BorderLayout.SOUTH) ;
	}
	
	public ControlWorkspace getControlWorkspace() { return this.cspace ; }
	
	public WorkingWorkspace getWorkingWorkspace() { return this.wspace ; }
	
	public StatusBar getStatusBar() { return this.statusBar  ; }
	
	private JMenuBarExt initMenuBar() {
		JMenuBarExt menuBar = new JMenuBarExt() ;
		Action startCrawler = new AbstractAction("Start") {
      public void actionPerformed(ActionEvent e) {
      	try {
        	ClusterClient client = CrawlerContext.getInstance().getCrawlerClient() ;
        	TaskResult.dump(client.execute(new ClusterServiceTask(Command.START))) ;
        	StatusBar statusBar = CrawlerApplicationPlugin.getInstance().getCrawlerPane().getStatusBar() ;
        	statusBar.setStatus("Crawler > Service > Start", "Start The Cluster Successfully!") ;
      	} catch (Exception ex) {
        	SwingUtil.showError(CrawlerPane.this, "Start Crawler", ex) ;
        }
      }
		};
		menuBar.registerMenuAction("Crawler > Service", startCrawler) ;
		
		Action stopCrawler = new AbstractAction("Stop") {
      public void actionPerformed(ActionEvent e) {
      	try {
        	ClusterClient client = CrawlerContext.getInstance().getCrawlerClient() ;
        	TaskResult.dump(client.execute(new ClusterServiceTask(Command.STOP))) ;
          //REVIEW: set status 
        	StatusBar statusBar = CrawlerApplicationPlugin.getInstance().getCrawlerPane().getStatusBar() ;
        	statusBar.setStatus("Crawler > Service > Stop", "Stop The Cluster Successfully!") ;
      	} catch (Exception ex) {
        	SwingUtil.showError(CrawlerPane.this, "Stop Crawler", ex) ;
        }
      }
		};
		menuBar.registerMenuAction("Crawler > Service", stopCrawler) ;
		
		Action shutdownCrawler = new AbstractAction("Shutdown") {
      public void actionPerformed(ActionEvent e) {
      	try {
        	ClusterClient client = CrawlerContext.getInstance().getCrawlerClient() ;
        	TaskResult.dump(client.execute(new ClusterServiceTask(Command.SHUTDOWN, 5 * 1000))) ;
        	StatusBar statusBar = CrawlerApplicationPlugin.getInstance().getCrawlerPane().getStatusBar() ;
        	statusBar.setStatus("Crawler > Service > Shutdown", "Shutdown The Cluster Successfully!") ;
      	} catch (Exception ex) {
        	SwingUtil.showError(CrawlerPane.this, "Shutdown Crawler", ex) ;
        }
      }
		};
		menuBar.registerMenuAction("Crawler > Service", shutdownCrawler) ;
		
		Action startService = new AbstractAction("Stand Alone") {
      public void actionPerformed(ActionEvent e) {
      	try {
	        CrawlerContext.getInstance().startCrawlerServiceWithConfig(false, null) ;
        	StatusBar statusBar = CrawlerApplicationPlugin.getInstance().getCrawlerPane().getStatusBar() ;
          statusBar.setStatus("Crawler > Embeded > Stand Alone", "Start Service (Stand Alone) Successfully!") ;
      	} catch (Exception ex) {
        	SwingUtil.showError(CrawlerPane.this, "Start Service(Stand Alone)", ex) ;
        }
      }
		};
		menuBar.registerMenuAction("Crawler > Embeded", startService) ;
		
		Action startServiceWithTest = new AbstractAction("Stand Alone  With Test") {
      public void actionPerformed(ActionEvent e) {
      	try {
	        CrawlerContext.getInstance().startCrawlerServiceWithConfig(true, null) ;
        	StatusBar statusBar = CrawlerApplicationPlugin.getInstance().getCrawlerPane().getStatusBar() ;
          statusBar.setStatus("Crawler > Embeded > Stand Alone With Test", "Start Service (With Test) successfully!") ;
      	} catch (Exception ex) {
        	SwingUtil.showError(CrawlerPane.this, "Start Service With Test", ex) ;
        }
      }
		};
		menuBar.registerMenuAction("Crawler > Embeded", startServiceWithTest) ;

		Action startServiceWithConfig = new AbstractAction("Stand Alone With Config") {
			public void actionPerformed(ActionEvent e) {
				try {
					JFileChooser fc = new JFileChooser();
					int returnVal = fc.showOpenDialog(CrawlerPane.this);
					if (returnVal == JFileChooser.APPROVE_OPTION) {
						File file = fc.getSelectedFile();
						CrawlerContext.getInstance().startCrawlerServiceWithConfig(true, file.getAbsolutePath()) ;
	        	StatusBar statusBar = CrawlerApplicationPlugin.getInstance().getCrawlerPane().getStatusBar() ;
	          statusBar.setStatus("Crawler > Embeded > Stand Alone With Config", "Start the service (With Config) Successfully!") ;

					}
				} catch (Throwable ex) {
					SwingUtil.showError(CrawlerPane.this, "Load Site Config", ex) ;
				}
			}
		};
		menuBar.registerMenuAction("Crawler > Embeded", startServiceWithConfig) ;
		
		Action startServiceWithConfigFile = new AbstractAction("Start Standalone With D:/crawler-site-config.json") {
			public void actionPerformed(ActionEvent e) {
				try {
					CrawlerContext.getInstance().startCrawlerServiceWithConfig(true, "D:/crawler-site-config.json") ;
					StatusBar statusBar = CrawlerApplicationPlugin.getInstance().getCrawlerPane().getStatusBar() ;
          statusBar.setStatus("Crawler > Embeded > Stand Alone With D:/crawler-site-config.json", "Start the service (With D:/crawler-site-config.json) successfully!") ;
				} catch (Exception ex) {
					SwingUtil.showError(CrawlerPane.this, "Start Service With Config", ex) ;
				}
			}
		};
		menuBar.registerMenuAction("Crawler > Embeded", startServiceWithConfigFile) ;

		Action dummyDocConsumer = new AbstractAction("Dummy Document Consumer") {
      public void actionPerformed(ActionEvent e) {
      	new Thread() {
      		public void run() {
      			try {
      				DocumentConsumer.run() ;
  	        	StatusBar statusBar = CrawlerApplicationPlugin.getInstance().getCrawlerPane().getStatusBar() ;
  	          statusBar.setStatus("Crawler > Integration > Start Integration Test", "Start Integration Test Successfully!") ;
      			} catch(Exception ex) {
      				ex.printStackTrace() ;
      			}
      		}
      	}.start() ;
      }
		};
		menuBar.registerMenuAction("Crawler > Integration", dummyDocConsumer) ;
		
		Action fileDocConsumer = new AbstractAction("File Document Consumer") {
      public void actionPerformed(ActionEvent e) {
      	new Thread() {
      		public void run() {
      			try {
      	      System.setProperty("document.consumer", FileDocumentConsumer.class.getName()) ;
      				DocumentConsumer.run() ;
  	        	StatusBar statusBar = CrawlerApplicationPlugin.getInstance().getCrawlerPane().getStatusBar() ;
  	          statusBar.setStatus("Crawler > Integration > Start Integration Test", "Start Integration Test Successfully!") ;
      			} catch(Exception ex) {
      				ex.printStackTrace() ;
      			}
      		}
      	}.start() ;
      }
		};
		menuBar.registerMenuAction("Crawler > Integration", fileDocConsumer) ;
		
		Action startIntegrationLogger = new AbstractAction("Start Integration Logger") {
      public void actionPerformed(ActionEvent e) {
        new Thread() {
          public void run() {
            try {
              if(DocumentConsumerLogger.getApplicationContext() == null) { //not start yet
                DocumentConsumerLogger.run() ;
              }
              CrawlerPane pane = CrawlerApplicationPlugin.getInstance().getCrawlerPane() ;
              WorkingWorkspace wspace = pane.getWorkingWorkspace() ;
              
              wspace.addTabView("Document Logger", new DocumentConsumerLoggerPanel()) ;
              
              StatusBar statusBar = CrawlerApplicationPlugin.getInstance().getCrawlerPane().getStatusBar() ;
              statusBar.setStatus("Crawler > Integration > Start Integration Logger", "Start Integration Logger Successfully!") ;
            } catch(Exception ex) {
              ex.printStackTrace() ;
            }
          }
        }.start() ;
      }
    };
    menuBar.registerMenuAction("Crawler > Integration", startIntegrationLogger) ;
		
		Action connectCrawlerClient = new AbstractAction("Connect Crawler Client") {
      public void actionPerformed(ActionEvent e) {
      	try {
      		JFrame jframe = SwingUtil.findAncestorOfType(CrawlerPane.this, JFrame.class) ;
      		CrawlerClientConnectionPanel connPanel = new CrawlerClientConnectionPanel(jframe) ;
      		StatusBar statusBar = CrawlerApplicationPlugin.getInstance().getCrawlerPane().getStatusBar() ;
          statusBar.setStatus("Crawler > Connect Crawler Client", "Connect Crawler Client Successfully!") ;
      	} catch (Throwable ex) {
      		SwingUtil.showError(CrawlerPane.this, "Connect Crawler Client", ex) ;
      	}
      }
		};
		menuBar.registerMenuAction("Crawler", connectCrawlerClient) ;
		
		Action loadSiteConfig = new AbstractAction("Load Site Config") {
      public void actionPerformed(ActionEvent e) {
      	try {
      		JFileChooser fc = new JFileChooser();
      		int returnVal = fc.showOpenDialog(CrawlerPane.this);
      		if (returnVal == JFileChooser.APPROVE_OPTION) {
      			File file = fc.getSelectedFile();
      			ModifySiteContextTask task = new ModifySiteContextTask() ;
	        	task.add(file) ;
	        	ClusterClient client = CrawlerContext.getInstance().getCrawlerClient() ;
	        	TaskResult.dump(client.execute(task)) ;
	        	StatusBar statusBar = CrawlerApplicationPlugin.getInstance().getCrawlerPane().getStatusBar() ;
	          statusBar.setStatus("Sites > Load Site Config", "Load Site Config Successfully!") ;
      		}
      	} catch (Throwable ex) {
      		JLabel statusBar = CrawlerApplicationPlugin.getInstance().getCrawlerPane().getStatusBar() ;
      		SwingUtil.showError(CrawlerPane.this, "Load Site Config", ex) ;
      	}
      }
		};
		menuBar.registerMenuAction("Sites", loadSiteConfig) ;
		
		Action loadSiteConfigTest = new AbstractAction("Load Site Config Test 10k Sites") {
      public void actionPerformed(ActionEvent e) {
        try {
            ModifySiteContextTask task = new ModifySiteContextTask() ;
            for(int i = 0; i < 9000; i++)
              task.add("dantri.com.vn_"+i, "http://dantri.com.vn", 3, 2, "ok");
            ClusterClient client = CrawlerContext.getInstance().getCrawlerClient() ;
            TaskResult.dump(client.execute(task)) ;
            StatusBar statusBar = CrawlerApplicationPlugin.getInstance().getCrawlerPane().getStatusBar() ;
            statusBar.setStatus("Sites > Load Site Config Test", "Load Site Config Test Successfully!") ;
        
        } catch (Throwable ex) {
          JLabel statusBar = CrawlerApplicationPlugin.getInstance().getCrawlerPane().getStatusBar() ;
          SwingUtil.showError(CrawlerPane.this, "Load Site Config Test", ex) ;
        }
      }
    };
    menuBar.registerMenuAction("Sites", loadSiteConfigTest) ;
		
		Action clearSiteConfig = new AbstractAction("Clear Site Config") {
			public void actionPerformed(ActionEvent e) {
				try {
					ClearSiteConfigTask addConfigTask = new ClearSiteConfigTask() ;
					ClusterClient client = CrawlerContext.getInstance().getCrawlerClient() ;
					TaskResult.dump(client.execute(addConfigTask)) ;
					StatusBar statusBar = CrawlerApplicationPlugin.getInstance().getCrawlerPane().getStatusBar() ;
          statusBar.setStatus("Sites > Clear Site Config", "Clear Site Config Successfully!") ;
				} catch (Throwable ex) {
					SwingUtil.showError(CrawlerPane.this, "Clear Site Config", ex) ;
				}
			}
		};
		menuBar.registerMenuAction("Sites", clearSiteConfig) ;
		
		
		Action injectURL = new AbstractAction("Inject URL") {
			public void actionPerformed(ActionEvent e) {
				try {
					InjectURLTask injectURLTask = new InjectURLTask() ;
					ClusterClient client = CrawlerContext.getInstance().getCrawlerClient() ;
					TaskResult.dump(client.execute(injectURLTask, "master")) ;
					StatusBar statusBar = CrawlerApplicationPlugin.getInstance().getCrawlerPane().getStatusBar() ;
          statusBar.setStatus("Url DB > Inject URL", "Inject URL Successfully!") ;
				} catch (Throwable ex) {
					SwingUtil.showError(CrawlerPane.this, "Inject URL", ex) ;
				}
			}
		};
		menuBar.registerMenuAction("Url DB", injectURL) ;
		return menuBar ;
	}
	
	public JMenuBarExt getMenuBar() { return this.menuBar ; }
}