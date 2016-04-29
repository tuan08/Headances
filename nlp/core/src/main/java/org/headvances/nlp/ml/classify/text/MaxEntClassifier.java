package org.headvances.nlp.ml.classify.text;

import org.headvances.nlp.ml.classify.FeaturesGenerator;
import org.headvances.nlp.ml.classify.MaxEntClassifierBase;
/**
 * $Author: Tuan Nguyen$
 **/
public class MaxEntClassifier extends MaxEntClassifierBase<TextDocument> {
	public MaxEntClassifier() throws Exception {
		this("classpath:nlp/text/model.maxent") ;
	}

	public MaxEntClassifier(String modelRes) throws Exception {
		super(modelRes, createFeatureGenerator()) ;
	}

	private static FeaturesGenerator<TextDocument> createFeatureGenerator() throws Exception {
		FeaturesGenerator<TextDocument> fgenerator = new FeaturesGenerator<TextDocument>() ;
		fgenerator.add(new TextFeatureGenerator()) ;	
		return fgenerator ;
	}
}