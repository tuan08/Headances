package org.headvances.ml.swingui.nlp.editor.wtag ;

import java.awt.Point;
import java.awt.event.MouseEvent;
import java.awt.event.MouseMotionAdapter;
import java.io.IOException;
import java.io.InputStream;
import java.io.Reader;
import java.util.List;

import javax.swing.JEditorPane;
import javax.swing.text.AttributeSet;
import javax.swing.text.BadLocationException;
import javax.swing.text.Document;
import javax.swing.text.Element;
import javax.swing.text.StyledEditorKit;

import org.headvances.ml.nlp.SuggestTag;
import org.headvances.ml.swingui.nlp.editor.TextEditor;
import org.headvances.nlp.token.IToken;
import org.headvances.nlp.token.tag.CharacterTag;
import org.headvances.nlp.token.tag.DigitTag;
import org.headvances.nlp.token.tag.NumberTag;
import org.headvances.nlp.token.tag.PosTag;
import org.headvances.nlp.token.tag.TokenTag;
import org.headvances.nlp.token.tag.WordTag;
import org.headvances.nlp.wtag.WTagBoundaryTag;
import org.headvances.swingui.SwingUtil;
import org.headvances.util.IOUtil;
import org.headvances.util.text.StringUtil;

public class WTagEditorKit extends StyledEditorKit {
	private WTagContext preferences = new WTagContext() ;
	
  public WTagContext getStylePreferences() { return preferences; }
  public void setStylePreferences(WTagContext prefs) { preferences = prefs; }

  public String getContentType() { return "text/tagged"; }

  public Object clone() {
    WTagEditorKit kit = new WTagEditorKit();
    kit.preferences = preferences;
    return kit;
  }

  public Document createDefaultDocument() {  return new WTagDocument(preferences);  }

  public void read(Reader in, Document doc, int pos) throws IOException, BadLocationException {
  	String string = IOUtil.getStreamContentAsString(in) ;
  	read(string, doc, pos) ;
  }
  
  public void read(InputStream in, Document doc, int pos) throws IOException, BadLocationException {
  	String string = IOUtil.getStreamContentAsString(in, "UTF-8") ;
  	read(string, doc, pos) ;
  }
  
  public void read(String text, Document doc, int pos) throws IOException, BadLocationException {
  	long start = System.currentTimeMillis() ;
  	WTagDocument wtdoc = (WTagDocument) doc ;
  	wtdoc.read(text, pos) ;
  	//System.out.println("Open File in " + (System.currentTimeMillis() - start) + "ms");
  }
  
  static public class WTagMouseMotionListener extends MouseMotionAdapter {
    public void mouseMoved(MouseEvent e) {
    	JEditorPane editor = (JEditorPane) e.getSource();
    	Point pt = new Point(e.getX(), e.getY());
    	int pos = editor.viewToModel(pt);
    	WTagDocument doc = (WTagDocument)editor.getDocument() ;
    	Element ele = doc.getCharacterElement(pos) ;
    	TextEditor teditor = SwingUtil.findAncestorOfType(editor, TextEditor.class) ;
    	AttributeSet style = ele.getAttributes() ;
    	IToken token = (IToken) style.getAttribute("token") ;
    	if(token != null) {
    		teditor.setStatus(getTagInfo(token)) ;
    	} else {
    		teditor.setStatus(StringUtil.EMPTY_ARRAY) ;
    	}
    }
    
    public String[] getTagInfo(IToken token) {
    	List<TokenTag> tags = token.getTag() ;
      if(tags == null) return StringUtil.EMPTY_ARRAY ;
    	StringBuilder bBuf = new StringBuilder() ;
    	StringBuilder tBuf = new StringBuilder() ;
    	StringBuilder meaningBuf = new StringBuilder() ;
    	for(int i = 0; i < tags.size(); i++) {
    		TokenTag tag = tags.get(i) ;
    		if(tag instanceof WTagBoundaryTag || tag instanceof SuggestTag) {
    			if(bBuf.length() > 0) bBuf.append(" | ") ;
    			bBuf.append(tag.getInfo()) ;
    		} else if(tag instanceof DigitTag || tag instanceof NumberTag || 
    				      tag instanceof CharacterTag || tag instanceof WordTag ||
    				      tag instanceof PosTag) {
    			if(tBuf.length() > 0) tBuf.append(" | ") ;
    			tBuf.append(tag.getInfo()) ;
    		} else {
    			if(meaningBuf.length() > 0) meaningBuf.append(" | ") ;
    			meaningBuf.append(tag.getInfo()) ;
    		}
    	}
    	String[] message = {bBuf.toString(), tBuf.toString(), meaningBuf.toString()} ;
    	return message;
    }
  }
}