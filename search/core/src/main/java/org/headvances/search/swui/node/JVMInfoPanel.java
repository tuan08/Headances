package org.headvances.search.swui.node;

import java.util.Date;

import javax.swing.JComponent;

import org.elasticsearch.action.admin.cluster.node.info.NodeInfo;
import org.elasticsearch.monitor.jvm.JvmInfo;
import org.headvances.search.ESClient;
import org.headvances.search.swui.ElasticSearchContext;
import org.headvances.swingui.component.TableView;
import org.headvances.swingui.component.UpdatableJPanel;
/**
 * $Author: Tuan Nguyen$ 
 **/
public class JVMInfoPanel extends UpdatableJPanel {
	private InfoNode    infoNode ;
	
	private TableView table ;
	
	public JVMInfoPanel(InfoNode infoNode) throws Exception {
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
		JvmInfo jvmInfo = ninfo.getJvm() ;
		String[]   header = { "Name", "Value" } ;
		
		Object[][] data = new Object[][] {
			{"Node Id",  ninfo.getNode().getAddress()} ,
			{"Start Time",  new Date(jvmInfo.getStartTime())} ,
			{"Pid",  jvmInfo.getPid()} ,
			{"Version",  jvmInfo.getVersion()} ,
			{"Heap Init", jvmInfo.getMem().getHeapInit()} ,
			{"Heap Max", jvmInfo.getMem().getHeapMax()} ,
			{"Non Heap Init", jvmInfo.getMem().getNonHeapInit()} ,
			{"Non Heap Max", jvmInfo.getMem().getNonHeapMax()}
		}; 
		table.setData(header, data) ;
	}
}