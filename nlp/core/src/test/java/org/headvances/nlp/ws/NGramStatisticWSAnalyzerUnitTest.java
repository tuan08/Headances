
package org.headvances.nlp.ws;

import org.headvances.nlp.token.IToken;
import org.headvances.nlp.token.TextSegmenter;
import org.headvances.nlp.token.analyzer.CommonTokenAnalyzer;
import org.headvances.nlp.token.analyzer.PunctuationTokenAnalyzer;
import org.headvances.nlp.token.analyzer.TokenAnalyzer;
import org.headvances.nlp.ws.NGramStatisticWSTokenAnalyzer;
import org.headvances.util.JVMInfoUtil;
import org.junit.Test;

public class NGramStatisticWSAnalyzerUnitTest {
  @Test
  public void matching() throws Exception {    
  	System.gc() ;
    System.out.println(JVMInfoUtil.getMemoryUsageInfo()) ;
    String text = "Chúng tôi cần bán iphone giá 5 triệu từ ngày 1/3/2012";
    TokenAnalyzer[] Analyzers = new TokenAnalyzer[] {
      new PunctuationTokenAnalyzer(), new CommonTokenAnalyzer(),
      new NGramStatisticWSTokenAnalyzer().setDebug(true)
    };
    TextSegmenter textSegmenter = new TextSegmenter(Analyzers);
    IToken[] token = textSegmenter.segment(text);
    
    System.out.println("Segement: ");
    for (int i = 0; i < token.length; i++) {
    	if(i > 0) System.out.append(" .. ");
      System.out.append(token[i].getOriginalForm());
    }
    System.out.println("\n\n");
    
    System.gc() ;
    System.out.println(JVMInfoUtil.getMemoryUsageInfo()) ;
    Analyzers = null ;
    textSegmenter = null ;
    System.gc() ;
    System.out.println(JVMInfoUtil.getMemoryUsageInfo()) ;
  }
}
