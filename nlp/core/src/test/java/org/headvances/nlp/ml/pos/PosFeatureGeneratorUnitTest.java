package org.headvances.nlp.ml.pos;

import org.headvances.nlp.ml.token.TokenFeatures;
import org.junit.Test;

public class PosFeatureGeneratorUnitTest {
	static String SAMPLE1 = 
		"Nguyễn Tấn Dũng:{pos:Np} đến thăm:{pos:Vp} Hà Nội:{pos:Np} vào:{pos:E} ngày:{pos:N} 1/1/2011:{pos:O} .:{pos:O}";
	
	@Test
	public void test() throws Exception {
    POSTokenFeaturesGenerator featuresGenerator = new POSTokenFeaturesGenerator() ;
    TokenFeatures[] tfeatures = featuresGenerator.generate(SAMPLE1) ;
    TokenFeatures.dump(tfeatures) ;
	}
}
