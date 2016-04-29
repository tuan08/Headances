package org.headvances.nlp.ml.pos;

import org.headvances.nlp.ml.token.ML;
import org.junit.Test;

public class MLPOSUnitTest {
	@Test
	public void test() throws Exception {
		ML mlpos = new ML("opennlp", new POSTokenFeaturesGenerator()) ;		
		String[] trainFiles = { "src/test/resources/pos/train.wtag"	 };
		mlpos.train(trainFiles, "target/pos.model", 100) ;
		
		String[] testFiles = { "src/test/resources/pos/train.wtag"	 };
		mlpos.test(testFiles, "target/pos.model") ;
	}
}
