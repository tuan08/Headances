package org.headvances.search.swui;

import javax.swing.JFrame;

import org.headvances.http.swui.HttpApplicationPlugin;
import org.headvances.swingui.SwingApplication;
/**
 * $Author: Tuan Nguyen$ 
 **/
public class Main  {
	public static void main(String[] args) {
		javax.swing.SwingUtilities.invokeLater(new Runnable() {
			public void run() {
				//Make sure we have nice window decorations.
				JFrame.setDefaultLookAndFeelDecorated(true);
				//Create and set up the window.
				SwingApplication application = new SwingApplication();
				application.addPlugin(new ElasticSearchPlugin()) ;
				application.addPlugin(new HttpApplicationPlugin("../webapps")) ;
				application.onInit() ;
				application.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
				//Display the window.
				application.setVisible(true);
			}
		});
	}
}