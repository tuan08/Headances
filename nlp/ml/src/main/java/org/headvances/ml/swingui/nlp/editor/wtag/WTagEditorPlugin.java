package org.headvances.ml.swingui.nlp.editor.wtag;

import java.awt.event.ActionEvent;
import java.io.File;

import javax.swing.AbstractAction;
import javax.swing.Action;
import javax.swing.JPanel;

import org.headvances.ml.swingui.nlp.NLPPane;
import org.headvances.ml.swingui.nlp.NLPPlugin;
import org.headvances.ml.swingui.nlp.config.PluginConfig;
import org.headvances.ml.swingui.nlp.fexplorer.FileEditorPlugin;

public class WTagEditorPlugin implements NLPPlugin {
	final static public String[] SUPPORT_TYPE = { "tagged", "wtag" };
	
	private PluginConfig plugin ;
  public void init(PluginConfig plugin) {
  	this.plugin = plugin ;
  }

  public PluginConfig getPluginConfig() { return this.plugin ; }

  public void onInit(final NLPPane app) {
  	app.getWorkingWorkspace().addFileEditorPlugin(new WTagFileEditorPlugin()) ;
  	Action newWtagEditorAction = new AbstractAction("WTag Editor") {
      public void actionPerformed(ActionEvent e) {
      	WTagEditor editor = new WTagEditor(plugin) ;
      	String id = Integer.toString(editor.hashCode()) ;
      	app.getWorkingWorkspace().addTabView("New WTag Editor", id, editor) ;
      }
  	};
  	app.getControlWorkspace().registerMenuAction("File>New", newWtagEditorAction) ;
  }

  public void onDestroy(NLPPane app) {
  }

  public void connect(NLPPlugin aplugin) {
  }

  public void disconnect(NLPPlugin aplugin) {
  }
  
  public WTagEditor createEditor() {
  	WTagEditor editor = new WTagEditor(plugin) ;
  	return editor ;
  }
  
  public class WTagFileEditorPlugin implements FileEditorPlugin {
    public String[] getSupportTypes() { return SUPPORT_TYPE ; }

    public JPanel createFileEditor(File file) {
    	WTagEditor editor = new WTagEditor(plugin) ;
  	  editor.open(file) ;
    	return editor ;
    }
  }
}
