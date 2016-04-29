package org.headvances.ml.swingui.classify;

import javax.swing.JFrame;

import org.headvances.swingui.ApplicationFrame;
import org.headvances.swingui.ApplicationPlugin;
import org.headvances.swingui.SwingApplication;
import org.headvances.swingui.component.JMenuBarExt;
/**
 * $Author: Tuan Nguyen$ 
 **/
public class DocumentLabelerPlugin implements ApplicationPlugin {
	static DocumentLabelerPlugin instance ;
	
	private SwingApplication   app ;
	private DocumentLabelerPane bodyPane ;
	
	public void onInit(SwingApplication app) {
		this.app = app ;
		initJMenu(app) ;
		bodyPane = new DocumentLabelerPane() ;
		ApplicationFrame appFrame = new ApplicationFrame("Content Labeler", bodyPane) ;
		appFrame.setClosable(false) ;
		app.addFrame(appFrame) ;
		instance = this ;
	}

	private void initJMenu(SwingApplication app) {
		JMenuBarExt menuBar = app.getJMenuBar() ;
	}

	public DocumentLabelerPane getBodyPane() { return bodyPane ; }
	
	public void onDestroy(SwingApplication app) {
	}

	static public DocumentLabelerPlugin getInstance() { return instance ; }

	public static void main(String[] args) {
		javax.swing.SwingUtilities.invokeLater(new Runnable() {
			public void run() {
				//Make sure we have nice window decorations.
				JFrame.setDefaultLookAndFeelDecorated(true);
				//Create and set up the window.
				SwingApplication application = new SwingApplication();
				application.addPlugin(new DocumentLabelerPlugin()) ;
				application.onInit() ;
				application.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
				//Display the window.
				application.setVisible(true);
			}
		});
	}
}