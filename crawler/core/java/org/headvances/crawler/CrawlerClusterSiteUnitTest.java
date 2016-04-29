package org.headvances.crawler;

import java.util.Collection;
import java.util.Iterator;

import org.headvances.cluster.ClusterClient;
import org.headvances.cluster.task.TaskResult;
import org.headvances.crawler.cluster.task.ModifySiteContextTask;
import org.headvances.crawler.integration.DocumentConsumer;
import org.headvances.crawler.master.CrawlerMaster;
import org.headvances.jms.EmbededActiveMQServer;
import org.headvances.util.FileUtil;
import org.junit.Before;
import org.junit.Test;
import org.springframework.context.ApplicationContext;
/**
 * Author : Tuan Nguyen
 *          tuan.nguyen@headvances.com
 * Apr 22, 2010  
 */
public class CrawlerClusterSiteUnitTest {
  private CrawlerMaster  master;
  private CrawlerFetcher fetcher ;

  @Before
  public void setup() throws Exception { 
  	FileUtil.removeIfExist("target/urldb") ;
  	FileUtil.removeIfExist("target/activemq") ;
  	FileUtil.removeIfExist("target/crawler") ;
  	
  	EmbededActiveMQServer.run() ;
  	CrawlerMaster.run() ;
  	
  	ApplicationContext crawlerMasterContext = CrawlerMaster.getApplicationContext() ;
  	master = crawlerMasterContext.getBean(CrawlerMaster.class) ;
  	
  	CrawlerFetcher.run(); 
  	ApplicationContext crawlerFetcherContext = CrawlerFetcher.getApplicationContext() ;
  	fetcher  = crawlerFetcherContext.getBean(CrawlerFetcher.class) ;
  
  	DocumentConsumer.run() ;
  	
  	ClusterClient client = new ClusterClient("crawler", "crawler", "127.0.0.1:5700") ;
  	
  	ModifySiteContextTask task = new ModifySiteContextTask() ;
  	//task.add("kenh14.vn", "http://kenh14.vn/home.chn", 3, "ok") ;
  	//task.add("dantri.com.vn",  "http://dantri.com.vn", 2, 1, "ok") ;
  	//task.add("thegioididong.com",  "http://thegioididong.com", 2, 3, "ok") ;
  	task.add("vnexpress.net",  "http://vnexpress.net", 3, 1, "ok") ;
  	
  	Collection<TaskResult> results = client.execute(task) ;
  	Iterator<TaskResult> i = results.iterator() ;
  	while(i.hasNext()) {
  		TaskResult result = i.next() ;
  		System.out.println(result.getMember() + ": " + result.getMember());
  	}
  }

  @Test
  public void testSite() throws Exception {
    master.getURLDatumFetchScheduler().injectURL() ;
    
    master.start() ;
    fetcher.start() ;
    Thread.currentThread().join(5 * 60000) ;
    master.stop() ;
    
    Thread.sleep(5000) ;
    master.getCrawlerMasterInfo().report(System.out) ;
  }
}