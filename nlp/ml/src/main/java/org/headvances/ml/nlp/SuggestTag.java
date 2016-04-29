package org.headvances.ml.nlp;

import org.headvances.nlp.wtag.WTagBoundaryTag;

/**
 * $Author: Tuan Nguyen$ 
 **/
public class SuggestTag extends WTagBoundaryTag {
	final static public String TYPE = "suggest" ;
	
	public SuggestTag(String[] feature) {
		super(feature) ;
	}
	
	public SuggestTag(String feature) {
		super(feature) ;
	}
	
	public String getOType() { return TYPE ; }
}