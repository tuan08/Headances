package org.headvances.ml.nlp.ws;

import org.headvances.ml.nlp.ws.OpenNLPWSTokenAnalyzer;
import org.headvances.nlp.NLPResource;
import org.headvances.nlp.dict.Dictionary;
import org.headvances.nlp.dict.DictionaryTaggingAnalyzer;
import org.headvances.nlp.token.IToken;
import org.headvances.nlp.token.TextSegmenter;
import org.headvances.nlp.token.analyzer.CommonTokenAnalyzer;
import org.headvances.nlp.token.analyzer.PunctuationTokenAnalyzer;
import org.headvances.nlp.token.analyzer.TokenAnalyzer;
import org.headvances.nlp.ws.NGramStatisticWSTokenAnalyzer;
import org.headvances.nlp.ws.WordTreeMatchingAnalyzer;
import org.junit.Test;

public class PerformanceTest {
  @Test
  public void matching() throws Exception {    
    TokenAnalyzer[] statWSAnalyzer = new TokenAnalyzer[] {
      new PunctuationTokenAnalyzer(), new CommonTokenAnalyzer(),
      new NGramStatisticWSTokenAnalyzer().setDebug(false)
    };
    TextSegmenter statWS = new TextSegmenter(statWSAnalyzer);
    
    TokenAnalyzer[] dictWSAnalyzer = new TokenAnalyzer[] {
        new PunctuationTokenAnalyzer(), new CommonTokenAnalyzer(),
        new WordTreeMatchingAnalyzer()
    };
    TextSegmenter dictWS = new TextSegmenter(dictWSAnalyzer);
      
    Dictionary dict = NLPResource.getInstance().getDictionary(Dictionary.DICT_RES);
    TokenAnalyzer[] maxentWSAnalyzer = new TokenAnalyzer[] {
      new PunctuationTokenAnalyzer(), 
      new CommonTokenAnalyzer(), 
      new OpenNLPWSTokenAnalyzer(),
      new DictionaryTaggingAnalyzer(dict)
    };
    TextSegmenter maxentWS = new TextSegmenter(maxentWSAnalyzer);
    
    run(statWS,   100, "Statistic Word Segmenter") ;
    run(dictWS,   100, "Dictionary Word Segmenter") ;
    run(maxentWS, 100, "MaxEnt Word Segmenter") ;
    System.out.println("------------------------------------------------") ;
    run(statWS,   100000, "Statistic Word Segmenter") ;
    run(dictWS,   100000, "Dictionary Word Segmenter") ;
    run(maxentWS, 100000, "MaxEnt Word Segmenter") ;
  }
  
  private void run(TextSegmenter textSegmenter, int loop, String test) throws Exception {    
  	System.gc() ;
  	System.out.println("#" + test);
  	String text = "Học sinh học sinh học từ dưới phổ thông cơ sở và được tuyên truyền thông tin về nhà nước xã hội chủ nghĩa";
  	long start = System.currentTimeMillis() ;
  	for(int i = 0; i < loop; i++) {
  		IToken[] token = textSegmenter.segment(text);
  	}

  	IToken[] token = textSegmenter.segment(text);
  	for (int i = 0; i < token.length; i++) {
  		if(i > 0) System.out.append(" .. ");
  		System.out.append(token[i].getOriginalForm());
  	}
  	System.out.println();
  	System.out.println("Run " + loop + " in " + (System.currentTimeMillis() - start) + "ms");
  }
}
