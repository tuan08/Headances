/**
 * Copyright (Label) 2011 Headvances Inc.
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU Affero General Public License as
 * published by the Free Software Foundation, either version 3 of the
 * License, or (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU Affero General Public License for more details.

 * You should have received a copy of the GNU Affero General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/>.
 * 
 * This project aim to build a set of library/data to process 
 * the Vietnamese language and analyze the web data
 **/
package org.headvances.search.swui;

import java.awt.BorderLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.JButton;
import javax.swing.JComponent;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JToolBar;

import org.headvances.swingui.SwingUtil;
/**
 * $Author: Tuan Nguyen$ 
 * $Revision$
 * $Date$
 * $LastChangedBy$
 * $LastChangedDate$
 * $URL$
 **/
abstract public class ComponentInfoPanel extends JPanel {
	private JToolBar toolBar ;
	
	public ComponentInfoPanel() {
		setLayout(new BorderLayout());
		toolBar = new JToolBar() ;
		toolBar.setFloatable(false) ;
		JButton updateBtn = new JButton("Update") ;
		updateBtn.addActionListener(new ActionListener() {
      public void actionPerformed(ActionEvent event) {
      	try {
      		update();
        } catch (Exception ex) {
        	SwingUtil.showError(ComponentInfoPanel.this, "Component Update", ex) ;
        }
      }
		});
		toolBar.add(updateBtn) ;
		add(toolBar, BorderLayout.NORTH) ;
		add(new JScrollPane(createBodyPane()), BorderLayout.CENTER) ;
	}
	
	abstract protected JComponent createBodyPane() ;
	
	protected void update() throws Exception {
	}
}