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

public class NPEntityTaggerUnitTest {

  @Test
  public void test() throws Exception{
    String text  = "Giáo sư Cù Trọng Xoay cho rằng: \"Buồn thì có buồn nhưng chưa được tha thiết\".";
    String text1 = "Đại tướng Võ Nguyên Giáp tới thăm và làm việc tại Cần Thơ.";
    String text2 = "Thủ tướng Nguyễn Tấn Dũng nhận được sự ủng hộ của nhiều người dân Việt Nam.";
    String text3 = "Ngày hội việc làm được tổ chức thường niên tại Hà Nội";
    String text4 = "Tổng giá trị tài sản ròng (NAV) của 4 quỹ VEIL, VGF và VDeF, VPF tính đến cuối tháng 11 đạt 600 triệu USD." ;
    test(text1);  
  }

  private void test(String text) throws Exception{
    Dictionary dict = new Dictionary(Dictionary.DICT_RES) ;
    TokenAnalyzer[] opennlpAnalyzer = new TokenAnalyzer[] {
      new PunctuationTokenAnalyzer(), new CommonTokenAnalyzer(), new OpenNLPWSTokenAnalyzer(),
      new DictionaryTaggingAnalyzer(dict), new OpenNLPEntityTokenAnalyzer()
    };
    
    TokenAnalyzer[] crfAnalyzer = new TokenAnalyzer[] {
        new PunctuationTokenAnalyzer(), new CommonTokenAnalyzer(), new CRFWSTokenAnalyzer(),
        new DictionaryTaggingAnalyzer(dict), new CRFNPEntityTokenAnalyzer()
    };
    
    PrintStream out = ConsoleUtil.getUTF8SuportOutput() ;
    TabularTokenPrinter printer = new TabularTokenPrinter();
    
    TextSegmenter textSegmenter = new TextSegmenter(crfAnalyzer) ;
    IToken[] token = textSegmenter.segment(text) ;
    printer.print(out, token) ;
    
    System.out.println("********************************************");
    
    textSegmenter = new TextSegmenter(opennlpAnalyzer) ;
    token = textSegmenter.segment(text) ;
    printer.print(out, token) ;  
    
  }

//  private void testWtag(String wtext) throws Exception{
//    MLENT mlent = new MLENT("classpath:ml/nlp/entity.np.crf", "np", true);
//    mlent.testText(System.out, wtext, true);
//  }

}
