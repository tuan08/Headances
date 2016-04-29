package org.headvances.search.rest.webmonitor;

import java.util.LinkedHashMap;
import java.util.List;

import org.headvances.ml.nlp.opinion.Opinion;
/** $Author: Tuan Nguyen$ **/
public class OpinionHolder extends LinkedHashMap<String, Opinion>{
	private OpinionFilter filter ;
	
	synchronized public boolean add(Opinion opinion, boolean overwrite) {
		String id = opinion.getId() ;
		if(id == null) {
			throw new RuntimeException("The opinion do not have an id!!!") ;
		}
		if(!overwrite && containsKey(id)) return false ;
		put(id, opinion) ;
		if(filter != null) filter.add(opinion) ;
		return true ;
	}
	
	synchronized public List<Opinion> getOpinions(OpinionFilter aFilter) {
		if(this.filter == null || !this.filter.equals(aFilter)) {
			filter = aFilter; 
			filter.add(values()) ;
		}
		return filter.opinions() ;
	}
}
