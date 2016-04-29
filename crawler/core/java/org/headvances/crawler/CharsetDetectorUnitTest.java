package org.headvances.crawler;

import info.monitorenter.cpdetector.io.ASCIIDetector;
import info.monitorenter.cpdetector.io.ByteOrderMarkDetector;
import info.monitorenter.cpdetector.io.CodepageDetectorProxy;
import info.monitorenter.cpdetector.io.JChardetFacade;
import info.monitorenter.cpdetector.io.ParsingDetector;
import info.monitorenter.cpdetector.io.UnicodeDetector;

import java.io.BufferedInputStream;
import java.io.InputStream;
import java.nio.charset.Charset;

import junit.framework.Assert;

import org.apache.http.HttpResponse;
import org.headvances.crawler.fetch.http.HttpClientSiteSessionImpl;
import org.junit.Test;

public class CharsetDetectorUnitTest {
  private CodepageDetectorProxy detector = CodepageDetectorProxy.getInstance() ;
  
  @Test
  public void test() throws Exception {
    detector.add(new ByteOrderMarkDetector()) ;
    detector.add(new ParsingDetector(true)) ;
    detector.add(JChardetFacade.getInstance()) ;
    detector.add(ASCIIDetector.getInstance()) ;
    detector.add(UnicodeDetector.getInstance()) ;

    HttpClientSiteSessionImpl client = new HttpClientSiteSessionImpl("moom.vn") ;
    Charset charset = detector.detectCodepage(new BufferedInputStream(client.fetchURL("http://www.baidu.com/").getEntity().getContent()), 0) ;
    Assert.assertEquals("GB2312", charset.toString()) ;
    
    charset = 
    	detector.detectCodepage(new BufferedInputStream(client.fetchURL("http://moom.vn").getEntity().getContent()), 0) ;
    Assert.assertEquals("UTF-8", charset.toString()) ;
    
    charset = detector.detectCodepage(new BufferedInputStream(client.fetchURL("http://www.kiwick.cz/").getEntity().getContent()), 0) ;
    Assert.assertEquals("windows-1250", charset.toString()) ;
    
    charset = detector.detectCodepage(new BufferedInputStream(client.fetchURL("http://www.saralee.co.jp/brands/kiwi.html").getEntity().getContent()), 0) ;
    Assert.assertEquals("Shift_JIS", charset.toString()) ;
    
    charset = detector.detectCodepage(new BufferedInputStream(client.fetchURL("http://www.thaiall.com/java/indexo.html").getEntity().getContent()), 0) ;
    Assert.assertEquals("x-windows-874", charset.toString()) ;
    
    charset = detector.detectCodepage(new BufferedInputStream(client.fetchURL("http://vietnamnet.vn").getEntity().getContent()), 0) ;
    Assert.assertEquals("UTF-8", charset.toString()) ;
    
    charset = detector.detectCodepage(new BufferedInputStream(client.fetchURL("http://vnexpress.net").getEntity().getContent()), 0) ;
    Assert.assertEquals("UTF-8", charset.toString()) ;
    
    HttpResponse response = client.fetchURL("http://webmuaban.com") ;
    InputStream is = response.getEntity().getContent() ;
    BufferedInputStream buff = new BufferedInputStream(is) ;
    
    charset = detector.detectCodepage(buff, 0) ;
    Assert.assertEquals("ISO-8859-1", charset.toString());
  }
}
