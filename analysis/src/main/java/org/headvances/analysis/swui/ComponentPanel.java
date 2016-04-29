package org.headvances.analysis.swui;

import java.awt.BorderLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.JButton;
import javax.swing.JComponent;
import javax.swing.JPanel;
import javax.swing.JToolBar;

import org.headvances.swingui.SwingUtil;
/**
 * $Author: Tuan Nguyen$ 
 **/
abstract public class ComponentPanel extends JPanel {
	private JToolBar toolBar ;
	
	public ComponentPanel() {
		setLayout(new BorderLayout());
		toolBar = new JToolBar() ;
		toolBar.setFloatable(false) ;
		JButton updateBtn = new JButton("Update") ;
		updateBtn.addActionListener(new ActionListener() {
      public void actionPerformed(ActionEvent event) {
      	try {
      		update();
        } catch (Exception ex) {
        	SwingUtil.showError(ComponentPanel.this, "Component Update", ex) ;
        }
      }
		});
		toolBar.add(updateBtn) ;
		add(toolBar, BorderLayout.NORTH) ;
		initJToolBar(toolBar) ;
		add(createBodyPane(), BorderLayout.CENTER) ;
	}
	
	protected void initJToolBar(JToolBar toolBar) {
		
	}
	
	abstract protected JComponent createBodyPane() ;
	
	protected void update() throws Exception {
	}
}