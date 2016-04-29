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

import javax.swing.tree.DefaultMutableTreeNode;

import org.elasticsearch.cluster.node.DiscoveryNode;
import org.headvances.search.swui.ElasticSearchPane;
import org.headvances.search.swui.ElasticSearchPlugin;
import org.headvances.search.swui.TreeNodeListener;
/**
 * $Author: Tuan Nguyen$ 
 * $Revision$
 * $Date$
 * $LastChangedBy$
 * $LastChangedDate$
 * $URL$
 **/
public class InfoNode extends DefaultMutableTreeNode implements TreeNodeListener {
	private DiscoveryNode discoveryNode ;

	public InfoNode(String name, DiscoveryNode member) {
	  super(name);
	  this.discoveryNode = member ;
  }

	public DiscoveryNode getDiscoveryNode() { return this.discoveryNode ; }
	
	public void onSelect() throws Exception {
		ElasticSearchPane pane = ElasticSearchPlugin.getInstance().getElasticSearchPane() ;
		org.headvances.search.swui.WorkingWorkspace wspace = pane.getWorkingWorkspace() ;
	}
}