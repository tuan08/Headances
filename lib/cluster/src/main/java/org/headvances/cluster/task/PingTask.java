package org.headvances.cluster.task;


import com.hazelcast.core.Cluster;
import com.hazelcast.core.Member;
/**
 * $Author: Tuan Nguyen$ 
 **/
public class PingTask  extends ClusterTask<String>  {
	private String message = null;

	public PingTask() { }

	public PingTask(String m) { this.message = m ; }

	public String call() {
		Cluster cluster = getHazelcastInstance().getCluster() ;
		Member member = cluster.getLocalMember() ;
		return member.toString() + ": " + message;
	}
}