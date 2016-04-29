package org.headvances.xhtml.url;

import java.util.Iterator;
import java.util.Map;

import org.headvances.crawler.fetch.FetchData;
import org.headvances.crawler.fetch.http.HttpClientSiteSessionImpl;
import org.headvances.html.dom.TDocument;
import org.headvances.util.html.URLNormalizer;
import org.headvances.xhtml.site.SiteConfig;
import org.headvances.xhtml.site.SiteContext;
import org.headvances.xhtml.url.URLContext;
import org.headvances.xhtml.url.URLDatum;
import org.headvances.xhtml.url.URLExtractor;
import org.junit.Test;

/**
 * $Author: Tuan Nguyen$ 
 **/
public class URLExtractorUnitTest {
	@Test
	public void testUnit() throws Exception {
		String[] urls = extract("http://nganphogallery.net") ;
		for(int i = 0; i < urls.length; i++) {
			System.out.println(urls[i]);
		}
	}

	private String[] extract(String url) throws Exception {
		URLContext urlcontext = createURLContext(url) ;
		HttpClientSiteSessionImpl client = 
			new HttpClientSiteSessionImpl(urlcontext.getSiteContext().getSiteConfig().getHostname()) ;
		URLDatum urldatum = new URLDatum() ;
		urldatum.setOriginalUrl(url, urlcontext.getUrlNormalizer()) ;
		FetchData fdata = new FetchData(urldatum); 
		client.fetch(fdata, urlcontext) ;
		
		String html = fdata.getDocument().getContent() ;
		TDocument tdoc = new TDocument("", url, html) ;
		URLExtractor extractor = new URLExtractor() ;
		urlcontext = createURLContext(urldatum.getFetchUrl()) ;
		
		Map<String, URLDatum> urls = extractor.extract(urldatum, urlcontext, tdoc) ;
		String[] array = new String[urls.size()] ;
		Iterator<URLDatum> i = urls.values().iterator() ;
		int idx = 0 ;
		while(i.hasNext()) {
			array[idx++] = i.next().getOriginalUrlAsString() ;
		}
		return array ;
	}
	
	private URLContext createURLContext(String url) {
		URLNormalizer urlnorm = new URLNormalizer(url);
		SiteConfig config = new SiteConfig() ;
		config.setHostname(urlnorm.getNormalizeHostName()) ;
		config.setInjectUrl(new String[] {url}) ;
		config.setCrawlDeep(3) ;
		SiteContext context = new SiteContext(config) ;
		URLContext urlcontext = new URLContext(urlnorm, context) ;
		return urlcontext ;
	}
}