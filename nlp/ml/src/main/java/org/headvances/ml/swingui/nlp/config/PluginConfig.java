package org.headvances.ml.swingui.nlp.config;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

public class PluginConfig {
	private String name ;
	private String type ;
	private String description ;
	private Map<String, ActionSet> actionSet ;
	
	public String getName() { return name; }
	public void setName(String name) { this.name = name; }
	
	public String getType() { return type ; }
	public void   setType(String type) { this.type = type; }
	
	public String getDescription() { return description; }
	public void setDescription(String description) { this.description = description; }
	
	public Map<String, ActionSet> getActionSet() { return actionSet; }
	public void setActionSet(Map<String, ActionSet> actionSet) {
  	this.actionSet = actionSet;
  }
	
	public List<ActionSet> getActionSetWithPrefix(String prefix) { 
		List<ActionSet> holder = new ArrayList<ActionSet>() ;
		Iterator<Map.Entry<String, ActionSet>> i = actionSet.entrySet().iterator() ;
		while(i.hasNext()) {
			Map.Entry<String, ActionSet> entry = i.next() ;
			if(entry.getKey().startsWith(prefix)) {
				holder.add(entry.getValue()) ;
			}
		}
		return holder ; 
	}
}
