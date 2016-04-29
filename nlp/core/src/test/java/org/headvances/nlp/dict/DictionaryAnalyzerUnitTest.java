package org.headvances.nlp.dict;

import org.headvances.nlp.token.WordTokenizerVerifier;
import org.headvances.nlp.token.analyzer.CommonTokenAnalyzer;
import org.headvances.nlp.token.analyzer.GroupTokenMergerAnalyzer;
import org.headvances.nlp.token.analyzer.PunctuationTokenAnalyzer;
import org.headvances.nlp.token.analyzer.TokenAnalyzer;
import org.headvances.nlp.ws.WordTreeMatchingAnalyzer;
import org.junit.Test;

/**
 * $Author: Tuan Nguyen$ 
 **/
public class DictionaryAnalyzerUnitTest {
  @Test
  public void testLongestMatchingAnalyzer() throws Exception {
    String[] dictRes = {
        "classpath:nlp/vn.lexicon.json",
        "classpath:nlp/lienhe.synset.json",
        "classpath:nlp/entity/vn.place.json",
        "classpath:nlp/mobile.product.json"
    };
    Dictionary dict = new Dictionary(dictRes) ;
    TokenAnalyzer[] analyzer = {
        new CommonTokenAnalyzer(), new PunctuationTokenAnalyzer(), 
        new GroupTokenMergerAnalyzer(), new WordTreeMatchingAnalyzer(dict)
    } ;
    WordTokenizerVerifier verifier = new WordTokenizerVerifier(analyzer) ;
    verifier.verify(
        "Một quả bom sắp nổ.", 
        "Một", "quả bom{postag, synset}", "sắp", "nổ", ".") ;
    verifier.verify(
        "186 trương định", 
        "186", "trương định{entity}") ;
    verifier.verify(
        "mua iphone", 
        "mua", "iphone{product}") ;
  }

  @Test
  public void testDictionaryTaggingAnalyzer() {

  }
}
