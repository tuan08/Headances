package org.headvances.search.swui.node;

import javax.swing.JComponent;

import org.elasticsearch.action.admin.cluster.node.info.NodeInfo;
import org.elasticsearch.monitor.os.OsInfo;
import org.headvances.search.ESClient;
import org.headvances.search.swui.ElasticSearchContext;
import org.headvances.swingui.component.TableView;
import org.headvances.swingui.component.UpdatableJPanel;
/**
 * $Author: Tuan Nguyen$ 
 **/
public class OSInfoPanel extends UpdatableJPanel {
	private InfoNode    infoNode ;
	
	private TableView table ;
	
	public OSInfoPanel(InfoNode infoNode) throws Exception {
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
		OsInfo osInfo   = ninfo.getOs() ;
		System.out.println("Node INFO: " + ninfo.getNode().getId());
		System.out.println("OS INFO: " + osInfo);
		String[]   header = { "Name", "Value" } ;
		
		if(osInfo.getCpu() != null) {
			OsInfo.Cpu cpu = osInfo.getCpu() ;
			OsInfo.Mem mem = osInfo.getMem() ;
			OsInfo.Swap swap = osInfo.getSwap() ;
			Object[][] data = new Object[][] {
					{"Node Id",  ninfo.getNode().getId()} ,
					{"CPU Vendor", cpu.getVendor()} ,
					{"CPU Model", cpu.getModel()},
					{"CPU Mhz",   cpu.getMhz()} ,
					{"CPU Core",   cpu.getTotalCores()},
					{"CPU Total Socket",   cpu.getTotalSockets()},
					{"CPU Core Per Socket",   cpu.getCoresPerSocket()},
					{"CPU Cache Size",   cpu.getCacheSize()},
					
					{"Mem",   mem.getTotal()},
					
					{"Swap",  swap.getTotal()},
			}; 
			table.setData(header, data) ;
		} else {
			Object[][] data = new Object[][] {
					{"Node Id",  ninfo.getNode().getId()} ,
			}; 
			table.setData(header, data) ;
		}
	}
}