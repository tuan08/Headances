package org.headvances.search.rest;

import org.headvances.data.Document;
import org.headvances.data.Entity;
import org.headvances.search.TermHighlighter;

/**
 * $Author: Tuan Nguyen$ 
 **/
public class CacheRecord extends RecordDescription {
	private String content ;

	public CacheRecord() {} 
	
	public CacheRecord(Document doc, TermHighlighter highlighter) {
		super(doc, highlighter) ;
		Entity mainContent = doc.getEntity("mainContent") ;
		if(mainContent != null) {
			this.content = mainContent.getString("content") ;
		}
		this.content = highlighter.getHighlightText(this.content) ;
		content = content.replaceAll("\n", "<br/>") ;
	}
	
	public String getContent() { return content; }
	public void setContent(String content) { this.content = content; }

	static CacheRecord mock() {
		CacheRecord record = new CacheRecord() ;
		record.setId("MockId") ;
		record.setUrl("mock url") ;
		record.setTitle("mock title") ;
		record.setDescription("mock description") ;
		record.setContent("mock content") ;
		return record ;
	}
}
