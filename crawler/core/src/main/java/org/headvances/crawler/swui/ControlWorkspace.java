package org.headvances.crawler.swui;

import org.headvances.cluster.ClusterClient;
import org.headvances.crawler.swui.fetcher.FetcherControlPanel;
import org.headvances.crawler.swui.master.MasterControlPanel;
import org.headvances.swingui.component.JAccordionPanel;

/**
 * $Author: Tuan Nguyen$ 
 **/
public class ControlWorkspace extends JAccordionPanel implements CrawlerClientListener {
	private MasterControlPanel masterControlPanel ;
	private FetcherControlPanel fetcherControlPanel ;
	
	public ControlWorkspace() {
		masterControlPanel = new MasterControlPanel(); 
		fetcherControlPanel = new FetcherControlPanel() ;
		addBar("Master", masterControlPanel) ;
		addBar("Fetcher", fetcherControlPanel) ;
	}

  public void onConnect(ClusterClient client) {
  	masterControlPanel.onConnect(client) ;
  	fetcherControlPanel.onConnect(client) ;
  	updateUI() ;
  }
}
