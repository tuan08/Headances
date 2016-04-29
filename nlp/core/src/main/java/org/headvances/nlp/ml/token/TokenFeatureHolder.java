package org.headvances.nlp.ml.token;

import java.util.ArrayList;
import java.util.List;
/**
 * $Author: Tuan Nguyen$ 
 **/
public class TokenFeatureHolder {
	private List<String> features = new ArrayList<String>() ;
	private String       targetFeature ;
	
	public void reset() {
		features.clear() ;
		targetFeature = null ;
	}
	
	final public void addFeature(String feature) {
		features.add(feature) ;
	}
	
	public String[] getFeatures() {
		return features.toArray(new String[features.size()]) ;
	}
	
	public String getTargetFeature() { return this.targetFeature ; }
	public void setTargetFeature(String s) { this.targetFeature = s ; }
}
