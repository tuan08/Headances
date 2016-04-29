package org.headvances.crawler;

import junit.framework.Assert;

import org.headvances.crawler.fetch.http.HttpFetcherManager;
import org.headvances.xhtml.url.URLDatum;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
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
public class CrawlerFetcherUnitTest extends AbstractJUnit4SpringContextTests {
  @Autowired
	private CrawlerFetcher crawlerFetcher ;
	
  @Autowired
  private HttpFetcherManager httpFetcherManager ;
  
  @Before
  public void setup() throws Exception {
  }
       
  @After
  public void clean() throws Exception { 
  }
  
  @Test
  public void testCrawlerFetcher() throws Exception {
  	Assert.assertNotNull(crawlerFetcher) ;
  	Assert.assertNotNull(httpFetcherManager) ;
  	for(int i = 0; i < 10; i++) {
  		URLDatum urldatum = new URLDatum(System.currentTimeMillis()) ;
  		urldatum.setOrginalUrl("http://test.moom.vn/test/crawler/vnexpress/page1.html") ;
  		httpFetcherManager.schedule(urldatum) ;
  	}
  	Thread.sleep(2000) ;
  }
}