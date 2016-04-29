package org.headvances.ml.nlp.ws;

import java.io.PrintStream;

import org.headvances.nlp.NLPResource;
import org.headvances.nlp.dict.Dictionary;
import org.headvances.nlp.dict.DictionaryTaggingAnalyzer;
import org.headvances.nlp.token.IToken;
import org.headvances.nlp.token.TabularTokenPrinter;
import org.headvances.nlp.token.TextSegmenter;
import org.headvances.nlp.token.analyzer.CommonTokenAnalyzer;
import org.headvances.nlp.token.analyzer.PunctuationTokenAnalyzer;
import org.headvances.nlp.token.analyzer.TokenAnalyzer;
import org.headvances.util.ConsoleUtil;
import org.headvances.util.JVMInfoUtil;
import org.junit.Test;
/**
 * $Author: Tuan Nguyen$ 
 **/
public class WordSegmenterUnitTest {
  @Test
  public void test() throws Exception {
  	System.out.println(JVMInfoUtil.getMemoryUsageInfo());
    Dictionary dict = NLPResource.getInstance().getDictionary(Dictionary.DICT_RES);
    TokenAnalyzer[] opennlpAnalyzer = new TokenAnalyzer[] {
      new PunctuationTokenAnalyzer(), 
      new CommonTokenAnalyzer(), 
      new OpenNLPWSTokenAnalyzer(),
      new DictionaryTaggingAnalyzer(dict)
    };

    TokenAnalyzer[] malletCRFAnalyzer = new TokenAnalyzer[] {
        new PunctuationTokenAnalyzer(), 
        new CommonTokenAnalyzer(), 
        new CRFWSTokenAnalyzer(), 
        new DictionaryTaggingAnalyzer(dict)  
    };

    String text1 = "Tổng bí thư Nguyễn Tấn Dũng đến thăm và làm việc tại Hà Nội." ;
    //this text contains the incorrect vietnamese characters.
    String text2  = "- TS Trần Thế Trung, Viện trưởng Viện nghiên cứu công nghệ FPT: Viện đang có một số dự án xoay quanh công nghệ thông tin, ngoài ra có cả dự án về Công nghệ vũ trụ, Công nghệ sinh học.";
    String text3  = "Lãnh đạo ĐH FPT và sinh viên Trần Hải Đăng tham gia buổi tư vấn trực tuyến. Ảnh: sinh viên." ;
    String text4  = "Khả năng phiến quân tại Libya đã đẩy lùi kế hoạch tái chiến thị trấn dầu lửa Brega ở phía Đông từ các nhóm lính trung thành với Đại tá Muammar Gaddafi." ;
    String text5  = "Nguyễn Hoàng mâu thuẫn với ai thời hậu lê." ;
    String text6  = "Ai là người đầu tiên đặt chân lên mặt trăng" ;
    String text7  = "- TS Trần Thế Trung, Viện trưởng Viện nghiên cứu công nghệ FPT: " ;
    String text8  = "Phật giáo , tôn giáo lớn thứ hai ở đất nước mặt trời mọc trái đất, Nhật Bản";
    String text9  = "Các khoản vay phải được trả lãi đúng hạn";
    String text10 = "Cù Huy Hà Vũ bị bắt hôm chủ nhật";
    String text11 = "Học sinh học sinh học.";
    String text12 = "Trong 2 gần đây, trên thị trường chưa từng có dự án chung cư nào có giá dưới 20 triệu đồng/m2 nằm trong vành đai 3 khu vực lõi Hà Nội, ngay cả bên ngoài vùng ven như Hà Đông, Hoài Đức, hay khu Long Biên cũng phải trên 20 triệu đồng/m2.";
    String text13 = "Hôm nay mặt trăng sáng quá";
      
    System.out.println("******************Maxent Analyzer***************");
    String selectText = text3;
    TextSegmenter textSegmenter = new TextSegmenter(opennlpAnalyzer) ;
    test(textSegmenter, selectText) ;

    System.out.println("******************CRF Analyzer******************");

    textSegmenter = new TextSegmenter(malletCRFAnalyzer);
    test(textSegmenter, selectText);
  }

  private void test(TextSegmenter textSegmenter, String text) throws Exception {
    IToken[] token = textSegmenter.segment(text) ;
    PrintStream out = ConsoleUtil.getUTF8SuportOutput() ;
    TabularTokenPrinter printer = new TabularTokenPrinter();
    printer.print(out, token) ;
  }

  //	private void testWTag(String text) throws Exception {
  //		MLWS mlws = new MLWS("classpath:ml/nlp/vnws.crf", true) ;
  //		mlws.testText(System.out, text, true) ;
  //	}
}