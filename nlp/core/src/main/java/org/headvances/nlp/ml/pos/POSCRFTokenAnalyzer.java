package org.headvances.nlp.ml.pos;

import org.headvances.nlp.ml.token.MalletCRFTokenAnalyzer;
import org.headvances.nlp.token.IToken;
import org.headvances.nlp.token.tag.PosTag;
/**
 * $Author: Tuan Nguyen$ 
 **/
public class POSCRFTokenAnalyzer extends MalletCRFTokenAnalyzer {
	public POSCRFTokenAnalyzer() throws Exception {
	  this("classpath:nlp/pos/model.crf");
  }
	
  public POSCRFTokenAnalyzer(String crfModelFile) throws Exception {
	  super(new POSTokenFeaturesGenerator(), crfModelFile);
  }

	protected void tag(IToken token, String predict) {
		token.removeTagType(PosTag.class) ;
  	token.add(new PosTag(token.getNormalizeForm(), predict)) ;
	}
}