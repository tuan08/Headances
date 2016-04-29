package org.headvances.nlp.ml.pos;

import org.headvances.nlp.ml.token.OpenNLPTokenAnalyzer;
import org.headvances.nlp.token.IToken;
import org.headvances.nlp.token.tag.PosTag;
/**
 * $Author: Tuan Nguyen$ 
 **/
public class POSOpenNLPTokenAnalyzer extends OpenNLPTokenAnalyzer {
	public POSOpenNLPTokenAnalyzer() throws Exception {
	  this("classpath:nlp/pos/model.opennlp");
  }
	
  public POSOpenNLPTokenAnalyzer(String crfModelFile) throws Exception {
	  super(new POSTokenFeaturesGenerator(), crfModelFile);
  }

	protected void tag(IToken token, String predict) {
		token.removeTagType(PosTag.class) ;
  	token.add(new PosTag(token.getNormalizeForm(), predict)) ;
	}
}