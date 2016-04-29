package org.headvances.all.swui;

import java.awt.event.ActionEvent;

import javax.swing.AbstractAction;
import javax.swing.Action;
import javax.swing.JFrame;

import org.headvances.analysis.swui.AnalysisApplicationPlugin;
import org.headvances.crawler.swui.CrawlerApplicationPlugin;
import org.headvances.http.swui.HttpApplicationPlugin;
import org.headvances.jms.swui.JMSApplicationPlugin;
import org.headvances.ml.swingui.nlp.SwingAppNLPPlugin;
import org.headvances.search.swui.ElasticSearchPlugin;
import org.headvances.swingui.SwingApplication;
import org.headvances.swingui.component.JMenuBarExt;
import org.headvances.util.FileUtil;
/**
 * $Author: Tuan Nguyen$ 
 **/
public class AllInOne  {
	static class StartAll extends Thread {
		SwingApplication app ;
		private boolean crawl ;

		public StartAll(SwingApplication app, boolean crawl) { 
			this.app = app ; 
			this.crawl = crawl ;
		}
		
		public void run() {
			try {
	      app.getPlugin(JMSApplicationPlugin.class).startActiveMQServer(true) ;
	      app.getPlugin(AnalysisApplicationPlugin.class).startAnalysisServer() ;
	      app.getPlugin(ElasticSearchPlugin.class).startElasticSearchCluster() ;
	      app.getPlugin(CrawlerApplicationPlugin.class).startCrawler(crawl) ;
      } catch (Exception e) {
	      e.printStackTrace();
      }
		}
	}
	
	public static void main(String[] args) throws Exception {
		FileUtil.removeIfExist("target/analysis") ;
		FileUtil.removeIfExist("target/data") ;
		FileUtil.removeIfExist("target/activemq") ;
		FileUtil.removeIfExist("target/crawler") ;
		FileUtil.removeIfExist("../data/crawler") ;
		
		final SwingApplication app = new SwingApplication();
		javax.swing.SwingUtilities.invokeLater(new Runnable() {
			public void run() {
				//Make sure we have nice window decorations.
				JFrame.setDefaultLookAndFeelDecorated(true);
				//Create and set up the window.
				app.addPlugin(new JMSApplicationPlugin()) ;
				app.addPlugin(new AnalysisApplicationPlugin()) ;
				app.addPlugin(new ElasticSearchPlugin()) ;
				app.addPlugin(new CrawlerApplicationPlugin()) ;
				
				app.addPlugin(new HttpApplicationPlugin("../webapps")) ;
				app.addPlugin(new SwingAppNLPPlugin()) ;
				app.onInit() ;
				app.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
				
				JMenuBarExt menuBar = app.getJMenuBar() ;
				Action startAll = new AbstractAction("All With Test Config") {
					public void actionPerformed(ActionEvent e) {
						new StartAll(app, false).start() ;
					}
				};
				menuBar.registerMenuAction("All > Start", startAll) ;
				startAll = new AbstractAction("All With Test Config And Crawl") {
					public void actionPerformed(ActionEvent e) {
						new StartAll(app, true).start() ;
					}
				};
				menuBar.registerMenuAction("All > Start", startAll) ;
				//Display the window.
				app.setVisible(true);
			}
		});
	}
}