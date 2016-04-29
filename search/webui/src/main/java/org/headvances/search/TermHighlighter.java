package org.headvances.search;

import org.headvances.util.text.StringUtil;

public class TermHighlighter extends TokenHighlighter {
  private String[] terms ;
  
  public TermHighlighter(String[] highlightWords) {
    super(highlightWords);
    this.terms = highlightWords ;
  }
  
  public TermHighlighter(String words) {
  	this(StringUtil.toStringArray(words, " "));
  }

  public String[] getTerms() { return this.terms ; }
  
  public void beforeRender(StringBuilder b, String match) {
    b.append("<strong>") ;
  }
  
  public void afterRender(StringBuilder b, String match) {
    b.append("</strong>") ;
  }
  
  public String highlight(String text) {
    String[] array = extractBestMatch(text, text.length()) ;
    StringBuilder b = new StringBuilder() ;
    for(int i = 0; i < array.length; i++) {
      b.append(array[i]) ;
    }
    return b.toString() ;
  }
  
  public String extract(String desc, String content, int limit, int length) {
  	if(terms == null || terms.length == 0) return desc ;
  	
    String text = desc + "\n" + content ;
    String[] array = extractBestMatch(text, limit) ;
    if(array == null || array.length == 0) {
      return desc ;
    } else {
      StringBuilder b = new StringBuilder() ;
      for(int i = 0; i < array.length; i++) {
        if(i > 0) {
          if(b.length() + array[i].length() > length) break ;
          b.append(" ... ") ;
        }
        b.append(array[i]) ;
      }
      return b.toString() ;
    }
  }
}