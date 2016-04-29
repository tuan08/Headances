package org.headvances.search.analysis;

import java.io.IOException;
import java.io.Reader;

import org.apache.lucene.analysis.Tokenizer;
import org.apache.lucene.analysis.tokenattributes.CharTermAttribute;
import org.headvances.util.IOUtil;
import org.headvances.util.html.URLNormalizer;

/**
 * $Author: Tuan Nguyen$ 
 **/
public class URLTokenizer extends Tokenizer {
	private CharTermAttribute termAtt = addAttribute(CharTermAttribute.class) ;
	private String[] token ;
	private int pos = 0 ;

	public URLTokenizer(Reader in) {
		super(in);
		init(in) ;
	}
	
	private void init(Reader in) {
		pos = 0 ;
		try {
			char[] data = IOUtil.getCharacters(in) ;
			String url = new String(data) ;
			URLNormalizer urlnorm = new URLNormalizer(url) ;
			this.token = urlnorm.getSources() ;
		} catch (Exception e) {
			this.token = new String[0] ;
			e.printStackTrace();
		}
	}
	
	public final boolean incrementToken() throws IOException {
		if(pos >= token.length) return false ;
		clearAttributes();
		String term = token[pos++] ;
		termAtt.setEmpty() ;
		termAtt.append(term) ;
		termAtt.setLength(term.length());
		return true;
	}

	public void reset(Reader input) throws IOException {
		super.reset(input) ;
		init(input) ;
	}

	public void reset() throws IOException { super.reset() ; }

	public void close() throws IOException { super.close() ; }
}