package org.headvances.crawler.fetch;

import java.io.Serializable;

import org.headvances.data.Document;
import org.headvances.xhtml.url.URLDatum;

/**
 * Author : Tuan Nguyen
 *          tuan.nguyen@headvances.com
 * Apr 14, 2010  
 */
public class FetchData implements Serializable {
  final static byte[] EMPTY = new byte[0] ;
  
	private URLDatum datum ;
	private Document document ;
	
  public FetchData() {} 
  
  public FetchData(URLDatum datum) {
    this.datum = datum ;
  }
  
  
  public URLDatum getURLDatum() { return this.datum ; }
  
  public Document getDocument() { return this.document ; }
  public void setDocument(Document doc) { this.document = doc ; } 
  
  public void setResponseCode(short code) { this.datum.setLastResponseCode(code) ; }
  
  public void setDownloadTime(long time) { this.datum.setLastFetchDownloadTime(time) ; }

}