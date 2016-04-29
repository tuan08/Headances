package org.headvances.cluster;

import java.io.Serializable;
import java.util.Set;

import com.hazelcast.core.Member;

/**
 * $Author: Tuan Nguyen$ 
 **/
public class MemberInfo implements Serializable {
	private Member member ;
	private ClusterNodeInfo info ;
	
	public MemberInfo() {} 
	
	public MemberInfo(Member member, ClusterNodeInfo info) {
		this.member = member ;
		this.info = info ;
	}

	public Member getMember() { return member; }
	public void setMember(Member member) { this.member = member; }

	public ClusterNodeInfo getInfo() { return info; }
	public void setInfo(ClusterNodeInfo info) { this.info = info; }
	
	public Set<String> getRole() { 
		if(info == null) return null ;
		return info.getRole() ; 
	}
	
}