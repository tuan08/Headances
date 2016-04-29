package org.headvances.ml.swingui.nlp.editor;

import java.awt.event.ItemEvent;
import java.awt.event.ItemListener;

import javax.swing.Action;
import javax.swing.JComponent;
import javax.swing.JMenuItem;
import javax.swing.JPopupMenu;
import javax.swing.JToggleButton;
import javax.swing.SwingConstants;
import javax.swing.event.PopupMenuEvent;
import javax.swing.event.PopupMenuListener;
/**
 * $Author: Tuan Nguyen$ 
 **/
public class DropDownButton extends JToggleButton {
	private JPopupMenu menu ;

	public DropDownButton(String label) {
		super(label + "▼") ;
		this.menu = new JPopupMenu();
		menu.addPopupMenuListener(new PopupMenuListener() {
			public void popupMenuWillBecomeVisible(PopupMenuEvent e) {
			}

			public void popupMenuWillBecomeInvisible(PopupMenuEvent e) {
				DropDownButton.this.setSelected(false);
			}

			public void popupMenuCanceled(PopupMenuEvent e) {
				DropDownButton.this.setSelected(false);
			}
		});
		addItemListener(new ItemListener() {
			public void itemStateChanged(ItemEvent e) {
				if (e.getStateChange() == ItemEvent.SELECTED) {
					JComponent component = (JComponent) e.getSource() ; 
					menu.show(component, 0, component.getHeight());
				}
			}
		});

		setFocusable(false);
		setHorizontalTextPosition(SwingConstants.LEADING);
	}

	public void add(JMenuItem item) {
		menu.add(item);
	}

	public void add(String path, Action action) {
		menu.add(action);
	}

	public void add(Action action) {
		menu.add(action);
	}
}
