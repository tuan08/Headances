package org.headvances.ml.swingui.nlp.editor.wtag;

import java.awt.Component;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.ItemEvent;
import java.awt.event.ItemListener;
import java.util.ArrayList;
import java.util.List;

import javax.swing.JCheckBoxMenuItem;
import javax.swing.JMenu;
import javax.swing.JMenuItem;
import javax.swing.JPopupMenu;

import org.headvances.ml.swingui.nlp.config.ActionConfig;
import org.headvances.ml.swingui.nlp.config.ActionSet;
import org.headvances.ml.swingui.nlp.editor.JTextPaneEditor;

/**
 * $Author: Tuan Nguyen$ 
 **/
public class TagPopupManager {
	private List<ActionSetSelection> tagSets ;
	private WTagEditor editor ;
	
	public TagPopupManager(WTagEditor editor, JPopupMenu popup, List<ActionSet> tagSets) {
		this.editor = editor ;
		this.tagSets = new ArrayList<ActionSetSelection>() ;
		for(int i = 0; i < tagSets.size(); i++) {
			ActionSet actionSet = tagSets.get(i) ;
			String name = actionSet.getName() ;
			boolean select = false ;
			if(name.endsWith(".np") || name.endsWith(".num")) select = true ;
			ActionSetSelection ass = new ActionSetSelection(actionSet, select) ;
			this.tagSets.add(ass) ;
		}
		addTagSetSelection(popup) ;
		popup.addSeparator() ;
		addTagSelection(popup) ;
	}
	
	void addTagSetSelection(final JPopupMenu popup) {
		JMenu tagMenu = new JMenu("Tag Set") ;
		popup.add(tagMenu) ;
		for(int i = 0; i < tagSets.size(); i++) {
			final ActionSetSelection actionSet = tagSets.get(i) ;
			JCheckBoxMenuItem tagSetItem = 
				new JCheckBoxMenuItem(actionSet.actionSet.getLabel(), actionSet.select);
			tagSetItem.addItemListener(new ItemListener() {
        public void itemStateChanged(ItemEvent e) {
        	actionSet.select = e.getStateChange() == ItemEvent.SELECTED ;
        	addTagSelection(popup) ;
        }
			}) ;
			tagMenu.add(tagSetItem);
		}
		tagMenu.addSeparator() ;
		JMenuItem clearEntityTagItem = new JMenuItem("Clear Entity");
		clearEntityTagItem.addActionListener(new ActionListener() {
      public void actionPerformed(ActionEvent e) {
      	JTextPaneEditor textArea = editor.getTextEditor() ;
      	WTagDocument doc = (WTagDocument) textArea.getStyledDocument() ;
	      doc.clearEntityTag(textArea.getSelectionStart(), textArea.getSelectionEnd()) ;
      }
		}) ;
		tagMenu.add(clearEntityTagItem) ;
		
		editor.toggleEntityHighlighter((WTagDocument)editor.getTextEditor().getDocument(), true) ;
		HighlightTagItemListener hlListener = new HighlightTagItemListener() ;
		JCheckBoxMenuItem hlEntItem = new JCheckBoxMenuItem("Highlight Entity", true);
		hlEntItem.addItemListener(hlListener);
		tagMenu.add(hlEntItem);
	}
	
	void addTagSelection(JPopupMenu popup) {
		Component[] components = popup.getComponents() ;
  	for(int i = 0; i < components.length; i++) {
  		if(components[i] instanceof SelectTagItem) {
  			popup.remove(components[i]) ;
  		}
  	}
		for(int i = 0; i < tagSets.size(); i++) {
			ActionSetSelection selection = tagSets.get(i) ;
			if(!selection.select) continue ;
			ActionSet actionSet = selection.actionSet ;
			for(final ActionConfig sel : actionSet.getActions()) {
				SelectTagItem tagItem = new SelectTagItem(sel.getLabel());
				tagItem.addActionListener(new ActionListener() {
					public void actionPerformed(ActionEvent e) {
						JTextPaneEditor textArea = editor.getTextEditor() ;
						WTagDocument doc = (WTagDocument) textArea.getStyledDocument() ;
						doc.tagEntity(textArea.getSelectionStart(), textArea.getSelectionEnd(), sel.getName()) ;
					}
				}) ;
				popup.add(tagItem) ;
			}
		}
	}
	
	static class SelectTagItem extends JMenuItem {
		public SelectTagItem(String label) { super(label); }
	}
	
	static public class ActionSetSelection {
		ActionSet actionSet ;
		boolean   select = false  ;
		
		ActionSetSelection(ActionSet actionSet, boolean select) {
			this.actionSet = actionSet ;
			this.select    = select ;
		}
	}
	
	class HighlightTagItemListener implements ItemListener {
    public void itemStateChanged(ItemEvent e) {
    	WTagDocument wtdoc = 
    		(WTagDocument) editor.getTextEditor().getStyledDocument() ;
    	editor.toggleEntityHighlighter(wtdoc, e.getStateChange() == ItemEvent.SELECTED) ;
    	wtdoc.highlight() ;
    }
	}
}
