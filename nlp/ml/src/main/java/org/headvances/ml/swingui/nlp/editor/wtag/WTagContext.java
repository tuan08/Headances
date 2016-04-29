package org.headvances.ml.swingui.nlp.editor.wtag ;

import java.awt.Color;

import javax.swing.text.MutableAttributeSet;
import javax.swing.text.SimpleAttributeSet;
import javax.swing.text.StyleConstants;
import javax.swing.text.StyleContext;

import org.headvances.nlp.token.IToken;

public class WTagContext extends StyleContext  {
	final static public Color ENT_NP_COLOR    = Color.RED ;
	final static public Color ENT_QT_COLOR    = Color.MAGENTA ;
	final static public Color ENT_OTHER_COLOR = new Color(127, 0, 255) ; //viollet color
	
	final static public Color TEXT_COLOR    = Color.BLACK ;
	final static public Color BG_COLOR      = Color.WHITE;
	final static public Color HL_BG_COLOR   = new Color(215, 235, 255);;
	
  public WTagContext() {
  }
  
  public MutableAttributeSet createAttributeSet(IToken token) {
  	SimpleAttributeSet set = new SimpleAttributeSet() ;
		set.addAttribute("token", token) ;
		StyleConstants.setUnderline(set, true) ;
  	return set ;
  }
}