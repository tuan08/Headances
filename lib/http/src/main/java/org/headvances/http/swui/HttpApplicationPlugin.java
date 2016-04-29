package org.headvances.http.swui;

import java.awt.event.ActionEvent;
import java.io.File;
import java.io.IOException;

import javax.swing.AbstractAction;
import javax.swing.Action;
import javax.swing.JFileChooser;
import javax.swing.JFrame;

import org.headvances.http.JettyWebServer;
import org.headvances.swingui.ApplicationPlugin;
import org.headvances.swingui.SwingApplication;
import org.headvances.swingui.SwingUtil;
import org.headvances.swingui.component.JMenuBarExt;
import org.headvances.util.text.StringUtil;
/**
 * $Author: Tuan Nguyen$ 
 **/
public class HttpApplicationPlugin implements ApplicationPlugin {
	static HttpApplicationPlugin instance ;
	
	private SwingApplication app ;
	private JettyWebServer webServer ;
	private String[] webappDirs ;
	
	public HttpApplicationPlugin() {
		this.webappDirs = StringUtil.EMPTY_ARRAY ;
	}
	
	public HttpApplicationPlugin(String ... webappDirs) {
		this.webappDirs = webappDirs ;
	}
	
	public void onInit(SwingApplication app) {
		this.app = app ;
		initJMenu(app) ;
		instance = this ;
	}

	private void initJMenu(SwingApplication app) {
		JMenuBarExt menuBar = app.getJMenuBar() ;
		menuBar.registerMenuAction("Http > Start", new StartServerAction(null)) ;
		for(String sel : webappDirs) {
			menuBar.registerMenuAction("Http > Start", new StartServerAction(sel)) ;
		}
		
		
		Action stopServer = new AbstractAction("Stop") {
			public void actionPerformed(ActionEvent e) {
				try {
					if(webServer == null) return ;
					webServer.stop() ;
					webServer = null ;
        } catch (Exception e1) {
	        e1.printStackTrace();
        }
			}
		};
		menuBar.registerMenuAction("Http", stopServer) ;
	}

	public void onDestroy(SwingApplication app) { }

	class StartServerAction extends AbstractAction {
		private String webappDir ;
		
		public StartServerAction(String webappDir) { 
			super("Start With " + (webappDir != null ? webappDir : "")) ; 
			this.webappDir = webappDir ;
		}		
		public void actionPerformed(ActionEvent e) {
			if(webServer != null) {
				return ;
			}
			if(webappDir == null) {
				JFileChooser fc = new JFileChooser();
				fc.setCurrentDirectory(new File("."));
				fc.setFileSelectionMode(JFileChooser.FILES_AND_DIRECTORIES);
				int returnVal = fc.showOpenDialog(app);
				if (returnVal == JFileChooser.APPROVE_OPTION) {
					File file = fc.getSelectedFile();
					try {
	          webappDir = file.getCanonicalPath() ;
          } catch (IOException ioe) {
          	SwingUtil.showError(app, "Start Http", ioe) ;
          	return ;
          }
				} else { 
					return ;
				}
			}
  		
			new Thread() {
				public void run() {
					try {
						webServer = new JettyWebServer(webappDir, 8080) ;
						webServer.start() ;
					} catch (Exception e) {
						SwingUtil.showError(app, "Start Http", e) ;
					}
				}
			}.start() ;
		}
	}
	
	static public HttpApplicationPlugin getInstance() { return instance ; }

	public static void main(String[] args) {
		javax.swing.SwingUtilities.invokeLater(new Runnable() {
			public void run() {
				//Make sure we have nice window decorations.
				JFrame.setDefaultLookAndFeelDecorated(true);
				//Create and set up the window.
				SwingApplication application = new SwingApplication();
				application.addPlugin(new HttpApplicationPlugin("src/test/resources/webapp")) ;
				application.onInit() ;
				application.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
				//Display the window.
				application.setVisible(true);
			}
		});
	}
}