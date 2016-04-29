package org.headvances.html;

import org.headvances.data.Document;
import org.headvances.html.fetcher.URLConnectionFetcher;
import org.junit.Test;
/**
 * $Author: Tuan Nguyen$ 
 **/
public class ParserPerformanceUnitTest {
  @Test
  public void testTextNode() throws Exception {
  	String url = 
    	"http://vnexpress.net";
    
  	URLConnectionFetcher fetcher = new URLConnectionFetcher();
    Document hdoc = fetcher.fetch(url);
    String html = hdoc.getContent() ;
    html = new LSDomWriter().toXMLString(NekoParser.INSTANCE.parseNonWellForm(hdoc.getContent())) ;
    //html = HtmlCleanerParser.INSTANCE.reformat(html) ;
    System.out.println(html);
    long start = System.currentTimeMillis() ;
    for(int i = 0; i < 10000; i++) {
    	NekoParser.INSTANCE.parseNonWellForm(html) ;
    }
    long finish = System.currentTimeMillis() ;
    System.out.println("Exec in: " + (finish - start));
  }
}