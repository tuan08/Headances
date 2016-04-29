package org.headvances.ml.swingui.nlp.fexplorer;

import java.io.File;

import javax.swing.JPanel;

public interface FileEditorPlugin {
	public String[] getSupportTypes() ;
	public JPanel   createFileEditor(File file) ;
}
