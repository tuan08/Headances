package org.headvances.search.rest.webmonitor;

import java.util.List;

import org.headvances.ml.nlp.opinion.Opinion;
import org.headvances.util.ListPageList;
import org.headvances.util.PageList;
/**
 * $Author: Tuan Nguyen$ 
 **/
public class Opinions {
	private int available ;
	private int availablePage ;
	private int currentPage ;
	private Opinion[] opinion = {} ;
	
	public Opinions() { } 
	
	public Opinions(List<Opinion> opinion, int page, int pageSize) throws Exception { 
		PageList<Opinion> pl = new ListPageList<Opinion>(pageSize, opinion) ;
		this.available = pl.getAvailable() ;
		this.availablePage = pl.getAvailablePage() ;
		this.currentPage = page ;
		if(this.currentPage > this.availablePage) this.currentPage = 1 ;
		List<Opinion> list = pl.getPage(currentPage) ;
		this.opinion = list.toArray(new Opinion[list.size()]) ;
	}

	public int  getAvailable() { return available; }
	public void setAvailable(int available) { this.available = available; }

	public int  getAvailablePage() { return availablePage; }
	public void setAvailablePage(int availablePage) { this.availablePage = availablePage; }

	public int  getCurrentPage() { return currentPage; }
	public void setCurrentPage(int currentPage) { this.currentPage = currentPage; }

	public Opinion[] getOpinion() { return opinion; }
	public void      setOpinion(Opinion[] opinion) { this.opinion = opinion; }
}