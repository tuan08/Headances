package org.headvances.ml.nlp.ws;

import java.io.PrintStream;

import org.headvances.ml.nlp.feature.TokenFeatures;
import org.headvances.ml.nlp.feature.TokenFeaturesGenerator;
import org.headvances.nlp.token.IToken;
import org.headvances.nlp.token.TokenCollection;
import org.headvances.util.ConsoleUtil;
import org.junit.Test;
/**
 * $Author: Tuan Nguyen$ 
 **/
public class WSFeatureGeneratorUnitTest {
	@Test
	public void test() throws Exception {
		TokenFeaturesGenerator featuresGenerator = MLWS.newTokenFeaturesGenerator() ;
		String text1 = 
			//"This:{} is:{} the:{} 1st:{} sentence:{} .:{}\n" +
			"ABC:{} Nguyễn Tấn Dũng:{} đến thăm:{} Hà Nội:{} vào:{} ngày:{} 1/1/2011:{} .:{}";
		String text2 = "Voi:{} về:{} !:{}" ;
		test(featuresGenerator, text1) ;
	}
	
	private void test(TokenFeaturesGenerator featuresGenerator, String text) throws Exception {
		TokenCollection[] collection = featuresGenerator.getDocumentReader().read(text) ;
		for(int i = 0; i < collection.length; i++) {
			test(featuresGenerator, collection[i].getTokens()) ;
		}
	}	
	
	private void test(TokenFeaturesGenerator featuresGenerator, IToken[] token) throws Exception {
		PrintStream out = ConsoleUtil.getUTF8SuportOutput() ;
		TokenFeatures[] tokenFeatures = featuresGenerator.generate(token, true) ;
		for(int i = 0; i < tokenFeatures.length; i++) {
			String[] feature = tokenFeatures[i].getFeatures();
			for(int j = 0; j < feature.length; j++) {
				if(j > 0) out.append(' ') ;
				out.append(feature[j]) ;
			}
			out.append(' ').append(tokenFeatures[i].getTargetFeature()).println() ;
		}
	}
}