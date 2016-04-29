package org.headvances.cluster.task;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Iterator;
import java.util.List;

import com.hazelcast.core.Cluster;
import com.hazelcast.core.HazelcastInstanceAwareObject;
import com.hazelcast.core.Member;
/**
 * $Author: Tuan Nguyen$ 
 **/
public class TaskResult implements Serializable {
	private String member  ;
	private List<String> message = new ArrayList<String>();

	public TaskResult() {}
	
	public TaskResult(HazelcastInstanceAwareObject task) {
		Cluster cluster = task.getHazelcastInstance().getCluster() ;
		Member member = cluster.getLocalMember() ;
		this.member = member.toString() ;
	}
	
	public String getMember() { return member; }
	public void   setMember(String member) { this.member = member; }

	public List<String> getMessage() { return message; }
	public void   setMessage(List<String> message) { this.message = message; }
	public void   addMessage(String m) { message.add(m); }
	
	public String getMessages(String separator) {
		StringBuilder b = new StringBuilder() ;
		for(int i = 0; i < message.size(); i++) {
			if(i > 0) b.append(separator) ;
			b.append(message.get(i));
		}
		return b.toString() ;
	}
	
	static public void dump(Collection<TaskResult> results) {
		Iterator<TaskResult> i = results.iterator() ;
  	while(i.hasNext()) {
  		TaskResult result = i.next() ;
  		System.out.println(result.getMember() + ": " + result.getMessages(", "));
  	}
	}
}