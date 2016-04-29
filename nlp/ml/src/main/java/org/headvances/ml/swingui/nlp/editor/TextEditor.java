package org.headvances.ml.swingui.nlp.editor;

import java.awt.BorderLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.File;
import java.io.FileOutputStream;

import javax.swing.JButton;
import javax.swing.JComponent;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JToolBar;

import org.headvances.ml.swingui.nlp.config.PluginConfig;
import org.headvances.swingui.component.Lifecycle;
import org.headvances.util.ExceptionUtil;
import org.headvances.util.IOUtil;
import org.headvances.util.text.StringUtil;

public class TextEditor extends JPanel implements Lifecycle {
	final static public String[] SUPPORT_EXT = {
		"txt", "text", "java", "xml", "html", "xhtml", "css"
	};
	
	private File file  ;
	private JTextPaneEditor textArea ;
	private JLabel status ;
	private CompoundUndoManager undoManager ;
	private PluginConfig pluginConfig ;
	private JScrollPane currentView ;
	
	public TextEditor(PluginConfig plugin) {
		this.pluginConfig = plugin ;
		setLayout(new BorderLayout()) ;
		textArea = new JTextPaneEditor() ;
		initialize(textArea) ;
		textArea.initDocumentListener() ;
		JToolBar toolbar = new JToolBar();
		initialize(toolbar) ;
		
		add(toolbar, BorderLayout.NORTH) ;
		setCurrentView(textArea) ;
		
		status = new JLabel() ;
		add(status, BorderLayout.SOUTH) ;
	}
	
	protected void initialize(JToolBar toolBar) {
		toolBar.setFloatable(false) ;
		undoManager = new CompoundUndoManager(textArea);
		JButton undo = new JButton(undoManager.getUndoAction());
		JButton redo = new JButton(undoManager.getRedoAction());
		toolBar.add(undo) ;
		toolBar.add(redo) ;
		JButton saveButton = new JButton("Save") ;
		ActionListener saveListener = new ActionListener() {
      public void actionPerformed(ActionEvent event) {
      	save() ;
      }
		};
		saveButton.addActionListener(saveListener) ;
		toolBar.add(saveButton) ;
	}
	
	protected void initialize(JTextPaneEditor textArea) { 
	}
	
	public PluginConfig getPluginConfig() { return this.pluginConfig ; }
	
	public void setCurrentView(JComponent comp) {
		if(currentView == null) {
			currentView = new JScrollPane(comp) ;
			add(currentView, BorderLayout.CENTER) ;
		} else {
			currentView.getViewport().setView(comp) ;
		}
	}
	
  public void onInit() {
  }

  public void onDestroy() {
  	if(textArea.isModified()) {
  		String mesg = "The file is modified, do you want to save?" ;
  		int confirm = JOptionPane.showConfirmDialog(this, mesg, "Modified File", JOptionPane.YES_NO_OPTION);
  		if(confirm == JOptionPane.YES_OPTION) {
				save() ;
			}
  	}
  }
	
	public JTextPaneEditor getTextEditor() { return this.textArea ; }
	
	public void setStatus(String[] message) {
		StringBuilder b = new StringBuilder() ;
		b.append("<html>") ;
		int msgCount = 0;
		for(int i = 0; i < message.length; i++) {
			if(message[i].length() > 0) {
				b.append(message[i]).append("<br/>") ;
				msgCount++ ;
			}
		}
		for(int i = msgCount; i < 3; i++) {
			b.append("&nbsp;").append("<br/>") ;
		}
		b.append("</html>") ;
		status.setText(b.toString()) ; 
	}
	
	public File getFile() { return this.file ; }
	
	public void open(File file) {
		this.file = file ;
		try {
			String text = IOUtil.getFileContentAsString(file, "UTF-8") ;
			textArea.setText(text) ;
			undoManager.discardAllEdits() ;
			textArea.setModified(false) ;
		} catch(Exception ex) {
			String msg = "Cannot open the file " + file.getName() + "\n" ;
			msg += ex.getMessage() + "\n";
			msg += ExceptionUtil.getStackTrace(ex, null) ;
			ex.printStackTrace() ;
			JOptionPane.showMessageDialog(this, msg, "Open File", JOptionPane.ERROR_MESSAGE) ;
		}
	}
	
  public void save() {
  	if(file == null) return ;
  	try {
  		String text = textArea.getText(); 
  		FileOutputStream os = new FileOutputStream(file) ;
  		os.write(text.getBytes(StringUtil.UTF8)) ;
  		os.close() ;
  		textArea.setModified(false) ;
  	} catch (Exception e) {
	    String msg = e.getMessage() ;
	    msg += "\n" + ExceptionUtil.getStackTrace(e, null) ;
	    JOptionPane.showMessageDialog(this, msg, "Save", JOptionPane.ERROR_MESSAGE) ;
    }
  }  
}