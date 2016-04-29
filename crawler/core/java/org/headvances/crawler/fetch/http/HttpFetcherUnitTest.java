package org.headvances.crawler.fetch.http;

import junit.framework.Assert;

import org.headvances.crawler.channel.ChannelGateway;
import org.headvances.crawler.fetch.FetchData;
import org.headvances.util.FileUtil;
import org.headvances.xhtml.site.SiteContextManager;
import org.headvances.xhtml.url.URLDatum;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.AbstractJUnit4SpringContextTests;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
/**
 * Author : Tuan Nguyen
 *          tuan.nguyen@headvances.com
 * Apr 22, 2010  
 */
@ContextConfiguration(
	locations={
		"classpath:/META-INF/in-memory-activemq.xml", 
		"classpath:/META-INF/crawler-fetcher.xml"
	}
)
@RunWith(SpringJUnit4ClassRunner.class)
public class HttpFetcherUnitTest extends AbstractJUnit4SpringContextTests {
  private SiteContextManager manager ;
  
  URLDatumFetchQueue urldatumFetchQueue = new URLDatumFetchQueue() ;
  
  @Autowired
  @Qualifier("FetchDatatGateway")
  private ChannelGateway fetchDataGateway ;
  
  @Autowired
  private SiteSessionManager siteSessionManager ;
  
  @Before
  public void setup() throws Exception {
  	manager = new SiteContextManager() ;
  	FileUtil.removeIfExist("target/db") ;
  }
       
  @After
  public void clean() throws Exception { 
    FileUtil.removeIfExist("target/db") ;
  }
  
  @Test
  public void testHttpFetcher() throws Exception {
  	manager = new SiteContextManager() ;
    manager.addCongfig("test.moom.vn",null, 3, "ok") ;
    HttpFetcher fetcher = 
    	new HttpFetcher(manager, siteSessionManager, urldatumFetchQueue, fetchDataGateway) ;
    
    URLDatum datum = new URLDatum(System.currentTimeMillis()) ;
    datum.setOrginalUrl("http://test.moom.vn/test/crawler/vnexpress/page1.html") ;
    FetchData data = fetcher.doFetch(datum) ;
    System.out.println(datum.getLastErrorCode()) ;
    Assert.assertEquals(52687, data.getDocument().getContent().length()) ;
  }
}