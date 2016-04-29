package org.headvances.search.rest;

import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;

public class Query {
	private String   index = "web" ;
	
	@NotNull
	@Size(min=0, max=200)
	private String  query;
	private boolean title = true;
	private boolean description = true; 
	private boolean content = true;
	
	private String[] contentType = null ;
	private String pageType = "all" ;
	private int    page = 1;
	
	public String getIndex() { return this.index ; }
	public void   setIndex(String index) { this.index = index; }
	
	public String getQuery() { return query; }
	public void   setQuery(String q) { this.query = q; }

	public boolean getTitle() { return title; }
	public void    setTitle(boolean title) { this.title = title; }
	
	public boolean getDescription() { return description; }
	public void    setDescription(boolean description) { this.description = description; }
	
	public boolean getContent() { return content; }
	public void    setContent(boolean content) { this.content = content; }

	public String[] getContentType() { return this.contentType ; }
	public void   setContentType(String[] type) { this.contentType = type ; }

	public String getPageType() { return this.pageType ; }
	public void   setPageType(String type) { this.pageType = type ; }

	public int    getPage() { return this.page ; }
	public void   setPage(int page) { this.page = page ; }
}