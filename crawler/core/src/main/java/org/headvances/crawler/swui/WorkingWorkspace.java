package org.headvances.crawler.swui;

import java.awt.BorderLayout;
import java.awt.Component;

import javax.swing.JComponent;
import javax.swing.JPanel;
import javax.swing.JTabbedPane;

import org.headvances.swingui.component.ClosableTabButton;
import org.headvances.swingui.component.Lifecycle;

public class WorkingWorkspace extends JPanel {
	private JTabbedPane tabbedPane ;
	
	public WorkingWorkspace() {
		setLayout(new BorderLayout()) ;
		tabbedPane = new JTabbedPane();
		tabbedPane.setTabLayoutPolicy(JTabbedPane.SCROLL_TAB_LAYOUT);
		add(tabbedPane, BorderLayout.CENTER) ;
	}
	
	public void addTabView(String title, JComponent jpanel) {
		for(int i = 0; i < tabbedPane.getTabCount(); i++) {
			String checkTitle = tabbedPane.getTitleAt(i) ;
			if(title.equals(checkTitle)) {
				Component comp = tabbedPane.getComponentAt(i) ;
				if(comp instanceof Lifecycle) {
					((Lifecycle)comp).onDestroy() ;
				}
				tabbedPane.remove(i) ;
				break ;
			}
		}
		int index = tabbedPane.getTabCount() ;
		tabbedPane.add(title, jpanel);
		ClosableTabButton ctBtn = new ClosableTabButton(tabbedPane) ;
		if(jpanel instanceof Lifecycle) ctBtn.setLifecycle((Lifecycle) jpanel) ;
		tabbedPane.setTabComponentAt(index, ctBtn);
		tabbedPane.setSelectedIndex(index);
	}
}