package org.headvances.nlp.ml.pos;

import java.io.PrintStream;

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

public class POSTokenAnalyzerUnitTest {
	@Test
	public void test() throws Exception{
		String text1 = "pha chơi xấu lập tức khiến tiền vệ ngôi sao của real madrid bị chấn thương. " ;
		String text2 = "Cuộc hội đàm đã diễn ra trong không khí trang trọng và nghiêm túc.";
		String text3 = "Hội nghị xử lý ngôn ngữ tự nhiên châu Á 2011 được tổ chức tại Malaysia vào giữa tháng 11.";
		String text4 = "Ông Cù Huy Hà Vũ bị bắt hôm chủ nhật";
		String text5 = "Học sinh học sinh học";

		test(text1 + text3);
	}
  
  private void test(String text) throws Exception{
  	Dictionary dict = new Dictionary(Dictionary.DICT_RES) ;
  	TokenAnalyzer[] wsanalyzer = new TokenAnalyzer[] {
  	  new PunctuationTokenAnalyzer(), new CommonTokenAnalyzer(), new NGramStatisticWSTokenAnalyzer(), 
  		new DictionaryTaggingAnalyzer(dict) 
  	};
  	TextSegmenter textSegmenter = new TextSegmenter(wsanalyzer) ;
    IToken[] token = textSegmenter.segment(text) ;

  	PrintStream out = ConsoleUtil.getUTF8SuportOutput() ;
    TabularTokenPrinter printer = new TabularTokenPrinter();
    
    out.println("***********************CRF*********************************");
    TokenAnalyzer crfPosAnalyzer = new POSCRFTokenAnalyzer() ;
    token = crfPosAnalyzer.analyze(token) ;
    printer.print(out, token) ;
    
    out.println("***********************OPENNLP*********************************");
    TokenAnalyzer opennlpPosAnalyzer = new POSOpenNLPTokenAnalyzer() ;
    for(int i = 0; i < 20; i++) {
    	long start = System.currentTimeMillis() ;
    	token = opennlpPosAnalyzer.analyze(token) ;
    	printer.print(out, token);
    	System.out.println("Decode in " + (System.currentTimeMillis() - start));
    }
  }
}
