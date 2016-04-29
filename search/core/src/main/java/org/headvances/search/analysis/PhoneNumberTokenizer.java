package org.headvances.search.analysis;


import java.io.IOException;
import java.io.Reader;

import org.apache.lucene.analysis.Tokenizer;
import org.apache.lucene.analysis.tokenattributes.CharTermAttribute;
import org.apache.lucene.util.AttributeSource;
import org.headvances.util.IOUtil;

public final class PhoneNumberTokenizer extends Tokenizer {
  
	private CharTermAttribute termAtt = addAttribute(CharTermAttribute.class) ;
	private boolean read ;
	
	public PhoneNumberTokenizer(Reader in) {
    super(in);
  }

  public PhoneNumberTokenizer(AttributeSource source, Reader in) {
    super(source, in);
  }

  public final boolean incrementToken() throws IOException {
  	if(read) return false ;
    
  	clearAttributes();
    char[] chars = IOUtil.getCharacters(input) ;
    StringBuilder b = new StringBuilder() ;
    for(char sel : chars) {
    	if(Character.isDigit(sel)) b.append(sel) ;
    }
    String term = b.toString() ;
    termAtt.setEmpty() ;
    termAtt.append(term) ;
    termAtt.setLength(term.length());
    read = true ;
    return true;
  }
  
  public void reset(Reader input) throws IOException {
    super.reset(input) ;
  	read = false ;
  }
  
  public void reset() throws IOException {
  	super.reset() ;
  }
  
  public void close() throws IOException { 
  	super.close() ;
  }
}