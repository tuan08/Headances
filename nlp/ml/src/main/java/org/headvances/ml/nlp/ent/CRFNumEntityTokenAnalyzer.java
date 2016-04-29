package org.headvances.ml.nlp.ent;

import org.headvances.nlp.token.IToken;
/**
 * $Author: Tuan Nguyen$ 
 **/
public class CRFNumEntityTokenAnalyzer extends CRFEntityTokenAnalyzer {
	public CRFNumEntityTokenAnalyzer() throws Exception {
		super("classpath:ml/nlp/entity.num.crf", EntitySetConfig.getConfig("num")) ;
	}
	
	public CRFNumEntityTokenAnalyzer(String crfModelFile) throws Exception {
		super(crfModelFile, EntitySetConfig.getConfig("num")) ;
  }

	protected void setEntityTag(String type, IToken token) {
		token.add(new EntityTag(type, token.getOriginalForm())) ;
	}
}