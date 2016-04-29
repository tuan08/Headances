package org.headvances.ml.feature;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Iterator;
/**
 * $Author: Tuan Nguyen$ 
 **/
public class ListFeatureHolder extends ArrayList<GenerateFeature> implements FeatureHolder {
	private int    docId ;
	private String targetLabel ;
	
	public ListFeatureHolder(int docId) {
		this.docId = docId ;
	}
	
	public int getDocId() { return docId ; }
	
	public String getTargetLabel() { return this.targetLabel ; } 
	public void   setTargetLabel(String label) { this.targetLabel = label ; }
	
	public GenerateFeature add(String category, String feature) {
		GenerateFeature gfeature = new GenerateFeature(docId, category, feature);
		add(gfeature) ;
		return gfeature ;
	}

	public void add(String category, String feature, boolean goodFeature) {
		GenerateFeature gfeature = new GenerateFeature(docId, category, feature) ;
		add(gfeature) ;
	}
	
	public void add(String prefix, Collection<String> features) {
		Iterator<String> i = features.iterator() ;
		while(i.hasNext()) {
			add(prefix , i.next()) ;
		}
	}
	
	public void add(String category, String[] features) {
		for(String sel : features) add(category, sel) ;
	}
	
	public GenerateFeature[] getFeatures() {
		return toArray(new GenerateFeature[size()]) ;
	}
}