package org.headvances.analysis.swui;

import org.headvances.cluster.ClusterClient;

/**
 * $Author: Tuan Nguyen$ 
 **/
public interface ClientListener {
	public void onConnect(ClusterClient client) ;
}