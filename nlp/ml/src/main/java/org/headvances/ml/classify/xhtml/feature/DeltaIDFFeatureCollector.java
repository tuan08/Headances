package org.headvances.ml.classify.xhtml.feature;

import org.headvances.html.dom.TDocument;
import org.headvances.ml.feature.DeltaIDFFeatures;
import org.headvances.ml.feature.Feature;
import org.headvances.ml.feature.FeatureDictionary;
import org.headvances.ml.feature.FeatureGenerator;
import org.headvances.ml.feature.GenerateFeature;
/**
 * $Author: Tuan Nguyen$ 
 **/
public class DeltaIDFFeatureCollector extends FeatureCollector {
	private DeltaIDFFeatures deltaIdf;
	public DeltaIDFFeatureCollector(FeatureGenerator<TDocument> generator) {
		super(generator) ;
		deltaIdf = new DeltaIDFFeatures();
	}

	public void collect(GenerateFeature[] gfeatures, String labelName) {
		deltaIdf.add(gfeatures, labelName);
	}

	public FeatureDictionary getFeatureDictionary() {
		FeatureDictionary dict = new FeatureDictionary() ;
		deltaIdf.setNumOfDocTracker(docIdTracker);
		dict.setLabels(getLabels());

		Feature[] features = deltaIdf.getFeatures();
		for(Feature f: features) {
			if(f.getFeature().contains("dom:")) {
				f.setWeight(f.getWeight() * 2f) ;
			}
			dict.addFeature(f);
		}
		return dict ;
	}
}
