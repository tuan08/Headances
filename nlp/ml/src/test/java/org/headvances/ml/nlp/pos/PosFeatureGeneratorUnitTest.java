package org.headvances.ml.nlp.pos;

import java.io.PrintStream;

import org.headvances.ml.nlp.feature.IOB2TokenFeaturesPrinter;
import org.headvances.ml.nlp.feature.TokenFeaturesGenerator;
import org.headvances.nlp.token.TokenCollection;
import org.headvances.util.ConsoleUtil;
import org.junit.Test;

public class PosFeatureGeneratorUnitTest {
	static String SAMPLE1 = 
		"Nguyễn Tấn Dũng:{} đến thăm:{} Hà Nội:{} vào:{} ngày:{} 1/1/2011:{} .:{}";
	
	@Test
	public void test() throws Exception {
    TokenFeaturesGenerator featuresGenerator = MLPOS.newTokenFeaturesGenerator() ;
    TokenCollection[] collections = featuresGenerator.getDocumentReader().read(SAMPLE1) ;
    test(featuresGenerator, collections) ;
	}
	
	private void test(TokenFeaturesGenerator featuresGenerator, TokenCollection[] collections) throws Exception {
		PrintStream out = ConsoleUtil.getUTF8SuportOutput() ;
		IOB2TokenFeaturesPrinter printer = new IOB2TokenFeaturesPrinter(out, featuresGenerator);
		printer.print(collections, true) ;
	}	
}
