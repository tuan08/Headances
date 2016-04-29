package org.headvances.ml.swingui.nlp;

import java.awt.BorderLayout;
import java.awt.Component;
import java.awt.event.ActionEvent;

import javax.swing.AbstractAction;
import javax.swing.Action;
import javax.swing.JComponent;
import javax.swing.JMenu;
import javax.swing.JMenuBar;
import javax.swing.JPanel;

import org.headvances.swingui.SwingApplication;
import org.headvances.swingui.SwingUtil;

public class ControlWorkspace extends JPanel {
	private JComponent currentView ;
	private JMenuBar menuBar ;
	
	public ControlWorkspace() {
		setLayout(new BorderLayout()) ;
		add(createMenuBar(), BorderLayout.NORTH) ;
	}

	public void setView(JComponent component) {
		if(component == null) return ;
		if(component == currentView) return ;
		if(this.currentView != null) remove(currentView) ;
		currentView = component ;
		add(currentView, BorderLayout.CENTER) ;
		updateUI();
	}
	
	public void registerMenuAction(String path, Action action) {
		findJMenu(menuBar, path).add(action);
	}
	
	private JMenuBar createMenuBar() {
		this.menuBar = new JMenuBar();
		Action exitAction = new AbstractAction("Exit") {
      public void actionPerformed(ActionEvent e) {
      	SwingApplication app = SwingUtil.findAncestorOfType(ControlWorkspace.this, SwingApplication.class) ;
      	app.onDestroy() ;
      	System.exit(0) ;
      }
		};
		findJMenu(menuBar, "Workspace") ;
		findJMenu(menuBar, "File > New") ;
		registerMenuAction("File", exitAction) ;
		return menuBar ;
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
