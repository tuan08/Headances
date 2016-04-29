package org.headvances.nlp.ml.token;

import org.headvances.nlp.token.IToken;
import org.headvances.util.IOUtil;
/**
 * $Author: Tuan Nguyen$ 
 **/
abstract public class TokenFeaturesGenerator {
	protected TokenFeatureGenerator[] fgenerator ;
	protected TokenFeatureGenerator   targetFeatureGenrator ;

	public void add(TokenFeatureGenerator fgenerator) {
		if(this.fgenerator == null) {
			this.fgenerator = new TokenFeatureGenerator[] {fgenerator} ;
		} else {
			TokenFeatureGenerator[] temp = new TokenFeatureGenerator[this.fgenerator.length + 1] ;
			System.arraycopy(this.fgenerator, 0, temp, 0, this.fgenerator.length) ;
			temp[this.fgenerator.length] = fgenerator ;
			this.fgenerator = temp ;
		}
	}

	public void setTargetFeatureGenerator(TokenFeatureGenerator fgenerator) {
		this.targetFeatureGenrator = fgenerator ;
	}

	public TokenFeatures[] generate(IToken[] token, boolean generateTarget) {
		TokenFeatures[] tokenFeatures = new TokenFeatures[token.length] ;
		TokenFeatureHolder featureHolder = new TokenFeatureHolder() ;
		for(int i = 0; i < token.length; i++) {
			for(int j = 0; j < fgenerator.length; j++) {
				fgenerator[j].generate(token, i, featureHolder) ;
			}
			if(generateTarget && this.targetFeatureGenrator != null) {
				this.targetFeatureGenrator.generate(token, i, featureHolder) ;
			}

			tokenFeatures[i] =
				new TokenFeatures(token[i].getOriginalForm(), featureHolder.getFeatures(), featureHolder.getTargetFeature()) ;
			featureHolder.reset() ;
		}
		return tokenFeatures ;
	}
	
	abstract public TokenFeatures[] generate(String text) throws Exception ;
	
	public TokenFeatures[] generateWTagFile(String wtagFile) throws Exception {
		String text = IOUtil.getFileContentAsString(wtagFile, "UTF-8") ;
		return generate(text) ;
  }
}