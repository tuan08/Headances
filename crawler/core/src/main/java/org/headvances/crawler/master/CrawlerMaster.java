package org.headvances.crawler.master;

import javax.annotation.PostConstruct;
import javax.annotation.PreDestroy;

import org.headvances.crawler.fetch.URLDatumFetchScheduler;
import org.headvances.xhtml.site.SiteContextManager;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.xml.XmlBeanDefinitionReader;
import org.springframework.context.ApplicationContext;
import org.springframework.context.support.GenericApplicationContext;
import org.springframework.stereotype.Service;
/**
 * Author : Tuan Nguyen
 *          tuan.nguyen@headvances.com
 * Apr 21, 2010  
 */
@Service("CrawlerMaster")
public class CrawlerMaster {
	private static ApplicationContext applicationContext ;
  private static final Logger logger = LoggerFactory.getLogger(CrawlerMaster.class);

  @Autowired
  private URLDatumFetchScheduler urldatumFetchScheduler ;
  @Autowired
  private SiteContextManager siteConfigManager ;
  private CrawlerMasterInfo crawlerMasterInfo ;
  private boolean startOnInit = false ; 
  
  public SiteContextManager getSiteConfigManager() { return this.siteConfigManager ; }
  
  public URLDatumFetchScheduler getURLDatumFetchScheduler() { return this.urldatumFetchScheduler ; }
  
  public CrawlerMasterInfo getCrawlerMasterInfo()  { return this.crawlerMasterInfo ; }
  
  public void setStartOnInit(boolean b) { this.startOnInit = b ;  }
  
  @PostConstruct
  public void onInit() throws Exception {
    this.crawlerMasterInfo = new CrawlerMasterInfo(true) ;
    this.crawlerMasterInfo.setStartTime(System.currentTimeMillis()) ;
    logger.info("onInit(), initialize the CrawlerService environment");
  }
  
  @PreDestroy
  public void onDestroy() throws Exception {
  	stop() ;
    logger.info("onDestroy(), destroy the CrawlerService environment");
  }
  
  synchronized public void start() {
    logger.info("Start the CrawlerService!!!!!!!!!!!!!!!!!!!!!!!") ;
    this.urldatumFetchScheduler.start() ;
    this.crawlerMasterInfo.setStatus(CrawlerMasterInfo.RUNNING_STATUS) ;
  }
  
  synchronized public void stop() {
  	this.urldatumFetchScheduler.stop() ;
  	logger.info("Stop the CrawlerService!!!!!!!!!!!!!!!!!!!!!!!") ;
  }
  
  static public ApplicationContext getApplicationContext() { return applicationContext ; }
  static public void setApplicationContext(ApplicationContext context) {
  	applicationContext = context ;
  }
  
  static public void run() throws Exception {
  	final GenericApplicationContext ctx = new GenericApplicationContext() ;
    XmlBeanDefinitionReader xmlReader = new XmlBeanDefinitionReader(ctx) ;
    String[] res = {
    	"classpath:/META-INF/crawler-master.xml",
    	"classpath:/META-INF/crawler-integration.xml",
    	"classpath:/META-INF/connection-factory-activemq.xml",
  		"classpath:META-INF/cluster.xml"
    } ;
    xmlReader.loadBeanDefinitions(res) ;
    ctx.refresh() ;
    ctx.registerShutdownHook() ;
    setApplicationContext(ctx) ;
  }
  
  static public void main(String[] args) throws Exception {
  	run() ;
  	Thread.currentThread().join() ;
  }
}