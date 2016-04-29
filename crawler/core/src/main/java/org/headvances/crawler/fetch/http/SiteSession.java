package org.headvances.crawler.fetch.http;

import org.headvances.crawler.fetch.FetchData;
import org.headvances.xhtml.url.URLContext;

/**
 * Author : Tuan Nguyen
 *          tuan.nguyen@headvances.com
 * Apr 22, 2010  
 */
public interface SiteSession extends Comparable<SiteSession> {
  public String getHostname() ;
  public boolean isLocked() ;
  public void fetch(FetchData fdata, URLContext context) ;
  public void destroy()  ;
}
