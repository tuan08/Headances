package org.headvances.ml.nlp.feature;

import java.io.PrintStream;

import org.headvances.nlp.token.IToken;
import org.headvances.nlp.token.TokenCollection;
/**
 * $Author: Tuan Nguyen$ 
 **/
public class IOB2TokenFeaturesPrinter {
	private PrintStream out ;
	private TokenFeaturesGenerator featuresGenerator ;
	
	public IOB2TokenFeaturesPrinter(PrintStream out, TokenFeaturesGenerator featuresGenerator) {
		this.out = out ;
		this.featuresGenerator = featuresGenerator ;
	}
	
	public void print(TokenCollection[] collection, boolean target) {
		for(int i = 0; i < collection.length; i++) {
			print(collection[i].getTokens(), target) ;
			if(i + 1 < collection.length) out.println() ;
		}
	}
	
	public void print(IToken[] token, boolean target) {
		TokenFeatures[] tokenFeatures = featuresGenerator.generate(token, target) ;
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
