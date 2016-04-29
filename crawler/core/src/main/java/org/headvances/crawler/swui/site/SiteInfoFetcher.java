package org.headvances.crawler.swui.site;

import java.util.Iterator;
import java.util.Map;

import org.apache.http.HttpResponse;
import org.headvances.crawler.fetch.http.HttpClientSiteSessionImpl;
import org.headvances.crawler.fetch.http.HttpClientUtil;
import org.headvances.html.dom.TDocument;
import org.headvances.util.html.URLNormalizer;
import org.headvances.xhtml.site.SiteContext;
import org.headvances.xhtml.url.URLContext;
import org.headvances.xhtml.url.URLDatum;
import org.headvances.xhtml.url.URLExtractor;

public class SiteInfoFetcher implements Runnable {
  private SiteContext context ;
  private int selectedRow ;
  
  private String   url ;
  private String   headers ;
  private String   xhtml   ;
  private String[] extractUrls ;
  
  private Throwable error ;
  private String status = "INIT" ;
  
  public SiteInfoFetcher(int selectedRow, SiteContext context) {
    this.selectedRow = selectedRow ;
    this.context = context ;
  }
  
  public String getUrl() { return url; }
  
  public String getHeader() { return headers ; }

  public String getXhtml() { return xhtml ; }
  
  public String getStatus() { return status ; }
  
  public String[] getExtractedUrl() { return extractUrls ; }
  
  public SiteContext getSiteContext() { return context ; }
  
  public int getSelectedRow() { return selectedRow ; }
  
  public Throwable getError() { return error ; }
  
  public void doFetch() throws InterruptedException {
    new Thread(this).start();
  }
  
  public void run() {
    status = "FETCHING" ; 
    try {
    	url = context.getSiteConfig().getInjectUrl()[0] ;
      HttpClientSiteSessionImpl client = 
        new HttpClientSiteSessionImpl(context.getSiteConfig().getHostname()) ;
      HttpResponse response = client.fetchURL(url) ;
      headers = HttpClientUtil.getResponseHeaders(response) ;
      xhtml = HttpClientUtil.getResponseBody(response) ;
      extractUrls(url, xhtml) ;
    } catch(Throwable ex) {
      error = ex ;
    } finally {
      status = "FINISHED" ;
    }
  }
  
  private void extractUrls(String url, String xhtml) throws Exception {
  	URLExtractor extractor = new URLExtractor() ;
  	extractor.onInit() ;

  	URLNormalizer urlnorm = new URLNormalizer(url) ;
  	URLContext urlcontext = new URLContext(urlnorm, context) ;
  	URLDatum urldatum = new URLDatum() ;
  	urldatum.setDeep((byte)1) ;
  	urldatum.setOriginalUrl(urlnorm.getNormalizeURL(), urlnorm) ;
  	TDocument tdoc = new TDocument("", urlnorm.getNormalizeURL(), xhtml) ;
  	Map<String, URLDatum> urls = extractor.extract(urldatum, urlcontext, tdoc) ;
  	this.extractUrls = new String[urls.size()] ;
  	Iterator<URLDatum> i = urls.values().iterator() ;
  	int idx = 0;
  	while(i.hasNext()) {
  		this.extractUrls[idx++] = i.next().getFetchUrl();
  	}
  }
}
