package org.headvances.analysis.swui;

import java.awt.BorderLayout;
import java.awt.Dimension;
import java.awt.event.ActionEvent;

import javax.swing.AbstractAction;
import javax.swing.Action;
import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.JSplitPane;

import org.headvances.analysis.AnalysisServer;
import org.headvances.swingui.SwingUtil;
import org.headvances.swingui.component.JMenuBarExt;
/**
 * $Author: Tuan Nguyen$ 
 **/
public class AnalysisPane extends JPanel {
	private JMenuBarExt menuBar ;
	private ControlWorkspace cspace ;
	private WorkingWorkspace wspace ;
	
	public AnalysisPane() {
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
		Action startServer = new AbstractAction("Start") {
      public void actionPerformed(ActionEvent e) {
      	new Thread() {
      		public void run() {
      			try {
      				AnalysisServer.run() ;
      				AnalysisContext.getInstance().connectClient("crawler", "crawler", "localhost:5700");
      			} catch (Exception ex) {
      				SwingUtil.showError(AnalysisPane.this, "Start Server", ex) ;
      			}
      		}
      	}.start() ;
      }
		};
		menuBar.registerMenuAction("Server > Embeded", startServer) ;
		
		Action startServerWithCrawlerInput = new AbstractAction("Start With Crawler Input") {
      public void actionPerformed(ActionEvent e) {
      	new Thread() {
      		public void run() {
      			try {
      				System.setProperty("crawler.input.auto-startup", "true") ;
      				System.setProperty("analysis.output.index", "true") ;
      				AnalysisServer.run() ;
      				AnalysisContext.getInstance().connectClient("crawler", "crawler", "localhost:5700");
      			} catch (Exception ex) {
      				SwingUtil.showError(AnalysisPane.this, "Start Server", ex) ;
      			}
      		}
      	}.start() ;
      }
		};
		menuBar.registerMenuAction("Server > Embeded", startServerWithCrawlerInput) ;
		
		Action connectClient = new AbstractAction("Connect Client") {
      public void actionPerformed(ActionEvent e) {
      	ClientConnectionPanel connPanel = null ;
      	try {
      		JFrame jframe = SwingUtil.findAncestorOfType(AnalysisPane.this, JFrame.class) ;
      		connPanel = new ClientConnectionPanel(jframe) ;
      	} catch (Throwable ex) {
      		if(connPanel != null) {
      			
      		}
      		SwingUtil.showError(AnalysisPane.this, "Connect Client", ex) ;
      	}
      }
		};
		menuBar.registerMenuAction("Server", connectClient) ;
		return menuBar ;
	}
	
	public JMenuBarExt getMenuBar() { return this.menuBar ; }
}