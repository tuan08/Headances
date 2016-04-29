package org.headvances.html.fetcher;

import org.headvances.data.Document;

/**
 * Author : Tuan Nguyen
 *          tuan.nguyen@headvances.com
 * Apr 25, 2010  
 */
public interface Fetcher {
  public Document fetch(String url) throws Exception ;
}
