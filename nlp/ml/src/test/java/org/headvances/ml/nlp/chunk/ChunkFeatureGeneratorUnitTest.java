package org.headvances.ml.nlp.chunk;

import java.io.PrintStream;

import org.headvances.ml.nlp.feature.IOB2TokenFeaturesPrinter;
import org.headvances.ml.nlp.feature.TokenFeaturesGenerator;
import org.headvances.nlp.token.TokenCollection;
import org.headvances.util.ConsoleUtil;
import org.junit.Test;

public class ChunkFeatureGeneratorUnitTest {
	static String SAMPLE1 = 
		"Đứa:{pos:Nc,chunk:NP:B} trẻ:{pos:N,chunk:NP:I} lên:{pos:V,chunk:VP:B} năm:{pos:M,chunk:QP:B} thì:{pos:C,chunk:O} chị:{pos:Nc,chunk:NP:B} ta:{pos:P,chunk:NP:I} lấy:{pos:V,chunk:VP:B} một:{pos:M,chunk:NP:B} người:{pos:Nc,chunk:NP:I} chồng:{pos:N,chunk:NP:I} cũng:{pos:R,chunk:VP:B} từng:{pos:R,chunk:VP:I} có:{pos:V,chunk:VP:I} một:{pos:M,chunk:NP:B} đời:{pos:N,chunk:NP:I} vợ:{pos:N,chunk:NP:I} và:{pos:C,chunk:NP:I} con:{pos:N,chunk:NP:I} riêng:{pos:A,chunk:NP:I} .:{pos:Mrk,chunk:O} ";
	
	@Test
	public void test() throws Exception {
    TokenFeaturesGenerator featuresGenerator = MLChunk.newTokenFeaturesGenerator() ;
    TokenCollection[] collections = featuresGenerator.getDocumentReader().read(SAMPLE1) ;
    test(featuresGenerator, collections) ;
	}
	
	private void test(TokenFeaturesGenerator featuresGenerator, TokenCollection[] collections) throws Exception {
		PrintStream out = ConsoleUtil.getUTF8SuportOutput() ;
		IOB2TokenFeaturesPrinter printer = new IOB2TokenFeaturesPrinter(out, featuresGenerator);
		printer.print(collections, true) ;
	}	
}
