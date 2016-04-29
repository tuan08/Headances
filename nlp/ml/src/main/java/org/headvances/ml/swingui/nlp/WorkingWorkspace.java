package org.headvances.ml.swingui.nlp;

import java.awt.BorderLayout;
import java.awt.Component;
import java.io.File;
import java.util.HashMap;
import java.util.Map;

import javax.swing.JComponent;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JTabbedPane;

import org.headvances.ml.swingui.nlp.fexplorer.FileEditorPlugin;
import org.headvances.swingui.component.ClosableTabButton;
import org.headvances.swingui.component.Lifecycle;

public class WorkingWorkspace extends JPanel implements Lifecycle {
	private JTabbedPane tabbedPane ;
	private Map<String, FileEditorPlugin> fileEditorPlugins = new HashMap<String, FileEditorPlugin>();
	
	public WorkingWorkspace() {
		setLayout(new BorderLayout()) ;
		tabbedPane = new JTabbedPane();
		tabbedPane.setTabLayoutPolicy(JTabbedPane.SCROLL_TAB_LAYOUT);
		add(tabbedPane, BorderLayout.CENTER) ;
	}
	
  public void onInit() {
  }

  public void onDestroy() {
  	int count = tabbedPane.getTabCount() ;
  	for(int i = 0; i < count; i++) {
  	  Component comp = tabbedPane.getTabComponentAt(i) ;
  	  ClosableTabButton ctBtn = (ClosableTabButton) comp ;
  	  Lifecycle lifecycle = ctBtn.getLifecycle() ;
  	  if(lifecycle != null) {
  	  	lifecycle.onDestroy() ; 
  	  }
  	}
  }
	
	public void addTabView(String title, String id, JComponent jpanel) {
		int index = tabbedPane.getTabCount() ;
		tabbedPane.add(title, jpanel);
		ClosableTabButton ctBtn = new ClosableTabButton(tabbedPane) ;
		if(jpanel instanceof Lifecycle) ctBtn.setLifecycle((Lifecycle) jpanel) ;
		tabbedPane.setTabComponentAt(index, ctBtn);
		tabbedPane.setSelectedIndex(index);
	}
	
	public void addFileEditorPlugin(FileEditorPlugin plugin) {
		String[] supportType = plugin.getSupportTypes() ;
		for(String sel : supportType) {
			this.fileEditorPlugins.put(sel, plugin) ;
		}
	}
	
	public void openFile(File file) {
		String fname = file.getName() ;
		String ext = fname.substring(fname.lastIndexOf(".") + 1) ;
		FileEditorPlugin plugin = this.fileEditorPlugins.get(ext) ;
		if(plugin != null) {
			addTabView(fname, file.getAbsolutePath(), plugin.createFileEditor(file)) ;
			return ;
		}
		String msg = "Not suported format" ;
		JOptionPane.showMessageDialog(this, msg, "Open File", JOptionPane.ERROR_MESSAGE) ;
	}
}
