package org.headvances.swingui.component;

import java.awt.Component;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;

import javax.swing.Action;
import javax.swing.JComponent;
import javax.swing.JMenu;
import javax.swing.JMenuItem;
import javax.swing.JPopupMenu;
/**
 * $Author: Tuan Nguyen$ 
 **/
public class JPopupMenuExt extends JPopupMenu {

	public JPopupMenuExt() {
	}

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

	public PopupListener createPopupTriggerListener() {
		return new PopupListener(this) ;
	}
	
	private Component[] getComponents(JComponent menu) {
		if(menu instanceof JMenu) {
			JMenu jmenu = (JMenu) menu ;
			return jmenu.getMenuComponents();
		} else {
			return menu.getComponents() ;
		}
	}
	
	static class PopupListener extends MouseAdapter {
    JPopupMenu popup;

    PopupListener(JPopupMenu popupMenu) {
      popup = popupMenu;
    }

    public void mousePressed(MouseEvent e) {
      maybeShowPopup(e);
    }

    public void mouseReleased(MouseEvent e) {
      maybeShowPopup(e);
    }

    private void maybeShowPopup(MouseEvent e) {
      if (e.isPopupTrigger()) {
        popup.show(e.getComponent(), e.getX(), e.getY());
      }
    }
  }
}