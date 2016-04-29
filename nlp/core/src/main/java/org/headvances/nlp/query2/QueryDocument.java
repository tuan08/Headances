package org.headvances.nlp.query2;

import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

import org.headvances.nlp.token.IToken;
import org.headvances.nlp.token.TokenCollection;
import org.headvances.nlp.token.TokenException;
import org.headvances.nlp.token.WordTokenizer;
import org.headvances.nlp.token.analyzer.PunctuationTokenAnalyzer;
import org.headvances.nlp.token.analyzer.TokenAnalyzer;

public class QueryDocument {
	private Map<String, TokenCollection> fields ;
	
	public QueryDocument() {
		fields = new HashMap<String, TokenCollection>() ;
	}
	
	public void add(String name, String data) throws TokenException {
		IToken[] tokens = new WordTokenizer(data).allTokens() ;
		tokens = PunctuationTokenAnalyzer.INSTANCE.analyze(tokens) ;
		fields.put(name, new TokenCollection(tokens) ) ;
	}
	
	public void add(String name, String data, TokenAnalyzer[] analyzer) throws TokenException {
		IToken[] tokens = new WordTokenizer(data).allTokens() ;
		TokenCollection collection = new TokenCollection(tokens) ;
		collection.analyze(analyzer) ;
		fields.put(name, collection) ;
	}
	
	public void add(String name, IToken[] token) {
		fields.put(name, new TokenCollection(token)) ;
	}
	
	public void analyze(TokenAnalyzer[] analyzer) throws TokenException {
		Iterator<TokenCollection> itr = fields.values().iterator() ;
		while(itr.hasNext()) {
			TokenCollection collection = itr.next() ;
			collection.analyze(analyzer) ;
		}
	}
	
	public TokenCollection getDocumentField(String name) {
		return fields.get(name) ;
	}
	
	public Map<String, TokenCollection> getDocumentFields() { return this.fields ; }
}