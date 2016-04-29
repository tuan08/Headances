package org.headvances.swingui.component;

import java.awt.Component;
import java.awt.event.ItemEvent;
import java.awt.event.ItemListener;

import javax.swing.Action;
import javax.swing.JCheckBoxMenuItem;
import javax.swing.JComponent;
import javax.swing.JMenu;
import javax.swing.JMenuItem;
import javax.swing.JPopupMenu;
import javax.swing.JRadioButtonMenuItem;
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
		super(label + " ▾") ;
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

	public void registerMenuAction(String path, Action action) {
		findJMenu(menu, path).add(action);
	}
	
	public void registerMenuAction(String path, JCheckBoxMenuItem menuItem) {
    findJMenu(menu, path).add(menuItem);
  }
	
	public void registerMenuAction(String path, JRadioButtonMenuItem menuItem) {
    findJMenu(menu, path).add(menuItem);
  }

	private JMenu findJMenu(JComponent menu, String paths) {
		String[] path = paths.split(">") ;
		for(int i = 0; i < path.length; i++) path[i] = path[i].trim() ;
		return findJMenu(menu, path, 0) ;
	}

	private JMenu findJMenu(JComponent menu, String[] path, int index) {
		Component[] component = getComponents(menu) ;
		for(int i = 0; i < component.length; i++) {
			if(!(component[i] instanceof JMenu)) continue ;
			JMenu subMenu = (JMenu) component[i] ;
			String label = subMenu.getText() ;
			if(path[index].equals(label)) {
				if(index + 1 == path.length) return (JMenu) subMenu ;
				return findJMenu(subMenu, path, index + 1) ;
			}
		}
		JMenu subMenu = new JMenu(path[index]) ;
		menu.add(subMenu) ;
		if(index + 1 == path.length) return subMenu ;
		return findJMenu(subMenu, path, index + 1) ;
	}

	private Component[] getComponents(JComponent menu) {
		if(menu instanceof JMenu) {
			JMenu jmenu = (JMenu) menu ;
			return jmenu.getMenuComponents();
		} else {
			return menu.getComponents() ;
		}
	}
}
