package org.headvances.crawler.fetch.http;

import org.headvances.util.RoundRobinAllocatorAgent;

/**
 * Author : Tuan Nguyen
 *          tuan.nguyen@headvances.com
 * Jul 8, 2010  
 */
public class SiteSessions {
  final static int MAX_CONNECTION = 1 ;
  
  private RoundRobinAllocatorAgent<SiteSession> allocator ;
  
  public SiteSessions(String hostname, int maxConnection) {
    SiteSession[] sessions = new SiteSession[maxConnection] ;
    for(int i = 0; i < sessions.length; i++) {
      sessions[i] = new HttpClientSiteSessionImpl(hostname) ;
    }
    allocator = new RoundRobinAllocatorAgent<SiteSession>(sessions) ;
  }
  
  synchronized public SiteSession next() { return allocator.next() ; }
 
  public SiteSession[] getSiteSessions() { return this.allocator.getItems() ; }
  
  public void destroy() {
    for(SiteSession sel : getSiteSessions()) sel.destroy() ;
  }
}
