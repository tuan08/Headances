package org.headvances.search.webui;

/**
 * $Author: Tuan Nguyen$ 
 **/
public class FilterOptions {
	final static public FilterOptions INSTANCE  = new FilterOptions() ;
	
	private String[] contentTags = {
		"content:article", "content:blog", "content:forum",
		"content:classified", "content:product", "content:job",
		"content:other", "content:ignore", "clear",
	} ;
	
	private String[] languageTags = {
	  "lang:vi", "lang:en", "lang:other", "clear" 
	} ;
	
	private String[] pageTypeTags = {
	  "content:list", "content:detail", "content:unknown-type", "clear" 
	} ;
	
	private String[] suspectTags  = {
		"content:list:suspect", "content:detail:suspect",
		"content:article:suspect", "content:product:suspect",
		"clear"
  } ;
	
	private String[] otherTags  = {
		"content:comment", "content:predict:trust", "content:predict:untrust",
		"clear"
	} ;

	private String[] predictTags  = {
	    "content:predict:0.0-0.2", "content:predict:0.2-0.4",
	    "content:predict:0.4-0.6", "content:predict:0.6-0.8",
	    "content:predict:0.8-1.0",
	    "clear"
	} ;

	private String[] deeps  = {"1", "2", "3", "4", "5", "-1" } ;

	public String[] getContentTags() { return this.contentTags ; }
	
	public String[] getLanguageTags() { return this.languageTags ; }
	
	public String[] getPageTypeTags() { return this.pageTypeTags ; }

	public String[] getSuspectTags() { return this.suspectTags ; }
	
	public String[] getPredictTags() { return this.predictTags ; }
	
	public String[] getOtherTags() { return this.otherTags ; }
	
	public String[] getDeeps() { return this.deeps ; }

	public String getLabel(String tag) {
		return "filter.lb." + tag.replace(':', '.') ;
	}
}
