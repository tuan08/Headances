package org.headvances.search.analysis ;

import java.io.IOException;
import java.util.regex.Pattern;

import org.apache.lucene.analysis.TokenStream;
import org.apache.lucene.analysis.tokenattributes.CharTermAttribute;
/**
 * Emits the entire input as a single token.
 */
public class WordTokenStream extends TokenStream {
  static Pattern SPLITTER = Pattern.compile("\\.\\.") ;
  
  private CharTermAttribute termAtt;
  private String[] token ;
  private int pos = 0 ;
  
  public WordTokenStream(String token) {
    this.token = new String[] { token } ;
    termAtt = addAttribute(CharTermAttribute.class);
  }
  
  public WordTokenStream(String[] token) {
    this.token =  token  ;
    termAtt = addAttribute(CharTermAttribute.class);
  }
  
  public final boolean incrementToken() throws IOException {
    clearAttributes();
    if(pos == token.length) return false ;
    String term = token[pos++] ;
    termAtt.setEmpty() ;
    termAtt.append(term) ;
    termAtt.setLength(term.length());
    return true;
  }
  
  public void reset() throws IOException {
    pos = 0 ;
  }
  
  public void close() throws IOException { }
}