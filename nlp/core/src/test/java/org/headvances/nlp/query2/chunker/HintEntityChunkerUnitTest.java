package org.headvances.nlp.query2.chunker;

import org.headvances.nlp.NLPResource;
import org.headvances.nlp.dict.Dictionary;
import org.headvances.nlp.query2.match.MatcherResourceFactory;
import org.headvances.nlp.token.WordTokenizerVerifier;
import org.headvances.nlp.token.analyzer.CommonTokenAnalyzer;
import org.headvances.nlp.token.analyzer.PunctuationTokenAnalyzer;
import org.headvances.nlp.token.analyzer.TokenAnalyzer;
import org.headvances.nlp.ws.WordTreeMatchingAnalyzer;
import org.junit.Before;
import org.junit.Test;

public class HintEntityChunkerUnitTest {
	private WordTokenizerVerifier wsverifier ;

	@Before
	public void setup() throws Exception {
		Dictionary dict = NLPResource.getInstance().getDictionary(Dictionary.DICT_RES) ;
		TokenAnalyzer[] wsanalyzer = {
			PunctuationTokenAnalyzer.INSTANCE, new CommonTokenAnalyzer(), 
			new WordTreeMatchingAnalyzer(dict),
			new HintEntityChunker(new MatcherResourceFactory()),
		};
		wsverifier = new WordTokenizerVerifier(wsanalyzer) ;
	}
	
	@Test
	public void testCase() throws Exception {
		verify(
			"ông Trần Hưng Đạo", 
			"ông", "Trần Hưng Đạo{person}") ;
	
		verify(
			"phố Trần Hưng Đạo", 
			"phố", "Trần Hưng Đạo{location}") ;
	}
	
	private void verify(String text, String ... expect) throws Exception {
		wsverifier.verify(text, expect) ;
	}
}