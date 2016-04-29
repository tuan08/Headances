package org.headvances.ml.nlp.ent;

import java.io.PrintStream;

import org.headvances.ml.nlp.feature.IOB2TokenFeaturesPrinter;
import org.headvances.ml.nlp.feature.TokenFeaturesGenerator;
import org.headvances.nlp.token.TabularTokenPrinter;
import org.headvances.nlp.token.TokenCollection;
import org.headvances.util.ConsoleUtil;
import org.junit.Test;
/**
 * $Author: Tuan Nguyen$ 
 **/
public class EntityFeatureGeneratorUnitTest {
	@Test
	public void test() throws Exception {
		TokenFeaturesGenerator featuresGenerator = 
			MLENT.createTokenFeaturesGenerator(EntitySetConfig.getConfig("np")) ;
		
		String text1 = 
			"Nguyễn Tấn Dũng:{per:B} đến thăm:{} thành phố:{} Hà Nội:{loc:B} vào:{} ngày:{} 1/1/2011:{} 123:{} 1.234:{} 123km:{} .:{}";
		
		String text2 = "12.000:{} đồng:{} ,:{} 12.000đồng:{qt:currency:B} ,:{} 12:{} triệu:{}" ;
		TokenCollection[] collections = featuresGenerator.getDocumentReader().read(text1);
		test(featuresGenerator, collections) ;
	}
	
	private void test(TokenFeaturesGenerator featuresGenerator, TokenCollection[] collections) throws Exception {
		PrintStream out = ConsoleUtil.getUTF8SuportOutput() ;
		TabularTokenPrinter printer = new TabularTokenPrinter();
		printer.print(out, collections) ;
		
		IOB2TokenFeaturesPrinter fprinter = new IOB2TokenFeaturesPrinter(out, featuresGenerator) ;
		fprinter.print(collections, true) ;
	}	
}