package org.headvances.search.rest;

import org.headvances.data.Document;
import org.headvances.data.Entity;
import org.headvances.data.HtmlDocumentUtil;
import org.headvances.search.TermHighlighter;

/**
 * $Author: Tuan Nguyen$ 
 **/
public class RecordDescription {
	private String id ;
	private String url ;
	private String title ;
	private String description ;
	private String bestMatch ;
	private int    index ;
	
	public RecordDescription() {} 
	
	public RecordDescription(Document doc, TermHighlighter highlighter) {
		this.id  = doc.getId() ;
		this.url = HtmlDocumentUtil.getHtmlLink(doc).getString("url") ;
		String anchorText = HtmlDocumentUtil.getHtmlLink(doc).getString("anchorText") ;
		
		Entity mainContent = doc.getEntity("mainContent") ;
		String mcontent = "" ;
		if(mainContent != null) {
			title = mainContent.getString("title") ;
			description = mainContent.getString("description") ;
			mcontent = mainContent.getString("content") ;
		}
		if(title == null) title = anchorText ;
		if(title == null) title = url ;
		bestMatch = highlighter.extract(description, mcontent, 2, 50) ;
		title = highlighter.getHighlightText(title) ;
		description = highlighter.getHighlightText(description) ;
	}
	
	public String getId() { return id; }
	public void setId(String id) { this.id = id; }
	
	public String getUrl() { return url; }
	public void setUrl(String url) { this.url = url; }
	
	public String getTitle() { return title; }
	public void setTitle(String title) { this.title = title; }
	
	public String getDescription() { return description; }
	public void setDescription(String description) { this.description = description; }
	
	public String getBestMatch() { return this.bestMatch ; }
	public void   setBestMatch(String s) { this.bestMatch = s ;}

	public int getIndex() { return this.index ; }
	public void setIndex(int index) { this.index = index ; }
}
