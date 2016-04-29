package org.headvances.ml.nlp.ent;

import org.headvances.nlp.token.IToken;
/**
 * $Author: Tuan Nguyen$ 
 **/
public class CRFNPEntityTokenAnalyzer extends CRFEntityTokenAnalyzer {
	public CRFNPEntityTokenAnalyzer() throws Exception {
		super("classpath:ml/nlp/entity.np.crf", EntitySetConfig.getConfig("np")) ;
	}
	
	public CRFNPEntityTokenAnalyzer(String crfModelFile) throws Exception {
		super(crfModelFile, EntitySetConfig.getConfig("np")) ;
  }

  protected void setEntityTag(String type, IToken token) {
		token.add(new EntityTag(type, token.getOriginalForm())) ;
	}
}