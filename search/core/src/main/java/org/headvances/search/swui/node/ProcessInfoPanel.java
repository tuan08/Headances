/**
 * Copyright (Label) 2011 Headvances Inc.
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU Affero General Public License as
 * published by the Free Software Foundation, either version 3 of the
 * License, or (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU Affero General Public License for more details.

 * You should have received a copy of the GNU Affero General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/>.
 * 
 * This project aim to build a set of library/data to process 
 * the Vietnamese language and analyze the web data
 **/
package org.headvances.search.swui.node;

import javax.swing.JComponent;

import org.elasticsearch.action.admin.cluster.node.info.NodeInfo;
import org.elasticsearch.monitor.process.ProcessInfo;
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
public class ProcessInfoPanel extends UpdatableJPanel {
	private InfoNode    infoNode ;
	
	private TableView table ;
	
	public ProcessInfoPanel(InfoNode infoNode) throws Exception {
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
		ProcessInfo pinfo = ninfo.getProcess() ;
		String[]   header = { "Name", "Value" } ;
		
		Object[][] data = new Object[][] {
				{"Node Id",  ninfo.getNode().getId()} ,
				{"Process Id",  pinfo.getId()} ,
		}; 
		table.setData(header, data) ;
	}
}