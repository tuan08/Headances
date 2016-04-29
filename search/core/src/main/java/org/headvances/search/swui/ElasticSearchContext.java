package org.headvances.search.swui;

import static org.elasticsearch.node.NodeBuilder.nodeBuilder;

import java.util.List;

import org.elasticsearch.node.NodeBuilder;
import org.headvances.search.ESClient;
import org.headvances.swingui.SwingUtil;
/**
 * $Author: Tuan Nguyen$ 
 **/
public class ElasticSearchContext {
	private static ElasticSearchContext instance ;
	
	private ESClient esclient ;

	public ESClient getESClient() { return this.esclient ; }
	
	public void connect(String[] address) throws Exception {
		esclient = new ESClient(address) ;
		broadcast(ESClientListener.CONNECT) ;
	}

	public void broadcast(int event) throws Exception {
		ElasticSearchPlugin instance = ElasticSearchPlugin.getInstance() ;
		List<ESClientListener> listeners = 
			SwingUtil.findDescentdantsOfType(instance.getElasticSearchPane(), ESClientListener.class) ;
		for(ESClientListener sel : listeners) sel.onEvent(esclient, event) ;
	}
	
	public void startElasticSearchCluster(final int numberOfNode) throws Exception {
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
					connect(new String[] { "localhost:9300" }) ;
				} catch (Exception e) {
	        e.printStackTrace();
        }
			}
		} ;
		thread.start() ;
	}
	
	static public ElasticSearchContext getInstance() { 
		if(instance == null) instance = new ElasticSearchContext() ;
		return instance ; 
	}
}
