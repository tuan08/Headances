package org.headvances.html.fetcher;

import org.apache.http.HttpResponse;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.protocol.BasicHttpContext;
import org.headvances.data.Document;
import org.headvances.data.HtmlDocumentUtil;
import org.headvances.util.IOUtil;
import org.headvances.util.html.URLNormalizer;
/**
 * Author : Tuan Nguyen
 *          tuan.nguyen@headvances.com
 * Apr 25, 2010  
 */
public class HttpClientFetcher implements Fetcher {
	private DefaultHttpClient httpclient ;

	public HttpClientFetcher() {
		this.httpclient = HttpClientFactory.getInstance() ;
	}

	public Document fetch(String urlString) throws Exception {
		return fetch("No Anchor Text", urlString) ;
	}

	public Document fetch(String anchorText, String urlString) throws Exception {  
		HttpGet httpget = new HttpGet(urlString);
		BasicHttpContext httpContext = new BasicHttpContext();
		httpContext.setAttribute("crawler.site", new URLNormalizer(urlString).getHost()) ;
		HttpResponse response = httpclient.execute(httpget, httpContext);

		Document hdoc = new Document() ;
		HtmlDocumentUtil.setHtmlLink(hdoc, anchorText, urlString, 1, null);
		hdoc.setContent(IOUtil.getStreamContentAsString(response.getEntity().getContent(), "UTF-8")) ;
		return hdoc ;
	}
}