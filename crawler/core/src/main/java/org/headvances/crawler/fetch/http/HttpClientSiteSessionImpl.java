package org.headvances.crawler.fetch.http;

import java.io.ByteArrayInputStream;
import java.io.InputStream;
import java.net.ConnectException;
import java.net.SocketTimeoutException;
import java.net.URISyntaxException;
import java.net.UnknownHostException;
import java.nio.charset.Charset;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.net.ssl.SSLPeerUnverifiedException;

import org.apache.http.Header;
import org.apache.http.HttpResponse;
import org.apache.http.StatusLine;
import org.apache.http.client.CookieStore;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.protocol.HttpClientContext;
import org.apache.http.conn.ConnectTimeoutException;
import org.apache.http.cookie.Cookie;
import org.apache.http.impl.client.BasicCookieStore;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.protocol.BasicHttpContext;
import org.headvances.crawler.ErrorCode;
import org.headvances.crawler.ResponseCode;
import org.headvances.crawler.fetch.FetchData;
import org.headvances.data.Document;
import org.headvances.data.HtmlDocumentUtil;
import org.headvances.util.IOUtil;
import org.headvances.util.text.StringUtil;
import org.headvances.xhtml.url.URLContext;
import org.headvances.xhtml.url.URLDatum;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
/**
 * Author : Tuan Nguyen
 *          tuan.nguyen@headvances.com
 * Apr 23, 2010  
 */
public class HttpClientSiteSessionImpl implements SiteSession {
  private static final Logger logger = LoggerFactory.getLogger(HttpClientSiteSessionImpl.class);
  
  private String              hostname ;
  private CloseableHttpClient httpclient ;
  private CookieStore         cookieStore ;
  private ErrorCheckCondition errorCheckCondition ;
  private boolean             lock = false ;
  private boolean             destroy = false ;
  private int                 getpage = 0 ;
  
  public HttpClientSiteSessionImpl(String hostname) {
    this.hostname = hostname ;
    this.httpclient = HttpClientFactory.getInstance() ;
    this.cookieStore = new BasicCookieStore();
  }

  public String getHostname() { return this.hostname ; }
  
  public CookieStore getCookieStore() { return this.cookieStore ; }
  
  public List<Cookie> getCookies() { return cookieStore.getCookies() ; }
  
  public boolean isLocked() { return lock  ; }
  
  synchronized public void fetch(FetchData fdata, URLContext context)  {
    if(errorCheckCondition != null) {
      if(errorCheckCondition.isExpired()) {
        errorCheckCondition = null ;
      } else {
        errorCheckCondition.handle(fdata, context) ;
        return ;
      }
    }
    try {
    	lock = true ;
      long startTime = System.currentTimeMillis() ;
      URLDatum urldatum = fdata.getURLDatum() ;
      String fetchUrl = urldatum.getFetchUrl();
      HttpGet httpget = new HttpGet(fetchUrl); 
      BasicHttpContext httpContext = new BasicHttpContext();
      httpContext.setAttribute("crawler.site", this.hostname) ;
      httpContext.setAttribute(HttpClientContext.COOKIE_STORE, cookieStore);
      HttpResponse response = httpclient.execute(httpget, httpContext);
      String redirectUrl = (String)httpContext.getAttribute("url.redirect") ;
      if(redirectUrl != null) {
      	urldatum.setRedirectUrl(redirectUrl) ;
      }
      Document hdoc = new Document() ;
      hdoc.setId(urldatum.getId().toString()) ;
      String url = urldatum.getOriginalUrlAsString() ;
      int deep = urldatum.getDeep() ;
      HtmlDocumentUtil.setHtmlLink(hdoc, urldatum.getAnchorTextAsString(), url, deep, null) ;
  		
      copyHeaders(hdoc, response) ;
      
      hdoc.setContentType(HttpClientUtil.getContentType(response)) ;
      StatusLine sline = response.getStatusLine() ;
      urldatum.setLastResponseCode((short)sline.getStatusCode()) ;
      urldatum.setContentType(hdoc.getContentType()) ;
      handleContent(context, urldatum, hdoc, response);
      
      long downloadTime = System.currentTimeMillis() - startTime ;
      fdata.setDownloadTime(downloadTime) ;
      fdata.setDocument(hdoc) ;
    } catch(Throwable t) {
    	handleError(fdata, context, getRootCause(t)) ;
    } finally {
      httpclient.getConnectionManager().closeExpiredConnections() ;
//      ThreadSafeClientConnManager mng = (ThreadSafeClientConnManager) httpclient.getConnectionManager() ;
//      int connectionInPool = mng.getConnectionsInPool() ;
//      if(connectionInPool > (HttpClientFactory.MAX_HTTP_CONNECTION - 100)) {
//        //logger.warn("There are " + connectionInPool + " connection in pool") ;
//        mng.closeExpiredConnections() ;
//      }
      if(getpage == 0 && destroy) onDestroy() ;
      lock = false ;
    }
  }
  
  public HttpResponse fetchURL(String url) throws Exception {
    HttpGet httpget = new HttpGet(url);
    BasicHttpContext httpContext = new BasicHttpContext();
    httpContext.setAttribute("crawler.site", this.hostname) ;
    httpContext.setAttribute(HttpClientContext.COOKIE_STORE, cookieStore);
    HttpResponse response = httpclient.execute(httpget, httpContext);
    return response ;
  }
  
  public void onDestroy() {
    logger.info("destroy HttpClientSessionImpl") ;
  }
  
  public void destroy() {
    destroy = true; 
    if(getpage == 0) onDestroy() ;
  }

  public int compareTo(SiteSession other) {
    return hostname.compareTo(other.getHostname());
  }
  
  Throwable getRootCause(Throwable t) {
    Throwable parent = t.getCause() ;
    while(parent != null) {
      t = parent ;
      parent = t.getCause() ;
    }
    return t ;
  }
  
  void copyHeaders(Document hdoc, HttpResponse response) {
  	Header[] headers = response.getAllHeaders() ;
  	Map<String, String> headerHolder = new HashMap<String, String>() ;
  	for(int i = 0; i < headers.length; i++) {
  	  headerHolder.put(headers[i].getName(), headers[i].getValue()) ;
  	}
  	headerHolder.put("response-code", Integer.toString(response.getStatusLine().getStatusCode())) ;
  	HtmlDocumentUtil.setHttpResponseHeader(hdoc, headerHolder) ;
  }
  
  void handleContent(URLContext context, URLDatum datum, Document hdoc, HttpResponse response) throws Exception {
    InputStream is = response.getEntity().getContent() ;
    byte[] data = IOUtil.getStreamContentAsBytes(is, 300000) ;
    Charset encoding = StringUtil.UTF8 ;
    try {
    	ByteArrayInputStream bis = new ByteArrayInputStream(data) ;
    	encoding = EncodingDetector.getInstance().detect(bis, data.length) ;
    	bis.close() ;
    } catch(Throwable t) {
    	t.printStackTrace();
      logger.warn("Cannot detect the encoding for url {}, {}" , datum.getOriginalUrlAsString() , t.getMessage()) ;
    }
    is.close() ;

    if(data != null) datum.setLastDownloadDataSize(data.length) ;
    else datum.setLastDownloadDataSize(0) ;
    if(data != null) {
      String content = new String(data, encoding) ;
      data = content.getBytes(StringUtil.UTF8) ;
      hdoc.setContent(content) ;
    }
  }
  
  public String getResponseContent(HttpResponse response) throws Exception {
    InputStream is = response.getEntity().getContent() ;
    byte[] data = IOUtil.getStreamContentAsBytes(is, 300000) ;
    Charset encoding = StringUtil.UTF8 ;
    try {
    	ByteArrayInputStream bis = new ByteArrayInputStream(data) ;
    	encoding = EncodingDetector.getInstance().detect(bis, data.length) ;
    	bis.close() ;
    } catch(Throwable t) {
    	t.printStackTrace();
      logger.warn("Cannot detect the encoding {}"  , t.getMessage()) ;
    }
    is.close() ;

    if(data != null) {
      String content = new String(data, encoding) ;
      return content; 
    }
    return null ;
  }
  
  void handleError(FetchData fdata, URLContext context, Throwable error) {
    if(error instanceof URISyntaxException) {
      fdata.setResponseCode(ResponseCode.ILLEGAL_URI) ;
    } else if(error instanceof SSLPeerUnverifiedException) {
      fdata.getURLDatum().setLastErrorCode(ErrorCode.ERROR_CONNECTION_NOT_AUTHORIZED) ;
      fdata.setResponseCode(ResponseCode.UNKNOWN_ERROR) ;
    } else if(error instanceof SocketTimeoutException) {
      fdata.getURLDatum().setLastErrorCode(ErrorCode.ERROR_CONNECTION_SOCKET_TIMEOUT) ;
      fdata.setResponseCode(ResponseCode.UNKNOWN_ERROR) ;
    } else if(error instanceof UnknownHostException) {
      errorCheckCondition = new ConnectionCheckCondition(ErrorCode.ERROR_CONNECTION_UNKNOWN_HOST, 5 * 60 *1000) ;
      errorCheckCondition.handle(fdata, context) ;
    } else if(error instanceof ConnectTimeoutException) {
      //Cannot etablish the connection to the server
      errorCheckCondition = new ConnectionCheckCondition(ErrorCode.ERROR_CONNECTION_TIMEOUT, 5 * 60 *1000) ;
      errorCheckCondition.handle(fdata, context) ;
    } else if(error instanceof ConnectException) {
      ConnectException cex = (ConnectException) error ;
      if(cex.getMessage().indexOf("timed out") >= 0) {
        fdata.getURLDatum().setLastErrorCode(ErrorCode.ERROR_CONNECTION_TIMEOUT) ;
      } else {
        fdata.getURLDatum().setLastErrorCode(ErrorCode.ERROR_CONNECTION) ;
      }
      fdata.setResponseCode(ResponseCode.UNKNOWN_ERROR) ;
    } else {
      logger.error("Error For URL: " + fdata.getURLDatum().getOriginalUrlAsString(), error) ;
      fdata.setResponseCode(ResponseCode.UNKNOWN_ERROR) ; 
    }
  }
  
  static interface ErrorCheckCondition {
    public boolean isExpired() ;
    public void    handle(FetchData fdata, URLContext context) ;
  }
  
  static class ConnectionCheckCondition implements ErrorCheckCondition {
    private byte errorCode ;
    private long expiredAt ;
    
    ConnectionCheckCondition(byte errorCode, long checkPeriod) {
      this.errorCode = errorCode ;
      this.expiredAt = System.currentTimeMillis() + checkPeriod ;
    }
    
    public boolean isExpired() { return System.currentTimeMillis() > expiredAt ; }
    
    public void handle(FetchData fdata, URLContext context) {
      fdata.getURLDatum().setLastErrorCode(this.errorCode) ;
      fdata.setResponseCode(ResponseCode.UNKNOWN_ERROR) ;
    }
  }
}