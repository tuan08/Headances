package org.headvances.ml.nlp;

import java.io.PrintStream;

import org.headvances.nlp.token.TabularTokenPrinter;
import org.headvances.nlp.wtag.WTagTokenizer;
import org.headvances.util.ConsoleUtil;
import org.junit.Test;
/**
 * $Author: Tuan Nguyen$ 
 **/
public class WTagTokenizerUnitTest {
	@Test
	public void run() throws Exception {
		String sample1 = 
			"Nguyễn Tấn Dũng:{tt:word, ent:vnname, lex:np} đến thăm:{lex:vb} Hà Nội:{ent:loc, lex:np}\n" +
			"vào:{lex:ad} ngày:{lex:nn} 1/1/2011:{tt:date} .:{tt:punc} ...:{} test:{} \n";
		PrintStream out = ConsoleUtil.getUTF8SuportOutput() ;
		TabularTokenPrinter printer = new TabularTokenPrinter();
		WTagTokenizer tokenizer = new WTagTokenizer(sample1) ;
		printer.print(out, tokenizer.allTokens()) ;
		out.println("-----------------------------------------------------------") ;
	}
}
