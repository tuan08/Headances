package org.headvances.ml.nlp.pos;

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
import org.headvances.nlp.ws.NGramStatisticWSTokenAnalyzer;
import org.headvances.util.ConsoleUtil;
import org.junit.Test;

public class PosTaggerUnitTest {
  
  @Test
  public void test() throws Exception{
    String text1 = "pha chơi xấu lập tức khiến tiền vệ ngôi sao của real madrid bị chấn thương." ;
    String text2 = "Cuộc hội đàm đã diễn ra trong không khí trang trọng và nghiêm túc.";
    String text3 = "Hội nghị xử lý ngôn ngữ tự nhiên châu Á 2011 được tổ chức tại Malaysia vào giữa tháng 11.";
    String text4 = "Ông Cù Huy Hà Vũ bị bắt hôm chủ nhật";
    String text5 = "Học sinh học sinh học";
    
    test(text5);
}
  
  private void test(String text) throws Exception{
    Dictionary dict = new Dictionary(Dictionary.DICT_RES) ;
    TokenAnalyzer[] malletAnalyzer = new TokenAnalyzer[] {
      new PunctuationTokenAnalyzer(), new CommonTokenAnalyzer(), new NGramStatisticWSTokenAnalyzer(), 
      new DictionaryTaggingAnalyzer(dict), new CRFPOSTokenAnalyzer() 
    };
    
    TokenAnalyzer[] opennlpAnalyzer = new TokenAnalyzer[] {
        new PunctuationTokenAnalyzer(), new CommonTokenAnalyzer(), new NGramStatisticWSTokenAnalyzer(), 
        new DictionaryTaggingAnalyzer(dict), new OpenNLPPOSTokenAnalyzer() 
      };
    
    PrintStream out = ConsoleUtil.getUTF8SuportOutput() ;
    TabularTokenPrinter printer = new TabularTokenPrinter();
    
    out.println("***********************CRF*********************************");
    TextSegmenter textSegmenter = new TextSegmenter(malletAnalyzer) ;
    IToken[] token = textSegmenter.segment(text) ;
    printer.print(out, token) ;
    
    out.println("***********************OPENNLP*********************************");
    
    textSegmenter = new TextSegmenter(opennlpAnalyzer);
    token = textSegmenter.segment(text);
    printer.print(out, token);
  }
}
