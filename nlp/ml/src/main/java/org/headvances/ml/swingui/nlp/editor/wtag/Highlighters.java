package org.headvances.ml.swingui.nlp.editor.wtag;

import java.awt.Color;
import java.util.HashMap;
import java.util.Map;

import javax.swing.text.MutableAttributeSet;
import javax.swing.text.StyleConstants;

import org.headvances.ml.nlp.SuggestTag;
import org.headvances.nlp.token.IToken;
import org.headvances.nlp.wtag.WTagBoundaryTag;
import org.headvances.util.text.StringUtil;

public class Highlighters {
	private Map<String, Highlighter> highlighters = new HashMap<String, Highlighter>() ;
	
	public void add(String name, Highlighter highlighter) {
		highlighters.put(name, highlighter) ;
	}
	
	public void remove(String name) {
		highlighters.remove(name) ;
	}
	
	public void hightlight(WTagContext context, IToken token, MutableAttributeSet attrs) {
		Highlighter[] highlighter = highlighters.values().toArray(new Highlighter[highlighters.size()]) ;
		StyleConstants.setForeground(attrs, WTagContext.TEXT_COLOR) ;
		StyleConstants.setBackground(attrs, WTagContext.BG_COLOR) ;
		for(int j = 0; j < highlighter.length; j++) {
		  highlighter[j].hightlight(context, token, attrs) ;
		}
	}
	
	static public interface Highlighter {
		public void hightlight(WTagContext context, IToken token, MutableAttributeSet attrs) ;
	}
	
	static public class EntityHighlighter implements Highlighter {
		private Config[] config = {
		  new Config("loc:",   WTagContext.ENT_NP_COLOR),
		  new Config("org:",   WTagContext.ENT_NP_COLOR),
		  new Config("per:",   WTagContext.ENT_NP_COLOR),
		  new Config("evt:",   WTagContext.ENT_NP_COLOR),
		  new Config("time:",  WTagContext.ENT_OTHER_COLOR),
		  new Config("phone:", WTagContext.ENT_OTHER_COLOR),
		  
		  new Config("qt:",    WTagContext.ENT_QT_COLOR),
		  
		  new Config("qtag:type", new Color(90, 0, 255)),
		  new Config("qtag:obj", new Color(130, 50, 255)),
		  new Config("qtag:attr", new Color(180, 0, 20)),
		  new Config("qtag:other", Color.BLACK)
		} ;
		
		public void hightlight(WTagContext context, IToken token, MutableAttributeSet attrs) {
			WTagBoundaryTag btag = token.getFirstTagType(WTagBoundaryTag.class) ;
			if(btag == null) return ;
			String[] tag = btag.getFeatures() ;
			if(tag == null || tag.length == 0) return ;
			for(String selTag : tag) {
				for(Config selConfig : config) {
					if(selTag.startsWith(selConfig.prefix)) {
						StyleConstants.setForeground(attrs, selConfig.color) ;
						return ;
					}
				}
			}
			StyleConstants.setForeground(attrs, WTagContext.ENT_OTHER_COLOR) ;
		}
		
		static class Config {
			String prefix ;
			Color color ;
			
			Config(String prefix, Color color) {
				this.prefix = prefix ;
				this.color = color ;
			}
		}
	}
	
	static public class SuggestWordHighlighter implements Highlighter {
		static String[] SUGGEST_WORD = {
			"một", "hai", "ba", "bốn", "năm", "sáu", "bẩy", "tám", "chín", "mười",
			"chục", "trăm", "ngàn", "triệu", "tỷ"
		} ;
		public void hightlight(WTagContext context, IToken token, MutableAttributeSet attrs) {
			if(Character.isUpperCase(token.getOriginalForm().charAt(0))) {
				StyleConstants.setBackground(attrs, WTagContext.HL_BG_COLOR) ;
				return ;
			}
			for(String sel : token.getWord()) {
				if(StringUtil.isIn(sel, SUGGEST_WORD)) {
					StyleConstants.setBackground(attrs, WTagContext.HL_BG_COLOR) ;
					return ;
				}
				for(char selLetter : sel.toCharArray()) {
					if((selLetter >= '0' && selLetter <= '9') ||
							selLetter == '%' || selLetter == '/') {
						StyleConstants.setBackground(attrs, WTagContext.HL_BG_COLOR) ;
						return ;
					}
				}
			}
		}
	}
	
	static public class MarkTagHighlighter implements Highlighter {
		public void hightlight(WTagContext context, IToken token, MutableAttributeSet attrs) {
			SuggestTag tag = token.getFirstTagType(SuggestTag.class) ;
			if(tag != null) {
				StyleConstants.setBackground(attrs, WTagContext.HL_BG_COLOR) ;
			}
		}
	}
}
