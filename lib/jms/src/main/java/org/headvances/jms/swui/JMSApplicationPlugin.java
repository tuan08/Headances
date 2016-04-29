package org.headvances.jms.swui;

import java.awt.event.ActionEvent;

import javax.swing.AbstractAction;
import javax.swing.Action;
import javax.swing.JFrame;

import org.headvances.jms.EmbededActiveMQServer;
import org.headvances.swingui.ApplicationPlugin;
import org.headvances.swingui.SwingApplication;
import org.headvances.swingui.component.JMenuBarExt;
/**
 * $Author: Tuan Nguyen$ 
 **/
public class JMSApplicationPlugin implements ApplicationPlugin {
	static JMSApplicationPlugin instance ;
	
	private SwingApplication app ;
	
	public void onInit(SwingApplication app) {
		this.app = app ;
		initJMenu(app) ;
		instance = this ;
	}

	private void initJMenu(SwingApplication app) {
		JMenuBarExt menuBar = app.getJMenuBar() ;
		Action startCrawler = new AbstractAction("Start ActiveMQ Server") {
			public void actionPerformed(ActionEvent e) {
				new Thread() {
					public void run() {
						try {
							EmbededActiveMQServer.run() ;
						} catch (Exception e) {
							e.printStackTrace();
						}
					}
				}.start() ;
			}
		};
		menuBar.registerMenuAction("ActiveMQ > Embeded", startCrawler) ;
	}

	public void startActiveMQServer(boolean wait) throws Exception {
		EmbededActiveMQServer.run() ;
	}
	
	public void onDestroy(SwingApplication app) {
	}

	static public JMSApplicationPlugin getInstance() { return instance ; }

	public static void main(String[] args) {
		javax.swing.SwingUtilities.invokeLater(new Runnable() {
			public void run() {
				//Make sure we have nice window decorations.
				JFrame.setDefaultLookAndFeelDecorated(true);
				//Create and set up the window.
				SwingApplication application = new SwingApplication();
				application.addPlugin(new JMSApplicationPlugin()) ;
				application.onInit() ;
				application.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
				//Display the window.
				application.setVisible(true);
			}
		});
	}
}