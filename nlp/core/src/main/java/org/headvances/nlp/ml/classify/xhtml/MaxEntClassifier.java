package org.headvances.nlp.ml.classify.xhtml;

import org.headvances.html.dom.TDocument;
import org.headvances.nlp.ml.classify.FeaturesGenerator;
import org.headvances.nlp.ml.classify.MaxEntClassifierBase;
/**
 * $Author: Tuan Nguyen$
 **/
public class MaxEntClassifier extends MaxEntClassifierBase<TDocument> {
	public MaxEntClassifier() throws Exception {
		this("classpath:nlp/xhtml/model.maxent") ;
	}

	public MaxEntClassifier(String modelRes) throws Exception {
		super(modelRes, createFeatureGenerator()) ;
	}

	private static FeaturesGenerator<TDocument> createFeatureGenerator() throws Exception {
		FeaturesGenerator<TDocument> fgenerator = new FeaturesGenerator<TDocument>() ;
		fgenerator.add(new URLFeatureGenerator()) ;		
		fgenerator.add(new ShortTextFeatureGenerator()) ;
		fgenerator.add(new DOMFeatureGenerator()) ;
		return fgenerator ;
	}
}