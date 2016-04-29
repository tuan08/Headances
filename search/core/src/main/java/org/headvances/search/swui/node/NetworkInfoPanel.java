package org.headvances.search.swui.node;

import javax.swing.JComponent;

import org.elasticsearch.action.admin.cluster.node.info.NodeInfo;
import org.elasticsearch.monitor.network.NetworkInfo;
import org.headvances.search.ESClient;
import org.headvances.search.swui.ElasticSearchContext;
import org.headvances.swingui.component.TableView;
import org.headvances.swingui.component.UpdatableJPanel;
/**
 * $Author: Tuan Nguyen$ 
 * $Revision$
 * $Date$
 * $LastChangedBy$
 * $LastChangedDate$
 * $URL$
 **/
public class NetworkInfoPanel extends UpdatableJPanel {
	private InfoNode    infoNode ;
	
	private TableView table ;
	
	public NetworkInfoPanel(InfoNode infoNode) throws Exception {
		this.infoNode = infoNode ;
		update() ;
	}
	
	protected JComponent createBodyPane() {
		table = new TableView() ;
		return table ;
	}
	
	protected void update() throws Exception {
		ESClient client = ElasticSearchContext.getInstance().getESClient() ;
		NodeInfo ninfo  = client.getNodeInfo(infoNode.getDiscoveryNode().getId()) ;
		NetworkInfo networkInfo = ninfo.getNetwork() ;
		NetworkInfo.Interface nif = networkInfo.getPrimaryInterface() ;
		String[]   header = { "Name", "Value" } ;
		
		Object[][] data = new Object[][] {
			{"Node Id",  ninfo.getNode().getAddress()} ,
			{"Name",  nif.getName() } ,
			{"Address",  nif.getAddress()} ,
			{"MAC Address",  nif.getMacAddress()} ,
		}; 
		table.setData(header, data) ;
	}
}