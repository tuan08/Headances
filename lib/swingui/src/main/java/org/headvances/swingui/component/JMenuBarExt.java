package org.headvances.swingui.component;

import java.awt.Component;

import javax.swing.Action;
import javax.swing.JComponent;
import javax.swing.JMenu;
import javax.swing.JMenuBar;
import javax.swing.JMenuItem;
/**
 * $Author: Tuan Nguyen$ 
 **/
public class JMenuBarExt extends JMenuBar {

	public void registerMenuAction(String path, Action action) {
		findJMenu(this, path).add(action);
	}
	
	public void registerMenuAction(String path, JMenuItem item) {
		findJMenu(this, path).add(item);
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
