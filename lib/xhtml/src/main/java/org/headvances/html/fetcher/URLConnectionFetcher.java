package org.headvances.html.fetcher;

import java.net.URI;
import java.net.URL;
import java.net.URLConnection;

import org.headvances.data.Document;
import org.headvances.data.HtmlDocumentUtil;
import org.headvances.util.IOUtil;
/**
 * Author : Tuan Nguyen
 *          tuan.nguyen@headvances.com
 * Apr 25, 2010  
 */
public class URLConnectionFetcher implements Fetcher {
  public Document fetch(String urlString) throws Exception {
    urlString = URLEncoder.encode(urlString);
    URL url = new URI(urlString).normalize().toURL();
    URLConnection uc = url.openConnection();
    uc.setReadTimeout(200000);

    Document hdoc = new Document() ;
    HtmlDocumentUtil.setHtmlLink(hdoc, "No Anchor Text", urlString, 1, null);
    hdoc.setContent(IOUtil.getStreamContentAsString(uc.getInputStream(), "UTF-8")) ;
    return hdoc ;
  }
}