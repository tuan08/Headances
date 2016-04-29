package org.headvances.ml.feature;

import java.util.Collection;
import java.util.Iterator;
import java.util.LinkedHashMap;
/**
 * $Author: Tuan Nguyen$ 
 **/
public class MapFeatureHolder extends LinkedHashMap<String, GenerateFeature> implements FeatureHolder {
	private int    docId ;
	private String label ;
	
	public MapFeatureHolder(int docId) {
		this.docId = docId ;
	}

	public int getDocId() { return 0; }
	
  public String getTargetLabel() { return label ; }
	public void   setTargetLabel(String label) { this.label = label ; }
  
	public GenerateFeature add(String category, String feature) {
	  String featureId = GenerateFeature.generateFeature(category, feature) ;
		GenerateFeature gfeature = get(feature) ;
		if(gfeature == null) {
			gfeature = new GenerateFeature(docId, category, feature) ;
			put(featureId, gfeature) ;
		} else {
			gfeature.addFrequency(1) ;
		}
		return gfeature ;
	}
	
	public void add(String prefix, Collection<String> features) {
		Iterator<String> i = features.iterator() ;
		while(i.hasNext()) {
			add(prefix, i.next()) ;
		}
	}
	
	public void add(String prefix, String[] features) {
		for(String sel : features) {
			add(prefix, sel) ;
		}
	}
	
	public GenerateFeature[] getFeatures() {
		return values().toArray(new GenerateFeature[size()]) ;
	}
}