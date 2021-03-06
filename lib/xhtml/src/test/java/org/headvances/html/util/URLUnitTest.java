package org.headvances.html.util;

import org.headvances.util.html.URLNormalizer;
import org.headvances.util.html.URLNormalizerProcessor;
import org.junit.Assert;
import org.junit.Test;

public class URLUnitTest {
  @Test
  public void testURLNormalizer() throws Exception {
    String url1 = "http://www1.vnexpress.net:8080/GL/Xa-Hoi?p1=v1&amp;p2=v2&p1=v12#ref" ;
    URLNormalizer normURL = new URLNormalizer(url1) ;
    Assert.assertEquals("http://", normURL.getProtocol()) ;
    Assert.assertEquals("www.vnexpress.net", normURL.getHost()) ;
    Assert.assertEquals("8080", normURL.getPort()) ;
    Assert.assertEquals("/GL/Xa-Hoi", normURL.getPath()) ;
    Assert.assertEquals(
        "http://www.vnexpress.net:8080/GL/Xa-Hoi?p1=v1&p1=v12&p2=v2", 
        normURL.getNormalizeURL()) ;
    Assert.assertEquals("http://www.vnexpress.net:8080/GL", normURL.getBaseURL()) ;
    Assert.assertEquals(2, normURL.getPathSegments().size()) ;
//    String url2 = "http://www.vnexpress.net/GL/Xa-Hoi/ban-tin.html" ;
//    normURL = new URLNormalizer(url2) ;
//    Assert.assertEquals("http://www.vnexpress.net/GL/Xa-Hoi", normURL.getBaseURL().toString()) ;
    
    String url3 = "http://www.tinhte.com/threads/401253-Ro-ri-hinh-anh-duoc-cho-la-cua-Nokia-N98" ;
    normURL = new URLNormalizer(url3) ;
    Assert.assertEquals("/threads/401253-Ro-ri-hinh-anh-duoc-cho-la-cua-Nokia-N98", normURL.getPath()) ;
    
    String url4 = "http://test.moom.vn/test/crawler/zing/?Label=N;O=A" ;
    normURL = new URLNormalizer(url4) ;
    Assert.assertEquals("/test/crawler/zing/?Label=N;O=A", normURL.getPathWithParams()) ;

    String url5 = "http://test.moom.vn/query?Label=N;O=A" ;
    normURL = new URLNormalizer(url5) ;
    Assert.assertEquals("/query?Label=N;O=A", normURL.getPathWithParams()) ;
    Assert.assertEquals("http://test.moom.vn", normURL.getBaseURL()) ;
    
    String url6 = "http://test.moom.vn/test/crawler/" ;
    normURL = new URLNormalizer(url6) ;
    Assert.assertEquals("/test/crawler/", normURL.getPathWithParams()) ;
    Assert.assertEquals("http://test.moom.vn/test/crawler", normURL.getBaseURL()) ;
    
    String url7 = "http://test.moom.vn/test/crawler" ;
    normURL = new URLNormalizer(url7) ;
    Assert.assertEquals("/test/crawler", normURL.getPathWithParams()) ;
    Assert.assertEquals("http://test.moom.vn/test", normURL.getBaseURL()) ;
    
    String url8 = "http://anthinhi-c.com.vn?q=article/index.php" ;
    normURL = new URLNormalizer(url8) ;
    Assert.assertEquals("anthinhi-c.com.vn", normURL.getHost()) ;
    
    String nowwwId = 
    	new URLNormalizer("http://clc.vn/?act=detail&icatid=99&id=183&module=product").getHostMD5Id() ;
    String wwwId = 
    	new URLNormalizer("http://www.clc.vn/?act=detail&icatid=99&id=183&module=product").getHostMD5Id() ;
    Assert.assertEquals(nowwwId, wwwId) ;
  }
  
  @Test
  public void testURLRewriter() throws Exception {
    String siteURL = "http://test.moom.vn" ;
    String baseURL = "http://test.moom.vn/base/level1" ;
    URLRewriter rewriter = new URLRewriter() ;
    Assert.assertEquals(
        "http://test.moom.vn/absolute/path", 
        rewriter.rewrite(siteURL, baseURL, "/absolute/path")) ;
    Assert.assertEquals(
        "http://test.moom.vn/base/level1/relative/path", 
        rewriter.rewrite(siteURL, baseURL, "relative/path")) ;
    
    Assert.assertEquals(
        "http://vnexpress.net/external", 
        rewriter.rewrite(siteURL, baseURL, "http://vnexpress.net/external")) ;
    
    Assert.assertEquals(
        "http://test.moom.vn/base/level1/directory/?Label=N&A=0", 
        rewriter.rewrite(siteURL, baseURL, "directory/?Label=N&A=0")) ;
  }
  
  @Test
  public void testURLParamClean() throws Exception {
  	URLNormalizerProcessor[] URL_PROCESSORS = { new URLSessionIdCleaner()} ;
  	URLNormalizer urlnorm= 
    	new URLNormalizer("http://www.thegioicauca.com/4rum/viewtopic.php?f=16&p=468&sid=086f5383d38cc1bc7643be3f7cfb1196&t=68") ;
  	urlnorm.process(URL_PROCESSORS) ;
  	Assert.assertNull(urlnorm.getParams().get("sid"));
  	
  	urlnorm= 
    	new URLNormalizer("http://forum.nguyentrai.de/viewtopic.php?f=5&sid=cc05fb6e4020b3bb983e68c126b170de&t=417440") ;
  	urlnorm.process(URL_PROCESSORS) ;
  	Assert.assertNull(urlnorm.getParams().get("sid"));
  	
  	urlnorm= 
    	new URLNormalizer("http://www.thegioicauca.com/4rum/viewtopic.php?f=16&p=468&s=086f5383d38cc1bc7643be3f7cfb1196&t=68") ;
  	urlnorm.process(URL_PROCESSORS) ;
  	Assert.assertNull(urlnorm.getParams().get("s"));
  	
  	urlnorm= 
    	new URLNormalizer("http://www.thegioicauca.com/4rum/viewtopic.php?NVS=086f5383d38cc1bc7643be3f7cfb1196&t=68") ;
  	urlnorm.process(URL_PROCESSORS) ;
  	Assert.assertNull(urlnorm.getParams().get("NVS"));
  }
}