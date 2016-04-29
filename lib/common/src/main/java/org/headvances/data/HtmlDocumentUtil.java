package org.headvances.data;

import java.util.Map;

/**
 * $Author: Tuan Nguyen$ 
 * 
 * TODO: replace All the HtmlDocument by Document class. Use this class to set the html link
 * and headers
 **/
public class HtmlDocumentUtil {
	final static public String ICONTENT  = "icontent" ;
	final static public String COMMENT   = "comment" ;
	final static public String MCONTENT = "mainContent";
	final static public String LOCATION  = "location" ;
	final static public String HTML_LINK = "htmlLink" ;
	final static public String HTTP_RESPONSE_HEADER = "httpResponseHeader" ;
	
	final static public String[] ENTITIES = {
		HTML_LINK, HTTP_RESPONSE_HEADER, LOCATION, ICONTENT, MCONTENT, COMMENT
	};
	
	static public void setHtmlLink(Document doc, String anchorText, String url, int deep, String[] tag) {
		Entity htmlLink = new Entity() ;
		htmlLink.add("anchorText", anchorText);
		htmlLink.add("url", url);
		htmlLink.add("deep", deep);
		htmlLink.add("tag", tag);
		doc.addEntity(HTML_LINK, htmlLink) ;
	}
	
	static public Entity getHtmlLink(Document doc) {
		return doc.getEntity(HTML_LINK) ;
	}
	
	static public void setHttpResponseHeader(Document doc, Map<String, String> headers) {
		Entity httpResponseHeader = new Entity() ;
		httpResponseHeader.putAll(headers) ;
		doc.addEntity(HTTP_RESPONSE_HEADER, httpResponseHeader) ;
	}
	
	static public Entity getHttpResponseHeader(Document doc) {
		return doc.getEntity(HTTP_RESPONSE_HEADER) ;
	}
	static public String getDomain(String url) {
    String string = url ;
    int idx = url.indexOf("://") ;
    if(idx > 0) {
      string = string.substring(idx + 3) ;
    }
    idx = string.indexOf("/") ;
    if(idx > 0) {
      string = string.substring(0, idx) ;
    }
    return string ;
  }
}
