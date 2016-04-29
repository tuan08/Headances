package org.headvances.ml.feature;

import java.util.Arrays;
import java.util.HashSet;
import java.util.Set;
/**
 * $Author: Tuan Nguyen$ 
 **/
public class Labels {
	private Set<String> labels = new HashSet<String>() ;
	private String[] cache = null ;

	public String[] getLabels() { 
		if(cache == null) cache = labels.toArray(new String[labels.size()]) ;
		Arrays.sort(cache) ;
		return cache ; 
	}
	public void     setLabels(String[] labels) {
		cache = null ;
		for(String sel : labels) this.labels.add(sel) ;
	}

	public String getLabel(String label, boolean add) {
		if(labels.contains(label)) return label ;
		if(add) {
			cache = null ;
			labels.add(label) ;
			return label ;
		} else {
			return null ;
		}
	}

	public int getLabelId(String label) {
		String[] labels = this.getLabels() ;
		for(int i = 0; i < labels.length; i++) {
			if(label.equals(labels[i])) {
				return i ;
			}
		}
		throw new RuntimeException("Not Found Label " + label) ;
	}

	public void collect (String label) {
		if(labels.contains(label)) return  ;
		cache = null ;
		labels.add(label) ;
	}
}