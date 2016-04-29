package org.headvances.crawler.fetch.http;

import org.headvances.crawler.ErrorCode;
import org.headvances.crawler.ResponseCode;
import org.headvances.crawler.channel.ChannelGateway;
import org.headvances.crawler.fetch.FetchData;
import org.headvances.crawler.fetch.Fetcher;
import org.headvances.xhtml.site.SiteContextManager;
import org.headvances.xhtml.url.URLContext;
import org.headvances.xhtml.url.URLDatum;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Qualifier;
/**
 * Author: Tuan Nguyen
 *         tuan.nguyen@headvances.com
 * Apr 14, 2010  
 */
public class HttpFetcher implements Fetcher, Runnable {
  private static final Logger logger = LoggerFactory.getLogger(HttpFetcher.class);
  
  private SiteContextManager manager ;
  private URLDatumFetchQueue urldatumFetchQueue ;

  @Qualifier("FetchDatatGateway")
  private ChannelGateway fetchDataGateway ;
  
  private SiteSessionManager siteSessionManager ;
  private FetcherInfo fetcherInfo = new FetcherInfo() ;
  private boolean exit = false ;
  
  public HttpFetcher(SiteContextManager manager,
		                 SiteSessionManager siteSessionManager, 
		                 URLDatumFetchQueue urldatumFetchQueue, 
		                 ChannelGateway fetchDataGateway) {
	this.manager = manager ;
    this.urldatumFetchQueue = urldatumFetchQueue ;
    this.fetchDataGateway = fetchDataGateway ;
    this.siteSessionManager = siteSessionManager ;
  }
  
  public FetcherInfo getFetcherInfo() { return this.fetcherInfo ; } 
  
  public SiteContextManager getSiteConfigManager() { return this.manager ; }
  public void setSiteConfigManager(SiteContextManager manager) { this.manager = manager ; }
  
  public SiteSessionManager getSiteSessionManager() { return this.siteSessionManager ; }
  public void setSiteSessionManager(SiteSessionManager manager) { this.siteSessionManager = manager ; }
  
  public void fetch(URLDatum datum) throws Exception {
  	FetchData fdata = doFetch(datum) ;
  	if(fdata != null) fetchDataGateway.send(fdata) ;
  }
  
  public FetchData doFetch(URLDatum datum) {
    URLContext context = null; 
    try {
      context = manager.getURLContext(datum.getOriginalUrlAsString());
    } catch(Exception ex) {
    	ex.printStackTrace();
      datum.setLastErrorCode(ErrorCode.ERROR_DB_CONFIG_GET) ;
      fetcherInfo.log(datum) ;
      return new FetchData(datum) ;
    }
    if(context == null) {
      datum.setLastErrorCode(ErrorCode.ERROR_DB_CONFIG_NOT_FOUND) ;
      fetcherInfo.log(datum) ;
      return new FetchData(datum) ;
    }
    
    datum.setLastResponseCode(ResponseCode.NONE) ;
    datum.setLastErrorCode(ErrorCode.ERROR_TYPE_NONE) ;
    SiteSessions sessions = siteSessionManager.getSiteSession(context.getSiteContext()) ;
    SiteSession session = sessions.next() ;
    if(session.isLocked()) {
    	try {
	      urldatumFetchQueue.addBusy(datum) ;
      } catch (InterruptedException e) {
	      e.printStackTrace();
      }
    	return null ;
    }
    FetchData fdata = new FetchData(datum) ;
    session.fetch(fdata, context) ;
    fetcherInfo.log(datum) ;
    return fdata ;
  }

  public void setExit(boolean b) { this.exit = b ; }
  
  public void run() {
  	try {
      while(!exit) {
        URLDatum urldatum = urldatumFetchQueue.poll(1000) ;
      	if(urldatum != null) fetch(urldatum) ;
      }
    } catch(Throwable ex) {
      logger.error("Error when handling the fetched request", ex) ;
    }
  }
}