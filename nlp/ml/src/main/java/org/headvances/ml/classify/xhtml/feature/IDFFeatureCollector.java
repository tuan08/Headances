package org.headvances.ml.classify.xhtml.feature;

import org.headvances.html.dom.TDocument;
import org.headvances.ml.feature.Feature;
import org.headvances.ml.feature.FeatureDictionary;
import org.headvances.ml.feature.FeatureGenerator;
import org.headvances.ml.feature.GenerateFeature;
import org.headvances.ml.feature.IDFFeatures;
/**
 * $Author: Tuan Nguyen$ 
 **/
public class IDFFeatureCollector extends FeatureCollector {
	private IDFFeatures idf;
	public IDFFeatureCollector(FeatureGenerator<TDocument> generator) {
		super(generator) ;
		idf = new IDFFeatures();
	}

	public void collect(GenerateFeature[] gfeatures, String labelName) {
		idf.add(gfeatures, labelName);
	}

	public FeatureDictionary getFeatureDictionary() {
		FeatureDictionary dict = new FeatureDictionary() ;
		idf.setNumOfDocId(docIdTracker);
		dict.setLabels(getLabels());

		Feature[] features = idf.getFeatures();
		for(Feature f: features) {
			if(f.getFeature().startsWith("dom:")) {
				f.setWeight(f.getWeight() * 2f) ;
			}
			dict.addFeature(f);
		}
		return dict ;
	}
}
