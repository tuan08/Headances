package org.headvances.ml.feature;

import java.util.Collection;
/**
 * $Author: Tuan Nguyen$ 
 **/
public interface FeatureHolder {
	public int    getDocId() ;
	public String getTargetLabel() ;

	public GenerateFeature add(String category, String feature) ;
	
	public void add(String prefix, Collection<String> features) ;
	
	public void add(String category, String[] features) ;
	
	public GenerateFeature[] getFeatures() ;
	
	public int size() ;
}