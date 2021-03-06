package org.headvances.html.dom.selector;

import org.headvances.html.dom.TNode;
import org.headvances.util.text.StringMatcher;

/**
 * $Author: Tuan Nguyen$ 
 **/
public class CssClassSelector implements Selector {
  private StringMatcher[] matcher ;

  public CssClassSelector(String exp) {
    this.matcher = new StringMatcher[] { new StringMatcher(exp.toLowerCase()) } ;
  }

  public CssClassSelector(String[] exp) {
    this.matcher = new StringMatcher[exp.length] ;
    for(int i = 0; i < matcher.length; i++) {
      matcher[i] = new StringMatcher(exp[i].toLowerCase()) ;
    }
  }

  public boolean isSelected(TNode node) {
    String css = node.getCssClass() ;
    if(css == null) return false ;
    for(StringMatcher sel : matcher) {
      if(sel.matches(css)) return true ;
    }
    return false ;
  }
}