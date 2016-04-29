package org.headvances.swingui.component;

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
public class UpdatableJPanel extends JPanel {
	private JToolBar  toolBar;

	public UpdatableJPanel() {
		setLayout(new BorderLayout());
		toolBar = new JToolBar();
		toolBar.setFloatable(false);
		JButton updateBtn = new JButton("Update");
		updateBtn.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent event) {
				try {
					update();
				} catch (Exception ex) {
					SwingUtil.showError(UpdatableJPanel.this, "Component Update", ex);
				}
			}
		});
		toolBar.add(updateBtn);
		initJToolBar(toolBar) ;
		add(toolBar, BorderLayout.NORTH);
		add(createBodyPane(), BorderLayout.CENTER);
	}

	protected void initJToolBar(JToolBar toolbar) {
	}
	
	protected JComponent createBodyPane() { return null ; }

	protected void update() throws Exception {
	}
}