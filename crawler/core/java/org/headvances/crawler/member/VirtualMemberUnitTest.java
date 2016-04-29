package org.headvances.crawler.member;

import java.util.Collection;
import java.util.Iterator;

import org.headvances.cluster.ClusterClient;
import org.headvances.cluster.task.PingTask;
import org.junit.Test;

public class VirtualMemberUnitTest {
	@Test
	public void testVirtualMember() throws Exception {
		VirtualMember[] members = new VirtualMember[2] ;
		for(int i = 0 ; i < members.length ; i++) {
			members[i] = new VirtualMember("classpath:META-INF/crawler-cluster.xml") ;
			members[i].start() ;
		}
		members[0].echo("Hello!!!") ;
		
		Collection<String> results = members[0].execute(new PingTask("hello ping")) ;
		Iterator<String> i = results.iterator() ;
    while(i.hasNext()) {
    	System.out.println("MultiTask Result: " + i.next()) ;
    }
    
    ClusterClient client = new ClusterClient("crawler", "crawler", "127.0.0.1:5700") ;
    results = client.execute(new PingTask("hello ping")) ;
		i = results.iterator() ;
    while(i.hasNext()) {
    	System.out.println("Client MultiTask Result: " + i.next()) ;
    }
    Thread.sleep(3000) ;
	}
}