package org.headvances.ml.swingui.nlp.config;

import java.util.List;

public class ActionSet {
	private String name  ;
	private String label ;
	private List<ActionConfig> actions ;
	
	public String getName() { return name; }
	public void setName(String name) { this.name = name; }
	
	public String getLabel() { return label; }
	public void setLabel(String label) { this.label = label; }
	
	public List<ActionConfig> getActions() { return actions; }
	public void setActions(List<ActionConfig> actions) { this.actions = actions; }
}
