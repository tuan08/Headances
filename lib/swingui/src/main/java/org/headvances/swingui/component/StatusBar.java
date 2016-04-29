package org.headvances.swingui.component;

import java.awt.Dimension;

import javax.swing.JLabel;

/**
 * $Author: Tuan Nguyen$ 
 **/
public class StatusBar extends JLabel {
	public StatusBar() {
		super(" Status: ") ;
		Dimension d = this.getPreferredSize();  
		this.setPreferredSize(new Dimension(d.width, d.height + 10));
	}
	
	public void setStatus(String action, String status) {
		setText(action + ": " + status) ;
	}
}
