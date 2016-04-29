package org.headvances.analysis;

import junit.framework.Assert;

import org.headvances.cluster.ClusterClient;
import org.headvances.jms.EmbededActiveMQServer;
import org.junit.Test;

/**
 * $Author: Tuan Nguyen$ 
 **/
public class AnalysisServerUnitTest {
	@Test
	public void testAnalysisServer() throws Exception {
		EmbededActiveMQServer.run() ;
		
		AnalysisServer.run() ;
		
		ClusterClient client = new ClusterClient("crawler", "crawler", "127.0.0.1:5700") ;
		Assert.assertEquals(1, client.getMembers("analysis").size()) ;
	}
}