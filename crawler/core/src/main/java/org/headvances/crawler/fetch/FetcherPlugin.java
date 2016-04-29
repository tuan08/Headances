package org.headvances.crawler.fetch;

import org.headvances.xhtml.url.URLContext;
import org.headvances.xhtml.url.URLDatum;

/**
 * Author : Tuan Nguyen
 *          tuan.nguyen@headvances.com
 * May 4, 2010  
 */
public interface FetcherPlugin {
  public void preFetch(URLContext context,  URLDatum urldatum, long atTime) ;
  public void postFetch(URLContext context, URLDatum urldatum, long atTime) ;
}
