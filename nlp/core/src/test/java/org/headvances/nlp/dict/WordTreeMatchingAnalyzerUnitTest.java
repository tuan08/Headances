package org.headvances.nlp.dict;

import java.io.PrintStream;

import org.headvances.nlp.NLPResource;
import org.headvances.nlp.token.IToken;
import org.headvances.nlp.token.TabularTokenPrinter;
import org.headvances.nlp.token.TextSegmenter;
import org.headvances.nlp.token.analyzer.CommonTokenAnalyzer;
import org.headvances.nlp.token.analyzer.GroupTokenMergerAnalyzer;
import org.headvances.nlp.token.analyzer.PunctuationTokenAnalyzer;
import org.headvances.nlp.token.analyzer.TokenAnalyzer;
import org.headvances.nlp.token.analyzer.USDTokenAnalyzer;
import org.headvances.nlp.token.analyzer.VNDTokenAnalyzer;
import org.headvances.nlp.ws.WordTreeMatchingAnalyzer;
import org.headvances.util.ConsoleUtil;
import org.junit.Test;

/**
 * $Author: Tuan Nguyen$ 
 **/
public class WordTreeMatchingAnalyzerUnitTest {
  @Test
  public void test() throws Exception {
    Dictionary dict = NLPResource.getInstance().getDictionary(Dictionary.DICT_RES) ;
    TokenAnalyzer[] analyzer = {
        new PunctuationTokenAnalyzer(), new CommonTokenAnalyzer(),
        new GroupTokenMergerAnalyzer(),
        new VNDTokenAnalyzer(), new USDTokenAnalyzer(), 
        new WordTreeMatchingAnalyzer(dict),
        new UnknownWordTokenSplitter(dict)
    };
    String text = "Trong 2 gần đây, trên thị trường chưa từng có dự án chung cư nào có giá dưới 20 triệu đồng/m2 nằm trong vành đai 3 khu vực lõi Hà Nội, ngay cả bên ngoài vùng ven như Hà Đông, Hoài Đức, hay khu Long Biên cũng phải trên 20 triệu đồng/m2." ;

    TextSegmenter textSegmenter = new TextSegmenter(analyzer);
    test(textSegmenter, text);
  }

  private void test(TextSegmenter textSegmenter, String text) throws Exception {
    IToken[] token = textSegmenter.segment(text) ;
    PrintStream out = ConsoleUtil.getUTF8SuportOutput() ;
    TabularTokenPrinter printer = new TabularTokenPrinter();
    printer.print(out, token) ;
  }
}
