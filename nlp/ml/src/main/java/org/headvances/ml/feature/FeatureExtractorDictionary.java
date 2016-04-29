package org.headvances.ml.feature;

import java.util.ArrayList;
import java.util.List;
/**
 * $Author: Tuan Nguyen$
 **/
public class FeatureExtractorDictionary<T> extends FeatureExtractor<T> {
	private FeatureDictionary dictionary ;

	public FeatureExtractorDictionary(FeatureGenerator<T> generator, FeatureDictionary dictionary) {
		super(generator);
		this.dictionary = dictionary ;
	}

	public Feature[] extract(T tdoc) {
		Feature[] features = super.extract(tdoc) ;
		List<Feature> holder = new ArrayList<Feature>() ;
		for(int i = 0; i < features.length; i++) {
			Feature dfeature = dictionary.getFeature(features[i].getFeature());
			if(dfeature != null) {
				features[i].setId(dfeature.getId()) ;
				features[i].setDocFrequency(dfeature.getDocFrequency()) ;
				features[i].setWeight(dfeature.getWeight()) ;
				holder.add(features[i]) ;
			}
		}
		return holder.toArray(new Feature[holder.size()]) ;
	}
}