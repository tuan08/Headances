package org.headvances.ml.swingui.nlp.editor.wtag;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.ItemEvent;
import java.awt.event.ItemListener;
import java.io.File;
import java.util.ArrayList;
import java.util.List;

import javax.swing.AbstractAction;
import javax.swing.Action;
import javax.swing.Box;
import javax.swing.JButton;
import javax.swing.JCheckBoxMenuItem;
import javax.swing.JFileChooser;
import javax.swing.JMenuItem;
import javax.swing.JOptionPane;
import javax.swing.JPopupMenu;
import javax.swing.JToggleButton;
import javax.swing.JToolBar;
import javax.swing.text.BadLocationException;

import org.headvances.ml.nlp.ent.EntitySetConfig;
import org.headvances.ml.nlp.ent.MLENT;
import org.headvances.ml.nlp.feature.TokenFeatures;
import org.headvances.ml.nlp.feature.TokenFeaturesGenerator;
import org.headvances.ml.nlp.ws.MLWS;
import org.headvances.ml.swingui.nlp.ResourceFactory;
import org.headvances.ml.swingui.nlp.config.ActionSet;
import org.headvances.ml.swingui.nlp.config.PluginConfig;
import org.headvances.ml.swingui.nlp.editor.JTextPaneEditor;
import org.headvances.ml.swingui.nlp.editor.TextEditor;
import org.headvances.nlp.token.IToken;
import org.headvances.nlp.token.TextSegmenter;
import org.headvances.nlp.token.TokenCollection;
import org.headvances.nlp.wtag.WTagDocumentWriter;
import org.headvances.swingui.SwingUtil;
import org.headvances.swingui.component.DropDownButton;
import org.headvances.swingui.component.TableView;

public class WTagEditor extends TextEditor {
	private TagPopupManager tagPopupManager ;
	
	public WTagEditor(PluginConfig plugin) {
		super(plugin) ;
	}
	
	protected void initialize(JToolBar toolBar) {
		super.initialize(toolBar) ;
		toolBar.addSeparator() ;
		JButton editorBtn = new JButton("Editor") ;
		editorBtn.addActionListener(new ActionListener() {
      public void actionPerformed(ActionEvent e) {
      	WTagEditor.this.setCurrentView(WTagEditor.this.getTextEditor()) ;
      }
		});
		toolBar.add(editorBtn) ;
		JToggleButton hcWordBtn = new JToggleButton("Suggest Word") ;
		hcWordBtn.addItemListener(new ItemListener() {
      public void itemStateChanged(ItemEvent e) {
      	WTagDocument wtdoc = 
      		(WTagDocument) WTagEditor.this.getTextEditor().getStyledDocument() ;
      	if(e.getStateChange() == ItemEvent.SELECTED) {
      		wtdoc.getHighlighters().add("SuggestWord", new Highlighters.SuggestWordHighlighter());
      		wtdoc.highlight() ;
      	} else {
      		wtdoc.getHighlighters().remove("SuggestWord");
      		wtdoc.highlight() ;
      	}
      }
		});
		toolBar.add(hcWordBtn) ;
		toolBar.addSeparator() ;
		toolBar.add(createTextDropDownButton()) ;
		toolBar.add(createEntityDropDownButton()) ;
		
		toolBar.add(Box.createHorizontalGlue());
		JButton updateBtn = new JButton("Update") ;
		updateBtn.addActionListener(new ActionListener() {
      public void actionPerformed(ActionEvent e) {
      	WTagDocument wtdoc = (WTagDocument) WTagEditor.this.getTextEditor().getStyledDocument() ;
      	try {
	        wtdoc.updateTokens() ;
        } catch (Exception e1) {
        	SwingUtil.showError(WTagEditor.this, "Error", e1) ;
        }
      }
		});
		toolBar.add(updateBtn) ;
	}
	
	public void initialize(final JTextPaneEditor textArea) {
		super.initialize(textArea) ;
		textArea.setEditorKitForContentType("text/tagged", new WTagEditorKit()) ;
		textArea.addMouseMotionListener(new WTagEditorKit.WTagMouseMotionListener()) ;
		textArea.setContentType("text/tagged") ;
		
		
		PluginConfig plugin = getPluginConfig() ;
		JPopupMenu popup = textArea.getJPopupMenu() ;
		popup.addSeparator() ;
		JMenuItem tagWordItem = new JMenuItem("Word Boundary");
		tagWordItem.addActionListener(new ActionListener() {
      public void actionPerformed(ActionEvent e) {
      	WTagDocument doc = (WTagDocument) textArea.getStyledDocument() ;
      	try {
	        doc.tagWord(textArea.getSelectionStart(), textArea.getSelectionEnd()) ;
        } catch (BadLocationException ex) {
	        ex.printStackTrace() ;
	        JOptionPane.showMessageDialog(WTagEditor.this, ex.getMessage(), "Tag Word", JOptionPane.ERROR_MESSAGE) ;
        }
      }
		}) ;
		popup.add(tagWordItem) ;
		JMenuItem clearWordItem = new JMenuItem("Clear Word Boundary");
		clearWordItem.addActionListener(new ActionListener() {
      public void actionPerformed(ActionEvent e) {
      	WTagDocument doc = (WTagDocument) textArea.getStyledDocument() ;
      	try {
	        doc.clearTagWord(textArea.getSelectionStart(), textArea.getSelectionEnd()) ;
        } catch (BadLocationException ex) {
	        ex.printStackTrace() ;
	        JOptionPane.showMessageDialog(WTagEditor.this, ex.getMessage(), "Tag Word", JOptionPane.ERROR_MESSAGE) ;
        }
      }
		}) ;
		popup.add(clearWordItem) ;
		List<ActionSet> tagSets = plugin.getActionSetWithPrefix("popup.tag") ;
		tagPopupManager = new TagPopupManager(this, popup, tagSets) ;
	}
	
	void toggleEntityHighlighter(WTagDocument doc, boolean toggle) {
		if(toggle) {
			doc.getHighlighters().add("entity", new Highlighters.EntityHighlighter()) ;
		} else {
			doc.getHighlighters().remove("entity") ;
		}
	}
	
	private DropDownButton createTextDropDownButton() {
		DropDownButton textMenu = new DropDownButton("Text");
		
		textMenu.add(new TextSegmenterAction(ResourceFactory.getOpennlpTextSegmenter(), "Tokenize Word - Opennlp")) ;
		textMenu.add(new TextSegmenterAction(ResourceFactory.getCRFTextSegmenter(), "Tokenize Word - CRF")) ;
		textMenu.add(new TextSegmenterAction(ResourceFactory.getStatisticTextSegmenter(), "Tokenize Word - Statistic")) ;
		textMenu.add(new TextSegmenterAction(ResourceFactory.getLongestMatchingTextSegmenter(), "Tokenize Word - Longest Matching")) ;
		
		Action tknSentenceItem = new AbstractAction("View Sentences") {
			public void actionPerformed(ActionEvent e) {
				String[] header = {"Sentence"} ;
				WTagDocument wtdoc = 
					(WTagDocument) WTagEditor.this.getTextEditor().getStyledDocument() ;
				try {
	        TokenCollection[] collection = wtdoc.getTokenCollection() ;
	        String[][] data = new String[collection.length][1] ;
	        for(int i = 0; i < collection.length; i++) {
	        	data[i][0] = collection[i].getOriginalForm() ;
	        }
					TableView view = new TableView(header, data) ;
					view.setMultilineCellRenderer(true) ;
					WTagEditor.this.setCurrentView(view) ;
				} catch (Exception ex) {
	        ex.printStackTrace();
        }
			}
		};
		textMenu.add(tknSentenceItem) ;
		Action wsFeatureItem = new AbstractAction("View Features") {
			public void actionPerformed(ActionEvent e) {
				try {
	        TokenFeaturesGenerator featuresGenerator = MLWS.newTokenFeaturesGenerator() ;
	        WTagDocument wtdoc = 
	        	(WTagDocument) WTagEditor.this.getTextEditor().getStyledDocument() ;
	        TokenCollection[] collection = wtdoc.getTokenCollection() ;
	        for(TokenCollection sel : collection) {
	        	sel.analyze(MLWS.WS_ANALYZER) ;
	        }
					TableView view = getFeaturesView(collection, featuresGenerator) ;
					WTagEditor.this.setCurrentView(view) ;
				} catch (Exception e1) {
	        e1.printStackTrace();
        }
			}
		};
		textMenu.add(wsFeatureItem) ;
		
		Action tokenTableItem = new AbstractAction("Token Table") {
			public void actionPerformed(ActionEvent e) {
				WTagDocument wtdoc = 
      		(WTagDocument) WTagEditor.this.getTextEditor().getStyledDocument() ;
				WTagEditor.this.setCurrentView(new WBoundaryTagTView(wtdoc)) ;
			}
		};
		textMenu.add(tokenTableItem) ;
		return textMenu ;
	}
	
	private DropDownButton createEntityDropDownButton() {
		DropDownButton menu = new DropDownButton("Entity");
		Action viewFeatureItem = new AbstractAction("View Features") {
			public void actionPerformed(ActionEvent e) {
				try {
					long start = System.currentTimeMillis() ;
					EntitySetConfig config = EntitySetConfig.getConfig("all") ;
					TokenFeaturesGenerator featuresGenerator = 
						MLENT.newTokenFeaturesGenerator(config) ;
	        WTagDocument wtdoc = 
	        	(WTagDocument) WTagEditor.this.getTextEditor().getStyledDocument() ;
	        TokenCollection[] collection = wtdoc.getTokenCollection() ;
					TableView view = getFeaturesView(collection, featuresGenerator) ;
					WTagEditor.this.setCurrentView(view) ;
					
					String execStatus = "Execute: " + (System.currentTimeMillis() - start ) + "ms" ;
					WTagEditor.this.setStatus(new String[]{execStatus}) ;
				} catch (Exception ex) {
					SwingUtil.showError(WTagEditor.this, "Error", ex) ;
				}
			}
		} ;
		menu.add(viewFeatureItem) ;
		JCheckBoxMenuItem mlEntBtn = new JCheckBoxMenuItem("Highlight Suggest Entities") ;
		mlEntBtn.addItemListener(new ItemListener() {
      public void itemStateChanged(ItemEvent e) {
      	JTextPaneEditor editor = WTagEditor.this.getTextEditor() ;
				WTagDocument wtdoc = (WTagDocument) editor.getStyledDocument() ;
      	if(e.getStateChange() == ItemEvent.SELECTED) {
      		try {
      			TokenCollection[] collection = wtdoc.getTokenCollection() ;
      			wtdoc = wtdoc.clone() ;
      			wtdoc.read(collection) ;
      			wtdoc.getHighlighters().add("MLEntity", new Highlighters.MarkTagHighlighter());
	      		wtdoc.highlight() ;
	      		editor.setStyledDocument(wtdoc) ;
      		} catch (Exception e1) {
	          e1.printStackTrace();
	          return ;
          }
      	} else {
//      		for(IToken sel : new ElementSelector.TokenElementSelector().selectToken(wtdoc)) {
//      			sel.removeTagType(SuggestTag.class) ;
//      		}
      		wtdoc.getHighlighters().remove("MLEntity");
      		wtdoc.highlight() ;
      	}
      }
		});
		menu.add(mlEntBtn) ;
		return menu ;
	}
	
	private TableView getFeaturesView(TokenCollection[] collection, TokenFeaturesGenerator featuresGenerator) throws Exception {
    String[] header = {"Token", "-2", "-1", "0", "+1", "+2"} ;
		List<String[]> data = new ArrayList<String[]>() ;
		for(int i = 0; i < collection.length; i++) {
			IToken[] token = collection[i].getSingleTokens() ;
			TokenFeatures[] features = featuresGenerator.generate(token, true) ;
			for(int j = 0; j < token.length; j++) {
				String tm2 = "", tm1 = "", tp1 = "", tp2 = "" , tc = "";
				for(String feature : features[j].getFeatures()) {
					if(feature.startsWith("p2:")) tm2 += feature + " " ;
					else if(feature.startsWith("p1:")) tm1 += feature + " " ;
					else if(feature.startsWith("n1:")) tp1 += feature + " " ;
					else if(feature.startsWith("n2:")) tp2 += feature + " " ;
					else tc += feature + " " ;
				}
				if(features[j].getTargetFeature() != null) tc += features[j].getTargetFeature() ;
				StringBuilder b = new StringBuilder() ;
				String oform = token[j].getOriginalForm() ;
				for(int k = 0; k < oform.length(); k++) {
					if(k > 0) b.append(' ') ;
					b.append(oform.charAt(k)) ;
				}
				data.add(new String[]{b.toString(), tm2, tm1, tc, tp1, tp2 }) ;
			}
			data.add(new String[]{"", "", "", "", "", ""}) ;
		}
		TableView view = new TableView(header, data.toArray(new String[data.size()][])) ;
		view.setColWidth(new int[] {100});
		return view ;
	}
	
	public void save() {
		File file = getFile() ;
		if(file == null) {
			JFileChooser fc = new JFileChooser();
			int returnVal = fc.showSaveDialog(this);
			if (returnVal == JFileChooser.APPROVE_OPTION) {
				file = fc.getSelectedFile();
			} else {
				return ;
			}
		}
		if(file == null) return ;
  	try {
  		JTextPaneEditor textArea = getTextEditor() ; 
  		WTagDocument wtdoc = (WTagDocument)textArea.getStyledDocument() ;
  		new WTagDocumentWriter().write(file, wtdoc.getTokenCollection()) ;
  		textArea.setModified(false);
  	} catch (Exception e) {
	    SwingUtil.showError(this, "Save Error", e) ;
    }
  }
	
	class TextSegmenterAction extends  AbstractAction {
		private TextSegmenter segmenter ;
		
		public TextSegmenterAction(TextSegmenter segmenter, String label) {
			super(label) ;
			this.segmenter = segmenter ;
		}
		
		public void actionPerformed(ActionEvent e) {
			try {
				long start = System.currentTimeMillis() ;
				JTextPaneEditor editor = WTagEditor.this.getTextEditor() ;
				WTagDocument wtdoc = (WTagDocument) editor.getStyledDocument() ;
				String text = wtdoc.getText(0, wtdoc.getLength()) ;
				IToken[] token = segmenter.segment(text) ;
				String tokenizeStatus =  "Tokenize: " + (System.currentTimeMillis() - start) + "ms" ;
				
				wtdoc = wtdoc.clone() ;
				wtdoc.read(token) ;
				editor.setStyledDocument(wtdoc) ;
				String execStatus = "Execute: " + (System.currentTimeMillis() - start ) + "ms" ;
				WTagEditor.this.setStatus(new String[]{tokenizeStatus, execStatus}) ;
			} catch (Throwable e1) {
				SwingUtil.showError(WTagEditor.this, "Error", e1) ;
			}
		}
	} ;
}