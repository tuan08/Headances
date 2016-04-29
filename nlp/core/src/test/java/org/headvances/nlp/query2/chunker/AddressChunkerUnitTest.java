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

public class AddressChunkerUnitTest {
	private WordTokenizerVerifier wsverifier ;

	@Before
	public void setup() throws Exception {
		Dictionary dict = NLPResource.getInstance().getDictionary(Dictionary.DICT_RES) ;
		TokenAnalyzer[] wsanalyzer = {
			PunctuationTokenAnalyzer.INSTANCE, new CommonTokenAnalyzer(), 
			new WordTreeMatchingAnalyzer(dict),
			new AddressChunker(new MatcherResourceFactory()),
		};
		wsverifier = new WordTokenizerVerifier(wsanalyzer) ;
	}
	
	//@Test
	public void testCase() throws Exception {
		verify(
			"186 Trương Định, Phường Trương Định, Quận Hai Bà Trưng, Hà Nội", 
			"186 Trương Định , Phường Trương Định , Quận Hai Bà Trưng , Hà Nội{loc}") ;
	}
	
	@Test
	public void test() throws Exception {
		verify(
			"186 Trương Định, Hà Nội", 
			"186 Trương Định , Hà Nội{loc}") ;
		verify(
			"186 Trương Định, Phường Trương Định, Quận Hai Bà Trưng, Hà Nội", 
			"186 Trương Định , Phường Trương Định , Quận Hai Bà Trưng , Hà Nội{loc}") ;
		verify(
			"186 Trương Định, Phường Trương Định, Hà Nội", 
			"186 Trương Định , Phường Trương Định , Hà Nội{loc}") ;
		verify(
			"186 Trương Định, Quận Hai Bà Trưng, Hà Nội", 
			"186 Trương Định , Quận Hai Bà Trưng , Hà Nội{loc}") ;
		verify(
			"186 Trương Định, Quận Hai Bà Trưng", 
			"186 Trương Định , Quận Hai Bà Trưng{loc}") ;
		
		verify("186 Trương Định", "186 Trương Định{loc}") ;
		verify("23/30 Trương Định", "23/30 Trương Định{loc}") ;
		verify("23n/30/100 Trương Định", "23n/30/100 Trương Định{loc}") ;
	}
	
	private void verify(String text, String ... expect) throws Exception {
		wsverifier.verify(text, expect) ;
	}
}