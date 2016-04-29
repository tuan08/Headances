package org.headvances.search.rest.webmonitor;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Iterator;
import java.util.List;

import org.headvances.ml.nlp.opinion.Opinion;
import org.headvances.util.text.StringUtil;

/**
 * $Author: Tuan Nguyen$ 
 **/
public class OpinionFilter {
	private String entity ;
	private String contentType = "all";
	private String category    = "all";
	private String rank = null ;
	
	private List<Opinion> opinions = new ArrayList<Opinion>();

	public String getEntity() { return entity; }
	public void   setEntity(String entity) { this.entity = entity ; }
	
	public String getContentType() { return contentType; }
	public void   setContentType(String contentType) { this.contentType = contentType; }

	public String getCategory() { return category; }
	public void   setCategory(String category) { this.category = category; }

	public String getRank() { return this.rank ; }
	public void   setRank(String rank) { this.rank = rank ; }
	
	public void add(Collection<Opinion> collection) {
		Iterator<Opinion> i = collection.iterator() ;
		while(i.hasNext()) add(i.next()) ;
	}

	public void add(Opinion opinion) {
		if(!StringUtil.isEmpty(contentType) && !"all".equals(contentType)) {
			if(!StringUtil.isIn(contentType, opinion.getTag())) return ;
		}
		
		if(!StringUtil.isEmpty(category) && !"all".equals(category)) {
			if(!StringUtil.isIn(category, opinion.getCategory())) return ;
		}
		
		if(!StringUtil.isEmpty(rank) && !"all".equals(rank)) {
			if(!rank.equals(opinion.getLabel())) return ;
		}
		opinions.add(opinion) ;
	}

	public List<Opinion> opinions() { return opinions ; }

	public boolean equals(OpinionFilter filter) {
		if(StringUtil.compare(contentType, filter.contentType) != 0) return false ;
		if(StringUtil.compare(category, filter.category) != 0) return false ;
		if(StringUtil.compare(rank, filter.rank) != 0) return false ;
		return true ;
	}
}