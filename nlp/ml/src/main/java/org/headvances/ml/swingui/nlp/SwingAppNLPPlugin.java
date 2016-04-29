package org.headvances.ml.swingui.nlp;

import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;

import javax.swing.JFrame;

import org.headvances.swingui.ApplicationFrame;
import org.headvances.swingui.ApplicationPlugin;
import org.headvances.swingui.SwingApplication;
import org.headvances.swingui.component.JMenuBarExt;

public class SwingAppNLPPlugin  implements ApplicationPlugin {
	static SwingAppNLPPlugin instance ;
	private SwingApplication   app ;
	private NLPPane bodyPane ;
	
	public void onInit(SwingApplication app) {	
		this.app = app ;
		initJMenu(app) ;
		bodyPane = new NLPPane() ;
		bodyPane.onInit() ;
		ApplicationFrame appFrame = new ApplicationFrame("NLP", bodyPane) ;
		appFrame.setClosable(false) ;
		app.addFrame(appFrame) ;
		instance = this ;
	}

  public void onDestroy(SwingApplication app) { 
  	bodyPane.onDestroy();
  }
  
  private void initJMenu(SwingApplication app) {
		JMenuBarExt menuBar = app.getJMenuBar() ;
	}
  
	public static void main(String[] args) {
		javax.swing.SwingUtilities.invokeLater(new Runnable() {
			public void run() {
				//Make sure we have nice window decorations.
				JFrame.setDefaultLookAndFeelDecorated(true);
				//Create and set up the window.
				final SwingApplication application = new SwingApplication();
				application.addWindowListener(new WindowAdapter() {
          public void windowClosing(WindowEvent e) {
          	application.onDestroy();
          }
				});
				application.addPlugin(new SwingAppNLPPlugin()) ;
				application.onInit() ;
				application.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
				//Display the window.
				application.setVisible(true);
			}
		});
	}
}