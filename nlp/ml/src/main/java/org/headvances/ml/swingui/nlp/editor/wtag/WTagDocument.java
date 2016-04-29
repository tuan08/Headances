package org.headvances.ml.swingui.nlp.editor.wtag ;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import javax.swing.text.AttributeSet;
import javax.swing.text.BadLocationException;
import javax.swing.text.DefaultStyledDocument;
import javax.swing.text.Element;
import javax.swing.text.MutableAttributeSet;
import javax.swing.text.SimpleAttributeSet;
import javax.swing.text.StyleConstants;

import org.headvances.ml.swingui.nlp.ResourceFactory;
import org.headvances.nlp.token.IToken;
import org.headvances.nlp.token.Token;
import org.headvances.nlp.token.TokenCollection;
import org.headvances.nlp.token.analyzer.LineAnalyzer;
import org.headvances.nlp.util.CharacterSet;
import org.headvances.nlp.wtag.WTagBoundaryTag;
import org.headvances.util.text.StringUtil;

public class WTagDocument extends DefaultStyledDocument {
	private WTagContext context ;
	private Highlighters highlighters = new Highlighters() ;
	
	public WTagDocument(WTagContext context) {
		this.context = context ;
	}
	
	public WTagContext getContext() { return this.context ; }

	public Highlighters getHighlighters() { return this.highlighters ; }
	
	public WTagDocument clone() {
		WTagDocument newDoc = new WTagDocument(context) ;
		newDoc.highlighters = highlighters ;
		return newDoc ;
	}
	
	public void insertString(int offs, String str, AttributeSet a) throws BadLocationException {
		super.insertString(offs, str, a) ;
	}
	
	protected void insertUpdate(DefaultDocumentEvent chng, AttributeSet attr) {
		super.insertUpdate(chng, attr);
	}

	protected void removeUpdate(DefaultDocumentEvent chng) {
		super.removeUpdate(chng);
	}	
	
	public void tagWord(int start, int end) throws BadLocationException {
		if(start == end) {
			Element ele = getCharacterElement(start) ;
			start = ele.getStartOffset() ;
			end   = ele.getEndOffset() ;
		}
		String string = getText(start, end - start) ;
		IToken token = new Token(string) ;
		token.add(new WTagBoundaryTag(StringUtil.EMPTY_ARRAY)) ;
		MutableAttributeSet attrs = context.createAttributeSet(token) ;
		//setCharacterAttributes(offset, string.length(), attrs, true) ;
		replace(start, string.length(), string, attrs) ;
	}
	
	public void clearTagWord(int offset, int end) throws BadLocationException {
		String string = getText(offset, end - offset) ;
		replace(offset, string.length(), string, null) ;
	}
	
	public void tagEntity(int start, int end, String name) {
		if(start == end) {
			Element ele = getCharacterElement(start) ;
			start = ele.getStartOffset() ;
			end   = ele.getEndOffset() ;
		}
		List<Element> elements = 
			new ElementSelector.RangeElementSelector(start, end).select(this) ;
		boolean begin = true ;
		for(int i = 0; i < elements.size(); i++) {
			Element ele = elements.get(i) ;
			IToken token = (Token)ele.getAttributes().getAttribute("token") ;
			if(token == null) continue ;
			WTagBoundaryTag btag = token.getFirstTagType(WTagBoundaryTag.class) ;
			btag.removeFeatureWithPrefix(name) ;
			if(begin) {
				btag.addFeature(name + ":B") ;
				begin = false ;
			} else {
				btag.addFeature(name + ":I") ;
			}
			MutableAttributeSet attrs = context.createAttributeSet(token) ;
			highlighters.hightlight(context, token, attrs) ;
			int startEle = ele.getStartOffset() ;
			int length = ele.getEndOffset() - startEle ;
			setCharacterAttributes(startEle, length, attrs, true) ;
 		}
	}
	
	public void clearEntityTag(int start, int end) {
		if(start == end) {
			Element ele = getCharacterElement(start) ;
			start = ele.getStartOffset() ;
			end   = ele.getEndOffset() ;
		}
		List<Element> elements = 
			new ElementSelector.RangeElementSelector(start, end).select(this) ;
		SimpleAttributeSet attrs = new SimpleAttributeSet() ;
		StyleConstants.setForeground(attrs, WTagContext.TEXT_COLOR) ;
		for(int i = 0; i < elements.size(); i++) {
			Element ele = elements.get(i) ;
			IToken token = (Token)ele.getAttributes().getAttribute("token") ;
			if(token == null) continue ;
			WTagBoundaryTag btag = token.getFirstTagType(WTagBoundaryTag.class) ;
			btag.clear() ;
			int eleStart = ele.getStartOffset() ;
			int eleLength = ele.getEndOffset() - eleStart ;
			setCharacterAttributes(eleStart, eleLength, attrs, false) ;
		}
	}
	
	public void highlight() {
		List<Element> elements = new ElementSelector.TokenElementSelector().select(this) ;
		for(int i = 0; i < elements.size(); i++) {
			Element ele = elements.get(i) ;
			IToken token = (Token)ele.getAttributes().getAttribute("token") ;
			if(token == null) continue ;
			MutableAttributeSet attrs = context.createAttributeSet(token) ;
			StyleConstants.setForeground(attrs, WTagContext.TEXT_COLOR) ;
			StyleConstants.setBackground(attrs, WTagContext.BG_COLOR) ;
			highlighters.hightlight(context, token, attrs) ;
			int startEle = ele.getStartOffset() ;
			int length = ele.getEndOffset() - startEle ;
			setCharacterAttributes(startEle, length, attrs, true) ;
 		}
	}
	
	public void read(String text, int pos) throws IOException, BadLocationException {
  	try {
  		text = text.trim() ;
  		TokenCollection[] collections = ResourceFactory.getWTagDocumentReader().read(text) ;
  		read(collections) ;
  	} catch(Exception ex) {
  		ex.printStackTrace() ;
  	}
  }
	
	public void read(IToken[] token) throws BadLocationException {
		for(int i = 0; i < token.length; i++) {
			String oform = token[i].getOriginalForm() ;
			if(oform.length() == 1 && CharacterSet.isIn(oform.charAt(0), CharacterSet.NEW_LINE)) {
				MutableAttributeSet style = context.createAttributeSet(token[i]) ;
				insertString(getLength(), "\n", style) ;
			} else {
				MutableAttributeSet style = context.createAttributeSet(token[i]) ;
				highlighters.hightlight(context, token[i], style) ;
				insertString(getLength(), oform, style) ;
				if(i + 1 < token.length) insertString(getLength(), " ", null) ;
			}
		}
	}
	
	public void read(TokenCollection[] collections) throws BadLocationException {
		for(TokenCollection selCollection : collections) {
			for(IToken selUnit : selCollection.getTokens()) {
				MutableAttributeSet style = context.createAttributeSet(selUnit) ;
				highlighters.hightlight(context, selUnit, style) ;
				insertString(getLength(), selUnit.getOriginalForm(), style) ;
				insertString(getLength(), " ", null) ;
			}
			MutableAttributeSet style = context.createAttributeSet(new Token("\n")) ;
			insertString(getLength(), "\n", style) ;
			insertString(getLength(), "\n", style) ;
		}
		MutableAttributeSet style = context.createAttributeSet(new Token("\n")) ;
		insertString(getLength(), "\n", style) ;
		insertString(getLength(), "\n", style) ;
	}
	
	public TokenCollection[] updateTokens() throws Exception {
  	remove(0, getLength()) ;
  	TokenCollection[] collections = getTokenCollection() ;
  	read(collections) ;
  	return collections ;
	}
	
	public IToken[] getITokens() throws BadLocationException {
		List<IToken> holder = new ArrayList<IToken>() ;
		List<Element> elements = new ElementSelector.LeafElementSelector().select(this) ;
		for(int i = 0; i < elements.size(); i++) {
			Element ele = elements.get(i) ;
			int start = ele.getStartOffset() ;
			int length = ele.getEndOffset() - start ;
			if(length == 0) continue ;
			
			String etext = getText(start, length) ;
			if(etext.equals(" ")) continue ;
			
			IToken token = (IToken)ele.getAttributes().getAttribute("token") ;
			if(token == null) {
				holder.add(new Token(etext)) ;
			  continue ;
			}
			if(etext.length() == 1) {
				if(etext.charAt(0) == '\n') {
					holder.add(new Token("\n")) ;
					continue ;
				}
			}
			
			if(!etext.equals(token.getOriginalForm())) {
				token.reset(etext) ;
			}
			holder.add(token) ;
		}
		return holder.toArray(new IToken[holder.size()]) ;
	}
	
	public TokenCollection[] getTokenCollection() throws Exception {
		IToken[] token = getITokens() ;
		List<TokenCollection> collections = new ArrayList<TokenCollection>() ;
		for(TokenCollection line : LineAnalyzer.INSTANCE.analyze(token)) collections.add(line) ;
		return collections.toArray(new TokenCollection[collections.size()]) ;
	}
}