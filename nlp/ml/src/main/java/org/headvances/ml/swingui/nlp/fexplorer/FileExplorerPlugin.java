package org.headvances.ml.swingui.nlp.fexplorer;

import java.awt.BorderLayout;
import java.awt.event.ActionEvent;
import java.io.File;

import javax.swing.AbstractAction;
import javax.swing.Action;
import javax.swing.JPanel;
import javax.swing.JTabbedPane;

import org.headvances.ml.swingui.nlp.ControlWorkspace;
import org.headvances.ml.swingui.nlp.NLPPane;
import org.headvances.ml.swingui.nlp.NLPPlugin;
import org.headvances.ml.swingui.nlp.config.PluginConfig;
import org.headvances.ml.swingui.nlp.editor.TextEditor;

public class FileExplorerPlugin extends JPanel implements NLPPlugin {
	final static public String[] SUPPORT_TYPE = {
		"txt", "text", "java", "xml", "html", "xhtml", "css"
	};
	
	private PluginConfig plugin ;
	private FileTreePanel fileTreePanel ;

  public void init(PluginConfig plugin) {
  	this.plugin = plugin ;
  	setLayout(new BorderLayout()) ;
		JTabbedPane tabbedPane = new JTabbedPane();
		this.fileTreePanel = new FileTreePanel() ;
		tabbedPane.addTab("Explorer", null, fileTreePanel, "File System Explorer");
		tabbedPane.addTab("Search", null, new JPanel(), "Search File");
		add(tabbedPane, BorderLayout.CENTER) ;
		setAutoscrolls(true) ;
  }

  public PluginConfig getPluginConfig() { return plugin ; }

  public void onInit(final NLPPane app) {
  	ControlWorkspace cspace = app.getControlWorkspace() ;
  	cspace.setView(this) ;
  	app.getWorkingWorkspace().addFileEditorPlugin(new TextFileEditorPlugin()) ;
  
  	Action fileExplorerWS = new AbstractAction("File Explorer") {
      public void actionPerformed(ActionEvent e) {
      	app.getControlWorkspace().setView(FileExplorerPlugin.this) ;
      }
  	};
  	cspace.registerMenuAction("Workspace", fileExplorerWS) ;
  }

  public void onDestroy(NLPPane app) { }

  public void connect(NLPPlugin aplugin) { }

  public void disconnect(NLPPlugin aplugin) { }
  
  class TextFileEditorPlugin implements FileEditorPlugin {
    public String[] getSupportTypes() { return SUPPORT_TYPE ; }

    public JPanel createFileEditor(File file) {
    	TextEditor editor = new TextEditor(plugin) ;
  	  editor.open(file) ;
    	return editor ;
    }
  }
}