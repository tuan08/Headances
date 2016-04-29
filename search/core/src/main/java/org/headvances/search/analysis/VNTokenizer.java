package org.headvances.search.analysis;

import java.io.IOException;
import java.io.Reader;

import org.apache.lucene.analysis.Tokenizer;
import org.apache.lucene.analysis.tokenattributes.CharTermAttribute;
import org.headvances.nlp.token.IToken;
import org.headvances.util.IOUtil;

/**
 * $Author: Tuan Nguyen$ 
 **/
public class VNTokenizer extends Tokenizer {
	private NLPTokenizer nlpTokenizer ;
	private CharTermAttribute termAtt = addAttribute(CharTermAttribute.class) ;
	private IToken[] token ;
	private int pos = 0 ;

	public VNTokenizer(Reader in, NLPTokenizer nlpTokenizer) {
		super(in);
		this.nlpTokenizer = nlpTokenizer;
		init(in) ;
	}
	
	private void init(Reader in) {
		pos = 0 ;
		try {
			char[] data = IOUtil.getCharacters(in) ;
			this.token = nlpTokenizer.tokenize(new String(data)) ;
		} catch (Exception e) {
			this.token = new IToken[] {} ;
			e.printStackTrace();
		}
	}
	
	public final boolean incrementToken() throws IOException {
		if(pos >= token.length) return false ;
		clearAttributes();
		String term = token[pos++].getNormalizeForm() ;
		term = normalize(term) ;
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

	final static public String normalize(String token) {
		StringBuilder b = new StringBuilder() ;
		char[] buf = token.toCharArray() ;
		for(int j = 0; j < buf.length; j++) {
			if(buf[j] == ' ' || buf[j] == ':' || buf[j] == '=' || buf[j] == '-') b.append('.') ;
			else b.append(buf[j]) ;
		}
		return b.toString() ;
	}
}