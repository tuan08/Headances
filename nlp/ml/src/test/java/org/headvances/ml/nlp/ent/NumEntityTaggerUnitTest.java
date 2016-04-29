package org.headvances.ml.nlp.ent;

import java.io.PrintStream;

import org.headvances.ml.nlp.ws.CRFWSTokenAnalyzer;
import org.headvances.ml.nlp.ws.OpenNLPWSTokenAnalyzer;
import org.headvances.nlp.dict.Dictionary;
import org.headvances.nlp.dict.DictionaryTaggingAnalyzer;
import org.headvances.nlp.token.IToken;
import org.headvances.nlp.token.TabularTokenPrinter;
import org.headvances.nlp.token.TextSegmenter;
import org.headvances.nlp.token.analyzer.CommonTokenAnalyzer;
import org.headvances.nlp.token.analyzer.PunctuationTokenAnalyzer;
import org.headvances.nlp.token.analyzer.TokenAnalyzer;
import org.headvances.util.ConsoleUtil;
import org.junit.Test;

public class NumEntityTaggerUnitTest {

  @Test
  public void test() throws Exception{
    String text = "Hội nghị diễn ra từ ngày 15/11/2011";
    String text1 = "1 triệu $ là giá bán của số điện thoại liên lạc 0979167957";
    String text2 = "80% dân số châu Phi có thu nhập dưới mức 1 đô la";
    String text3 = "150 người đã bị bắt giữ trong cuộc biểu tình diễn ra trong 3 ngày";
    
    test(text2);
    
    System.out.println();
    
//    String wtext = "Trong:{} 10:{qt:time:B} ngày:{qt:time:I} kể:{} từ:{time:B} 12.1:{time:I} " +
//    		"đến:{time:I} 20.1:{time:I} ,:{} đại hội:{} diễn ra:{} với:{} 90%:{percent:B} đại biểu:{} tham gia:{} (04)33722711:{phone:B}";
////    testWtag(wtext);
  }

  private void test(String text) throws Exception{
    Dictionary dict = new Dictionary(Dictionary.DICT_RES) ;
    TokenAnalyzer[] crfAnalyzer = new TokenAnalyzer[] {
        new PunctuationTokenAnalyzer(), new CommonTokenAnalyzer(), new CRFWSTokenAnalyzer(),
        new DictionaryTaggingAnalyzer(dict), new CRFNumEntityTokenAnalyzer()
    };
    
    TokenAnalyzer[] opennlpAnalyzer = new TokenAnalyzer[] {
        new PunctuationTokenAnalyzer(), new CommonTokenAnalyzer(), new OpenNLPWSTokenAnalyzer(),
        new DictionaryTaggingAnalyzer(dict), new OpenNLPEntityTokenAnalyzer()
    };

    PrintStream out = ConsoleUtil.getUTF8SuportOutput() ;
    TabularTokenPrinter printer = new TabularTokenPrinter();
    
    TextSegmenter textSegmenter = new TextSegmenter(crfAnalyzer) ;
    IToken[] token = textSegmenter.segment(text) ;
    printer.print(out, token) ;
    
    System.out.println("*****************************************************");
    
    textSegmenter = new TextSegmenter(opennlpAnalyzer) ;
    token = textSegmenter.segment(text) ;
    printer.print(out, token) ;
  }

//  private void testWtag(String wtext) throws Exception{
//    MLENT mlent = new MLENT("classpath:ml/nlp/entity.num.crf", "num", true);
//    mlent.testText(System.out, wtext, true);
//  }

}
