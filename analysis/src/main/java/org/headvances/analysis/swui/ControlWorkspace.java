package org.headvances.analysis.swui;

import org.headvances.swingui.component.JAccordionPanel;

/**
 * $Author: Tuan Nguyen$ 
 **/
public class ControlWorkspace extends JAccordionPanel  {
	public ControlWorkspace() {
		addBar("Analysis", new RoleControlPanel("analysis")) ;
	}
}
