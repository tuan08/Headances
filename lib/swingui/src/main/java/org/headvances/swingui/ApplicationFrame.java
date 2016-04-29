package org.headvances.swingui;

import java.awt.Container;

import javax.swing.JInternalFrame;

/**
 * $Author: Tuan Nguyen$ 
 **/
public class ApplicationFrame extends JInternalFrame {
	static final int xOffset = 5, yOffset = 5;
	
	public ApplicationFrame(String name) {
		super(name, true/*resizable*/, true/*closable*/, true/*maximizable*/, true/*iconifiable*/);
		setSize(800, 500);
		setLocation(xOffset, yOffset);
	}
	
	public ApplicationFrame(String name, Container container) {
		this(name) ;
		setContentPane(container) ;
	}
}