package org.headvances.ml.nlp.chunk;

import java.io.PrintStream;

import org.headvances.ml.nlp.pos.OpenNLPPOSTokenAnalyzer;
import org.headvances.nlp.dict.Dictionary;
import org.headvances.nlp.dict.DictionaryTaggingAnalyzer;
import org.headvances.nlp.token.IToken;
import org.headvances.nlp.token.TabularTokenPrinter;
import org.headvances.nlp.token.TextSegmenter;
import org.headvances.nlp.token.analyzer.CommonTokenAnalyzer;
import org.headvances.nlp.token.analyzer.PunctuationTokenAnalyzer;
import org.headvances.nlp.token.analyzer.TokenAnalyzer;
import org.headvances.nlp.token.tag.PosTag;
import org.headvances.nlp.token.tag.TokenTag;
import org.headvances.nlp.ws.NGramStatisticWSTokenAnalyzer;
import org.headvances.util.ConsoleUtil;
import org.junit.Test;

public class ChunkingUnitTest {

  @Test
  public void test() throws Exception {
    String text = "Học sinh học toán học";
    test(text);
  }

  private void test(String text) throws Exception {
    Dictionary dict = new Dictionary(Dictionary.DICT_RES);

    TokenAnalyzer[] opennlpAnalyzer = new TokenAnalyzer[] {
      new PunctuationTokenAnalyzer(), new CommonTokenAnalyzer(),
      new NGramStatisticWSTokenAnalyzer(), 
      new DictionaryTaggingAnalyzer(dict),
      new OpenNLPPOSTokenAnalyzer(), 
      new CRFChunkTagTokenAnalyzer()
    };

    PrintStream out = ConsoleUtil.getUTF8SuportOutput();
    TabularTokenPrinter printer = new TabularTokenPrinter() {
    	protected boolean isPrintTag(TokenTag sel) {
    		if(sel.isTypeOf(ChunkTag.TYPE)) return true ;
    		if(sel.isTypeOf(PosTag.TYPE)) return true ;
    		return false ;
    	}
    } ;

    TextSegmenter textSegmenter = new TextSegmenter(opennlpAnalyzer);
    IToken[] token = textSegmenter.segment(text);
    printer.print(out, token);
  }
}
