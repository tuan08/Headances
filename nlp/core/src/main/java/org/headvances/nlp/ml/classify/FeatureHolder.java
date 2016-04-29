package org.headvances.nlp.ml.classify;

import java.util.LinkedHashMap;
/**
 * $Author: Tuan Nguyen$ 
 **/
public class FeatureHolder extends LinkedHashMap<String, Feature> {
	private int    docId ;
	private String label ;
	
	public FeatureHolder(int docId) {
		this.docId = docId ;
	}

	public int getDocId() { return 0; }
	
  public String getTargetLabel() { return label ; }
	public void   setTargetLabel(String label) { this.label = label ; }
  
	public Feature add(String feature) {
		Feature gfeature = get(feature) ;
		if(gfeature == null) {
			gfeature = new Feature(feature) ;
			put(feature, gfeature) ;
		} else {
			gfeature.addFeatureFreq(1) ;
		}
		return gfeature ;
	}
	
	public Feature add(String prefix, String feature) {
		feature = prefix + ":" + feature ;
		return add(feature) ;
	}
	
	public void add(String prefix, String[] feature) {
		for(String sel : feature) {
			add(prefix, sel) ;
		}
	}
	
	public Feature[] getFeature() {
		return values().toArray(new Feature[size()]) ;
	}
}