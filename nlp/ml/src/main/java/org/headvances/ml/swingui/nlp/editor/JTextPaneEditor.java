package org.headvances.ml.swingui.nlp.editor;

import java.awt.Font;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;

import javax.swing.JPopupMenu;
import javax.swing.JTextPane;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;

public class JTextPaneEditor extends JTextPane {
	private JPopupMenu popup ;
	private boolean modified = false; 
	
	public JTextPaneEditor() {
    setFont(new Font(Font.MONOSPACED, Font.TRUETYPE_FONT, 14));
		initPopupMenu() ;
	}
	
	private void initPopupMenu() {
		this.popup = new JPopupMenu();
		//Add listener to the text area so the popup menu can come up.
		MouseListener popupListener = new PopupListener(popup);
		addMouseListener(popupListener);
	}
	
	public boolean isModified() { return this.modified ; }
	
	public void setModified(boolean b) { this.modified = b ; }
	
	public JPopupMenu getJPopupMenu() { return this.popup ; }

	public void initDocumentListener() {
		getStyledDocument().addDocumentListener(new ModifiedDocumentListener()) ;
	}
	
	class PopupListener extends MouseAdapter {
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
	
	class ModifiedDocumentListener implements DocumentListener {
    public void insertUpdate(DocumentEvent e) {
    	modified = true ;
    }

    public void removeUpdate(DocumentEvent e) {
    	modified = true ;
    }

    public void changedUpdate(DocumentEvent e) {
    	//modified = true ;
    }
	}
}