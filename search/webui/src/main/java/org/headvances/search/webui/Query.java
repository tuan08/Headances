package org.headvances.search.webui;

import java.util.Date;
import java.util.HashSet;

import javax.validation.constraints.Future;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;

import org.headvances.util.text.StringUtil;
import org.springframework.format.annotation.DateTimeFormat;

public class Query {
	private String   index = "web" ;
	private int      page  = 1 ;
	private int      deep  = -1 ;
	private String   site  = "" ;
	private HashSet<String> tags = new HashSet<String>() ;
	
	@NotNull
	@Size(min=0, max=200)
	private String  query;
	private boolean title = true;
	private boolean description = true; 
	private boolean content = true;
	
	@DateTimeFormat(style="S-")
	@Future
	private Date createdDate = new Date();

	public String getIndex() { return this.index ; }
	public void   setIndex(String index) { this.index = index; }
	
	public int    getPage() { return this.page ; }
	public void   setPage(int page) { this.page = page ; }
	
	public int  getDeep() { return this.deep ; }
	public void setDeep(int deep) { this.deep = deep ; }
	
	public String getSite() { return this.site ; }
	public void   setSite(String site) { this.site = site ; }

	public void addTag(String tag) { tags.add(tag) ; }

	public boolean hasTag(String tag) {
		return tags.contains(tag) ;
	}
	
	public String getTag(String tag) {
		if(tags.contains(tag)) return tag ;
		return null ;
	}
	
	public String getTags(String tag) {
		if(tags.contains(tag)) return tag ;
		return null ;
	}
	
	public void rmTag(String[] tag) { 
		for(String sel : tag) tags.remove(sel) ; 
	}
	
	public String getTags() { return StringUtil.joinStringCollection(this.tags, ","); }
	
	public String[] getTagsArray() { return tags.toArray(new String[tags.size()]) ; }
	
	public void setTags(String tags) {
		String[] array = StringUtil.toStringArray(tags) ;
		for(String sel : array) this.tags.add(sel) ;
	}
	
	public String getQuery() { return query; }
	public void   setQuery(String q) { this.query = q; }

	public boolean getTitle() { return title; }
	public void    setTitle(boolean title) { this.title = title; }
	
	public boolean getDescription() { return description; }
	public void    setDescription(boolean description) { this.description = description; }
	
	public boolean getContent() { return content; }
	public void    setContent(boolean content) { this.content = content; }
	
	public Date getCreatedDate()          { return createdDate; }
	public void setCreatedDate(Date date) { this.createdDate = date; }
}