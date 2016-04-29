package org.headvances.cluster;

import java.util.HashSet;
import java.util.Set;

import org.codehaus.jackson.annotate.JsonIgnore;

import com.hazelcast.core.Member;

public class ClusterMember {
	private Member member ;
	private String host ;
	private int    port ;
	private Set<String> roles ;
	
	public ClusterMember() {	}

	public ClusterMember(Member member) {
		this.member = member ;
		this.host   = member.getInetSocketAddress().getHostName() ;
		this.port   = member.getInetSocketAddress().getPort() ;
	}
	
	@JsonIgnore
	public Member getMember() { return this.member ; }
	
	public String getHost() { return host; }
	public void   setHost(String host) { this.host = host; }
	
	public int  getPort() { return port; }
	public void setPort(int port) { this.port = port; }
	
	public void addRole(String role) {
		if(roles == null) roles = new HashSet<String>() ;
		roles.add(role) ;
	}

	public Set<String> getRoles() { return roles; }
	public void        setRoles(Set<String> roles) { this.roles = roles; }
	
	public String toString() { return host + ":" + port ; }
}
