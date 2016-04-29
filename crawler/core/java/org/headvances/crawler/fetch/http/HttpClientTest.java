package org.headvances.crawler.fetch.http;

import org.apache.http.HttpResponse;
import org.junit.Test;
/**
 * Author : Tuan Nguyen
 *          tuan.nguyen@headvances.com
 * Apr 22, 2010  
 */
public class HttpClientTest {
  
  @Test
  public void testHttpFetcher() throws Exception {
    HttpClientSiteSessionImpl client = new HttpClientSiteSessionImpl("ddth.com") ;
    HttpResponse response = client.fetchURL("http://webtretho.com/forum/showthread.php?t=306595") ;
    HttpClientUtil.printResponseHeaders(response) ;
    System.out.println("status line= " + response.getStatusLine()) ;
    System.out.println("status code= " + response.getStatusLine().getStatusCode()) ;
    HttpClientUtil.printCookiStore(client.getCookieStore()) ;
    HttpClientUtil.printResponseBody(response) ;
  }
}