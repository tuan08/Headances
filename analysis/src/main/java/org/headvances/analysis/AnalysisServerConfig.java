package org.headvances.analysis;

import org.headvances.util.text.StringUtil;

public class AnalysisServerConfig {
	private String[] role ;
	
	public String[] getRole() { return this.role ; }
	public void setRole(String[] role) { this.role = role ;}
	
	public void setRoles(String roles) {
		this.role = StringUtil.toStringArray(roles) ;
	}
}