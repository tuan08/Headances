package org.headvances.search.webui;

import org.headvances.data.Document;
import org.headvances.data.Entity;
import org.headvances.data.HtmlDocumentUtil;
import org.headvances.search.TermHighlighter;
import org.headvances.util.text.StringUtil;
/**
 * $Author: Tuan Nguyen$ 
 **/
public class SearchRecord {
	private String   id ;
	private String   url  ;
	private String   anchorText ;
	private String   title ;
	private String   description ;
	private String   content ;
	private String   commentTitle ;
	private String   commentContent ;
	private String   matchHighlight ;
	private String[] label ;
	private String[] tag ;
	
	public SearchRecord() { } 
	
	public SearchRecord(Document doc, TermHighlighter highlighter, boolean detailView) {
		this.id  = doc.getId() ;
		this.url = HtmlDocumentUtil.getHtmlLink(doc).getString("url") ;
		this.anchorText = HtmlDocumentUtil.getHtmlLink(doc).getString("anchorText") ;
		
		Entity mainContent = doc.getEntity("mainContent") ;
		if(mainContent != null) {
			title = mainContent.getString("title") ;
			description = mainContent.getString("description") ;
			content = mainContent.getString("content") ;
		}
		if(title == null) title = doc.getTitle() ;
		if(title == null) title = url ;
		title = highlighter.getHighlightText(title) ;
		
		if(detailView) {
			description = highlighter.getHighlightText(description) ;
			content = highlighter.getHighlightText(content) ;
			content = content.replaceAll("\n", "<br/>") ;
			
			Entity comment = doc.getEntity("commentContent") ;
			if(comment != null) {
				this.commentTitle   = highlighter.getHighlightText(comment.getString("title")) ;
				this.commentContent = highlighter.getHighlightText(comment.getString("content")) ;
				this.commentContent = this.commentContent.replaceAll("\n", "<br/>") ;
			}
		} else {
			matchHighlight = highlighter.extract(description, content, 2, 50) ;
		}
		this.label = doc.getLabels() ;
		this.tag = doc.getTags() ;
	}
	
	public String getId() { return this.id ; }
	
	public String getUrl() { return this.url ; }
	public String getDisplayUrl() { 
		if(url != null && url.length() > 60) {
			return url.substring(0, 60) + "..." ;
		}
		return this.url ; 
	}
	public void setUrl(String url) { this.url = url ; }
	
	public String getAnchorText() { return this.anchorText ; }
	
	public String getTitle() { 
		if(StringUtil.isEmpty(title)) return this.getDisplayUrl() ;
		return title; 
	}
	public void setTitle(String title) { this.title = title; }
	
	public String getDescription() { return description; }
	public void setDescription(String description) { this.description = description; }
	
	public String getContent() { return content; }
	public void setContent(String content) { this.content = content; }

	
	public String getCommentTitle() { return this.commentTitle ; }
	public String getCommentContent() { return this.commentContent ; }
	
	public String getMatchHighlight() { return this.matchHighlight ; }
	
	public String[] getLabels() { return this.label ; }
	
	public void updateLabel(LabelHistories histories) {
		String newLabel = histories.get(this.id) ;
		if(newLabel != null) {
			this.label = new String[] {newLabel} ;
		}
	}
	
	public String[] getTags() { return this.tag ; }
}
