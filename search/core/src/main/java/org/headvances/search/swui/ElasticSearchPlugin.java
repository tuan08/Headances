package org.headvances.search.swui;

import javax.swing.JFrame;

import org.headvances.swingui.ApplicationFrame;
import org.headvances.swingui.ApplicationPlugin;
import org.headvances.swingui.SwingApplication;
import org.headvances.swingui.component.JMenuBarExt;
/**
 * $Author: Tuan Nguyen$ 
 **/
public class ElasticSearchPlugin implements ApplicationPlugin {
	static ElasticSearchPlugin instance ;
	
	private SwingApplication app ;
	private ElasticSearchPane elasticSearchPane ;
	
	public void onInit(SwingApplication app) {
		this.app = app ;
		initJMenu(app) ;
		elasticSearchPane = new ElasticSearchPane() ;
		ApplicationFrame appFrame = new ApplicationFrame("Search", elasticSearchPane) ;
		appFrame.setClosable(false) ;
		app.addFrame(appFrame) ;
		instance = this ;
	}

	private void initJMenu(SwingApplication app) {
		JMenuBarExt menuBar = app.getJMenuBar() ;
	}

	public ElasticSearchPane getElasticSearchPane() { return elasticSearchPane ; }
	
	public void onDestroy(SwingApplication app) {
	}

	public void startElasticSearchCluster() throws Exception {
		ElasticSearchContext.getInstance().startElasticSearchCluster(1) ;
	}
	
	static public ElasticSearchPlugin getInstance() { return instance ; }

	public static void main(String[] args) {
		javax.swing.SwingUtilities.invokeLater(new Runnable() {
			public void run() {
				//Make sure we have nice window decorations.
				JFrame.setDefaultLookAndFeelDecorated(true);
				//Create and set up the window.
				SwingApplication application = new SwingApplication();
				application.addPlugin(new ElasticSearchPlugin()) ;
				application.onInit() ;
				application.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
				//Display the window.
				application.setVisible(true);
			}
		});
	}
}