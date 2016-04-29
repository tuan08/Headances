package org.headvances.nlp.token;

import org.headvances.nlp.token.analyzer.TokenAnalyzer;
import org.junit.Test;
/**
 * $Author: Tuan Nguyen$ 
 **/
public class WordTokenizerUnitTest {
  @Test
  public void testWordTokenizer() throws Exception {
    TokenAnalyzer[] analyzer = {} ;
    WordTokenizerVerifier verifier = new WordTokenizerVerifier(analyzer) ;
    verifier.verify(
        "this is a test", 
        "this", "is", "a", "test") ;

    verifier.verify(
        "this 'is'a test",
        "this", "'", "is",  "'", "a",  "test") ;

    verifier.verify(
        "a>b a > b", 
        "a", ">", "b", "a", ">", "b") ;
    verifier.verify(
        "Mr. Test 1 2 3 4.", 
        "Mr.", "Test", "1", "2", "3", "4."
        ) ;
  }
}