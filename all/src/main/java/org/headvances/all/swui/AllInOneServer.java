package org.headvances.all.swui;

import static org.elasticsearch.node.NodeBuilder.nodeBuilder;

import org.elasticsearch.node.NodeBuilder;
import org.headvances.analysis.AnalysisServer;
import org.headvances.crawler.CrawlerFetcher;
import org.headvances.crawler.master.CrawlerMaster;
import org.headvances.http.JettyWebServer;
import org.headvances.jms.EmbededActiveMQServer;


public class AllInOneServer {
	static public void startElasticSearchCluster(final int numberOfNode) throws Exception {
		Thread thread = new Thread() {
			public void run() {
				try {
					NodeBuilder nb = nodeBuilder() ;
					nb.getSettings().put("cluster.name", "headvances") ;
					nb.getSettings().put("path.data", "target/data") ;
					for(int i = 0; i < numberOfNode; i++) {
						nb.node() ;
					}
					Thread.sleep(1000) ;
				} catch (Exception e) {
	        e.printStackTrace();
        }
			}
		} ;
		thread.start() ;
	}
	
	
	public static void main(String[] args) throws Exception {
		String appHome = System.getProperty("app.home.dir") ;
		EmbededActiveMQServer.run() ;
		
		System.setProperty("crawler.input.auto-startup", "true") ;
		System.setProperty("analysis.output.index", "true") ;
		AnalysisServer.run() ;
		
		startElasticSearchCluster(1) ;
		
		CrawlerMaster.run();
    CrawlerFetcher.run();
    
    if(appHome != null) {
    	JettyWebServer.run(new String[] {"-webapp", appHome + "/webapps"} ) ;
    }
    Thread.currentThread().join() ;
	}
}
