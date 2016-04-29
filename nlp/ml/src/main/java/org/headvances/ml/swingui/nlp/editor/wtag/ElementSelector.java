package org.headvances.ml.swingui.nlp.editor.wtag;

import java.util.ArrayList;
import java.util.List;

import javax.swing.text.AttributeSet;
import javax.swing.text.Element;

import org.headvances.nlp.token.IToken;

abstract public class ElementSelector {
	
	public List<Element> select(WTagDocument doc) {
		List<Element> holder = new ArrayList<Element>() ;
		traverse(holder, doc.getDefaultRootElement()) ;
		return holder ;
	}
	 
	protected void traverse(List<Element> holder, Element ele)  {
		if(ele.isLeaf()) {
			if(isSelect(ele)) holder.add(ele) ; 
			return  ;
		}
		for(int i = 0; i < ele.getElementCount(); i++) {
			traverse(holder, ele.getElement(i)) ;
		}
	}
	
	abstract protected boolean isSelect(Element ele) ;

	static public class LeafElementSelector extends ElementSelector {
    protected boolean isSelect(Element ele) {
			return true ;
    }
	}
	
	static public class TokenElementSelector extends ElementSelector {
    protected boolean isSelect(Element ele) {
    	AttributeSet attrs = ele.getAttributes(); 
			IToken token = (IToken)attrs.getAttribute("token") ;
			return token != null ;
    }
    
    public IToken[] selectToken(WTagDocument doc) {
    	List<Element> elements = select(doc) ;
    	IToken[] token = new IToken[elements.size()] ;
    	for(int i = 0; i < elements.size(); i++) {
    		token[i] = (IToken) elements.get(i).getAttributes().getAttribute("token") ;
    	}
    	return token ;
    }
	}
	
	static public class RangeElementSelector extends ElementSelector {
    private int start , end  ;
    
    public RangeElementSelector(int start, int end) {
    	this.start = start ;
    	this.end = end ;
    }
    
		protected boolean isSelect(Element ele) {
			int offset = ele.getStartOffset() ;
			if(offset >= start && offset < end) return true ;
			return false;
    }
	}
}