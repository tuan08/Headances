package org.headvances.nlp.ml.chunk;

import org.headvances.nlp.ml.token.TokenFeatures;
import org.headvances.nlp.ml.token.TokenFeaturesGenerator;
import org.junit.Test;

public class ChunkFeatureGeneratorUnitTest {
	static String SAMPLE1 = 
		"Đứa:{pos:Nc,chunk:NP:B} trẻ:{pos:N,chunk:NP:I} lên:{pos:V,chunk:VP:B} năm:{pos:M,chunk:QP:B} thì:{pos:C,chunk:O} chị:{pos:Nc,chunk:NP:B} ta:{pos:P,chunk:NP:I} lấy:{pos:V,chunk:VP:B} một:{pos:M,chunk:NP:B} người:{pos:Nc,chunk:NP:I} chồng:{pos:N,chunk:NP:I} cũng:{pos:R,chunk:VP:B} từng:{pos:R,chunk:VP:I} có:{pos:V,chunk:VP:I} một:{pos:M,chunk:NP:B} đời:{pos:N,chunk:NP:I} vợ:{pos:N,chunk:NP:I} và:{pos:C,chunk:NP:I} con:{pos:N,chunk:NP:I} riêng:{pos:A,chunk:NP:I} .:{pos:Mrk,chunk:O} ";
	
	@Test
	public void test() throws Exception {
    TokenFeaturesGenerator generator = new ChunkTokenFeaturesGenerator() ;
    TokenFeatures[] tfeatures = generator.generate(SAMPLE1) ;
    TokenFeatures.dump(tfeatures) ;
	}
}
