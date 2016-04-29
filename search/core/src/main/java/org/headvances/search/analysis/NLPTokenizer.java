package org.headvances.search.analysis;

import org.headvances.nlp.token.IToken;
import org.headvances.nlp.token.TokenCollection;
import org.headvances.nlp.token.TokenException;
import org.headvances.nlp.token.WordTokenizer;
import org.headvances.nlp.token.analyzer.CommonTokenAnalyzer;
import org.headvances.nlp.token.analyzer.GroupTokenMergerAnalyzer;
import org.headvances.nlp.token.analyzer.PunctuationTokenAnalyzer;
import org.headvances.nlp.token.analyzer.SentenceSplitterAnalyzer;
import org.headvances.nlp.token.analyzer.TokenAnalyzer;
import org.headvances.nlp.token.analyzer.TokenCollectionAnalyzer;
import org.headvances.nlp.ws.NGramStatisticWSTokenAnalyzer;
/**
 * $Author: Tuan Nguyen$ 
 **/
public class NLPTokenizer {
	private TokenCollectionAnalyzer sentenceSpliter = SentenceSplitterAnalyzer.INSTANCE ;

	private TokenAnalyzer[] analyzer ;
	
	public NLPTokenizer() throws Exception {
		analyzer = new TokenAnalyzer[] {
			PunctuationTokenAnalyzer.INSTANCE, new CommonTokenAnalyzer(), new GroupTokenMergerAnalyzer(),
			new NGramStatisticWSTokenAnalyzer()
		} ;
	}
	
	public IToken[] tokenize(String text) throws TokenException {
		WordTokenizer tokenizer = new WordTokenizer(text) ;
		IToken[] token = tokenizer.allTokens() ;
		for(TokenAnalyzer sel : analyzer) token = sel.analyze(token) ;
		return token ;
	}

	public TokenCollection[] splitSentence(String text) throws TokenException {
		IToken[] token = tokenize(text) ;
		return sentenceSpliter.analyze(token) ;
	}
}