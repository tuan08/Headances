package org.headvances.cluster;

import java.io.Serializable;
import java.util.Set;

public class ClusterNodeInfo implements Serializable {
	private String description ;
	private Set<String> role ;
	
	public String getDescription() { return description; }
	public void setDescription(String description) { this.description = description; }
	
	public Set<String> getRole() { return role; }
	public void setRole(Set<String> role) { this.role = role; }
}
