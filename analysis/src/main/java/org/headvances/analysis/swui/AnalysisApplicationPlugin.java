package org.headvances.analysis.swui;

import javax.swing.JFrame;

import org.headvances.analysis.AnalysisServer;
import org.headvances.jms.swui.JMSApplicationPlugin;
import org.headvances.swingui.ApplicationFrame;
import org.headvances.swingui.ApplicationPlugin;
import org.headvances.swingui.SwingApplication;
import org.headvances.swingui.component.JMenuBarExt;
/**
 * $Author: Tuan Nguyen$ 
 **/
public class AnalysisApplicationPlugin implements ApplicationPlugin {
	static AnalysisApplicationPlugin instance ;
	
	private SwingApplication app ;
	private AnalysisPane     analysisPane ;
	
	public void onInit(SwingApplication app) {
		this.app = app ;
		initJMenu(app) ;
		analysisPane = new AnalysisPane() ;
		ApplicationFrame appFrame = new ApplicationFrame("Analysis", analysisPane) ;
		appFrame.setClosable(false) ;
		app.addFrame(appFrame) ;
		instance = this ;
	}

	private void initJMenu(SwingApplication app) {
		JMenuBarExt menuBar = app.getJMenuBar() ;
	}

	public AnalysisPane getAnalysisPane() { return analysisPane ; }
	
	public void onDestroy(SwingApplication app) {
	}

	public void startAnalysisServer() throws Exception {
		System.setProperty("crawler.input.auto-startup", "true") ;
		System.setProperty("analysis.output.index", "true") ;
		AnalysisServer.run() ;
		AnalysisContext.getInstance().connectClient("crawler", "crawler", "localhost:5700");
	}
	
	static public AnalysisApplicationPlugin getInstance() { return instance ; }

	public static void main(String[] args) {
		javax.swing.SwingUtilities.invokeLater(new Runnable() {
			public void run() {
				//Make sure we have nice window decorations.
				JFrame.setDefaultLookAndFeelDecorated(true);
				//Create and set up the window.
				SwingApplication application = new SwingApplication();
				application.addPlugin(new JMSApplicationPlugin()) ;
				application.addPlugin(new AnalysisApplicationPlugin()) ;
				application.onInit() ;
				application.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
				//Display the window.
				application.setVisible(true);
			}
		});
	}
}