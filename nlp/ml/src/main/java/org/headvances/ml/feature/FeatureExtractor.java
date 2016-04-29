package org.headvances.ml.feature;

/**
 * $Author: Tuan Nguyen$
 **/
public class FeatureExtractor<T> {
	private FeatureGenerator<T>	generator;
	private int	                docIdTracker;
	private boolean             useMapFeatureHolder = false ;
	
	public FeatureExtractor(FeatureGenerator<T> generator) {
		this.generator = generator;
	}
	
	public void setUseMapFeatureHolder(boolean b ) {
		useMapFeatureHolder = b ;
	}
	
	public Feature[] extract(T tdoc) {
		FeatureHolder holder ;
		if(useMapFeatureHolder) holder = new MapFeatureHolder(docIdTracker);
		else                    holder = new ListFeatureHolder(docIdTracker) ;
		generator.generate(holder, tdoc);
		GenerateFeature[] gfeatures = holder.getFeatures();
		Feature[] features = new Feature[gfeatures.length];
		for (int i = 0; i < gfeatures.length; i++) {
		  features[i] = new Feature(gfeatures[i].getFeature());
		  features[i].setId(i) ;
		  features[i].setTermFrequency(gfeatures[i].getFrequency());
		}
		docIdTracker++;
		return features ;
	}
}