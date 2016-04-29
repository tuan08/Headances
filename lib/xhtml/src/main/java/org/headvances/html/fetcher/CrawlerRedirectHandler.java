package org.headvances.html.fetcher;

import java.net.URI;

import org.apache.http.Header;
import org.apache.http.HttpResponse;
import org.apache.http.ProtocolException;
import org.apache.http.impl.client.DefaultRedirectHandler;
import org.apache.http.protocol.HttpContext;

public class CrawlerRedirectHandler extends DefaultRedirectHandler {
	private String currentSite ;
	
	public String getCurrentSite() { return this.currentSite ; }
	public void setCurrentSite(String site) { this.currentSite =  site ; }
	
	public URI getLocationURI(HttpResponse res, HttpContext context) throws ProtocolException {
		return super.getLocationURI(res, context);
	}

	public boolean isRedirectRequested(HttpResponse res, HttpContext context) {
		Header header = res.getFirstHeader("Location") ;
		//HttpClientUtil.printResponseHeaders(res) ;
		if(header != null) {
			String location = header.getValue() ;
			if(location.indexOf("://") > 0) {
				String site = (String)context.getAttribute("crawler.site") ;
				int idx = location.indexOf("://") ;
				String hostName = location.substring(idx + 3) ;
				idx = hostName.indexOf('/') ;
				if(idx > 0) {
					hostName = hostName.substring(0, idx) ;
				}
				if(!hostName.endsWith(site)) return false ;
			}
		}
		return super.isRedirectRequested(res, context);
	}

}
