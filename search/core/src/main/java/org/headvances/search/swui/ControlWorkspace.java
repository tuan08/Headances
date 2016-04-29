package org.headvances.search.swui;

import org.headvances.search.ESClient;
import org.headvances.search.swui.index.IndexInfoPanel;
import org.headvances.search.swui.node.NodeInfoPanel;
import org.headvances.swingui.component.JAccordionPanel;
/**
 * $Author: Tuan Nguyen$ 
 **/
public class ControlWorkspace extends JAccordionPanel  implements ESClientListener {
	private NodeInfoPanel nodeInfoPanel ;
	private IndexInfoPanel indexInfoPanel ;
	
	public ControlWorkspace() {
		nodeInfoPanel = new NodeInfoPanel() ;
		indexInfoPanel = new IndexInfoPanel() ;
		addBar("Nodes", nodeInfoPanel) ;
		addBar("Indices", indexInfoPanel) ;
	}
	
	public void onEvent(ESClient client, int event) {
  	nodeInfoPanel.onEvent(client, event) ;
  	indexInfoPanel.onEvent(client, event) ;
		updateUI() ;
  }
}
