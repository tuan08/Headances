package org.headvances.nlp.query2.match;

import org.headvances.nlp.dict.EntityDictionary;
import org.headvances.nlp.dict.SynsetDictionary;
import org.headvances.nlp.token.IToken;
import org.headvances.nlp.token.TokenCollection;
import org.headvances.nlp.token.TokenException;
import org.headvances.nlp.token.WordTokenizer;
import org.headvances.nlp.token.analyzer.CommonTokenAnalyzer;
import org.headvances.nlp.token.analyzer.DateTokenAnalyzer;
import org.headvances.nlp.token.analyzer.EmailTokenAnalyzer;
import org.headvances.nlp.token.analyzer.PunctuationTokenAnalyzer;
import org.headvances.nlp.token.analyzer.TimeTokenAnalyzer;
import org.headvances.nlp.token.analyzer.TokenAnalyzer;
import org.headvances.nlp.token.analyzer.USDTokenAnalyzer;
import org.headvances.nlp.token.analyzer.VNDTokenAnalyzer;
import org.headvances.nlp.token.analyzer.VNMobileTokenAnalyzer;
import org.headvances.nlp.token.analyzer.VNPhoneTokenAnalyzer;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
/**
 * $Author: Tuan Nguyen$ 
 **/
public class RuleMatcherUnitTest {
  MatcherResourceFactory umFactory ;

  @Before
  public void setup() throws Exception {
    SynsetDictionary dict = new SynsetDictionary() ;
    dict.add("lienhe", new String[]{}, new String[]{"liên hệ"}) ;
    dict.add("person", new String[]{}, new String[]{"chúng tôi", "tôi", "anh"}) ;
    EntityDictionary entityDict = new EntityDictionary(EntityDictionary.DICT_RES) ;
    umFactory = new MatcherResourceFactory(dict, entityDict) ;
  }

  @Test
  public void testConstruction() throws Exception {
    assertRule(
        "/p word{word=liên hệ} .2. word{word=chúng tôi, tôi, anh}",
        "Hãy liên hệ chúng tôi vào những ngày trong tuần", 
        true
        );
    assertRule(
        "/p word{word=liên hệ} .0. synset{name=person}",
        "Hãy liên hệ chúng tôi vào những ngày trong tuần", 
        true
        );
    assertRule(
        "/p synset{name=lienhe} .2. synset{name=person}",
        "Hãy liên hệ với tôi vào những ngày trong tuần", 
        true
        );

    assertRule(
        "/p synset{name=lienhe} .10. entity{type = place, street}",
        "Hãy liên hệ với tôi tai pho Trương Định", 
        true
        );

    assertRule(
        "/p word{word=so} .2. digit",
        "match con so digit 123", 
        true
        );

    assertRule(
        "/p word{word=tien te} .2. currency{unit=vnd}",
        "match tien te 123 ngan tien viet", 
        true
        );
  }

  private void assertRule(String rule, String text, boolean expectMatch) throws Exception {
    RuleMatcher ruleMatcher = new RuleMatcher(umFactory, rule) ;
    TokenCollection tokenSet = createTokenSet(text) ;
    RuleMatch ruleMatch = ruleMatcher.matches(tokenSet) ;
    System.out.println("Text: " + text);
    System.out.println("  Rule: " + rule);
    if(ruleMatch != null) {
      System.out.println("  Match: " + ruleMatch);
    }
    if(expectMatch) {
      Assert.assertNotNull(ruleMatch) ;
    } else {
      Assert.assertNull(ruleMatch) ;
    }
  }

  private TokenCollection createTokenSet(String text) throws TokenException {
    TokenAnalyzer[] analyzer = {
        PunctuationTokenAnalyzer.INSTANCE, new CommonTokenAnalyzer(), 
        new DateTokenAnalyzer(), new TimeTokenAnalyzer(), 
        new VNDTokenAnalyzer(), new USDTokenAnalyzer(),
        new VNPhoneTokenAnalyzer(), new VNMobileTokenAnalyzer(),
        new EmailTokenAnalyzer()	
    };
    IToken[] tokens = new WordTokenizer(text).allTokens() ;
    TokenCollection tokenSet = new TokenCollection(tokens) ;
    tokenSet.analyze(analyzer) ;
    return  tokenSet ;
  }
}