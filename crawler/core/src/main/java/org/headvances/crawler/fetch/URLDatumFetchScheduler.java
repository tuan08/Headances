package org.headvances.crawler.fetch;

import java.util.Iterator;
import java.util.LinkedList;
import java.util.Map;

import org.apache.hadoop.io.Text;
import org.headvances.crawler.master.URLDatumCommitInfo;
import org.headvances.crawler.master.URLDatumScheduleInfo;
import org.headvances.util.html.URLNormalizer;
import org.headvances.util.text.DateUtil;
import org.headvances.util.text.TextTabularFormatter;
import org.headvances.xhtml.site.SiteConfig;
import org.headvances.xhtml.site.SiteContext;
import org.headvances.xhtml.site.SiteContextManager;
import org.headvances.xhtml.url.Segment;
import org.headvances.xhtml.url.URLDatum;
import org.headvances.xhtml.url.URLDatumDB;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jmx.export.annotation.ManagedAttribute;
import org.springframework.jmx.export.annotation.ManagedOperation;
import org.springframework.jmx.export.annotation.ManagedResource;
import org.springframework.stereotype.Component;
/**
 * $Author: Tuan Nguyen$ 
 **/
@Component
@ManagedResource(
	objectName="org.headvances.crawler.fetch:name=URLDatumFetchScheduler", 
	description="This bean is responsible to schedule the urls and commit the downloaded urls"
)
public class URLDatumFetchScheduler {
	private static final Logger logger = LoggerFactory.getLogger(URLDatumPreFetchScheduler.class);
	final static int MAX_HISTORY =  100 ;
	
	@Autowired
	private URLDatumPreFetchScheduler  preFetchScheduler ;
	@Autowired
	private URLDatumPostFetchScheduler postFetchScheduler ;

	private boolean      exist = false ;
	private ManageThread manageThread ;
	private LinkedList<URLDatumScheduleInfo> urldatumScheduleInfos ;
	private LinkedList<URLDatumCommitInfo>   urldatumCommitInfos ;
	private String state = "INIT";
	
	public URLDatumFetchScheduler() {
		urldatumScheduleInfos = new LinkedList<URLDatumScheduleInfo>() ;
		urldatumCommitInfos = new LinkedList<URLDatumCommitInfo>() ;
	}
	
	@ManagedAttribute(
		description="The state of the bean, the possible value are: " + 
		            "INIT, STARTING, STOPPING, SCHEDULING, COMMITING"
  )
	public String getState() { return this.state ; }
	
	@ManagedOperation(description="URLDatum Schedule info as formatted text")
  public String formatURLDatumScheduleInfosAsText() {
  	String string = 
  		"The frequency of time this bean is invoked, The frequency of time this bean is invoked. \n" +
  		"The frequency of time this bean is invoked, The frequency of time this bean is invoked. \n" +
  		"The frequency of time this bean is invoked, The frequency of time this bean is invoked. \n" +
  		"The frequency of time this bean is invoked, The frequency of time this bean is invoked. \n" +
  		"The frequency of time this bean is invoked, The frequency of time this bean is invoked. \n" +
  		"The frequency of time this bean is invoked, The frequency of time this bean is invoked.";
		return string ; 
  }
	
	public LinkedList<URLDatumScheduleInfo> getURLDatumScheduleInfos() { 
		return this.urldatumScheduleInfos ; 
	}
	
	@ManagedOperation(description="URLDatum Commit info as formatted text")
  public String formatURLDatumCommitInfosAsText() {
		String[] header = {
			"Time", "Exec Time", "Commit URL", "New URL", "URL List", "URL Detail"
		} ;
		int[] width = { 20, 15,  15, 15, 15, 15 } ;
		TextTabularFormatter formatter = new TextTabularFormatter(width) ;
		formatter.printHeader(header) ;
		Iterator<URLDatumCommitInfo> i = urldatumCommitInfos.iterator() ;
		while(i.hasNext()) {
			URLDatumCommitInfo sel = i.next() ;
			formatter.printRow(
				DateUtil.asCompactDateTime(sel.getTime()), sel.getExecTime(), sel.getURLCommitCount(),
				sel.getNewURLFoundCount(), sel.getNewURLTypeList(), sel.getNewURLTypeDetail()
			);
		}
		return formatter.getFormattedText() ; 
  }
	
	public LinkedList<URLDatumCommitInfo> getURLDatumCommitInfos() { 
		return this.urldatumCommitInfos ; 
	}
	
	synchronized public void start() {
    logger.info("start URLDatumFetchScheduler!!!!!!!!!!!!!!!!!!!!!!!") ;
    if(manageThread != null && manageThread.isAlive()) {
    	exist = false;
    	return ;
    }
    manageThread = new ManageThread() ;
    manageThread.setName("crawler.master.fetch-manager") ;
    state = "STARTING" ;
    manageThread.start() ;
    logger.info("Create a new thread and start URLDatumFetchScheduler!") ;
  }
  
  synchronized public void stop() {
    logger.info("stop URLDatumFetchScheduler!!!!!!!!!!!!!!!!!!!!!!!") ;
    state = "STOPPING" ;
    if(manageThread == null) return ;
    if(manageThread.isAlive()) {
    	exist = true ;
    	logger.info("set manage thread to exist for URLDatumFetchScheduler") ;
    }
  }
	
	public void run() {
		try {
    	long lastUpdateDB = 0l ;
    	long updatePeriod =  1 * 24 * 3600 * 1000l ;
    	URLDatumCommitInfo commitInfo = null ;
    	while(!exist) {
    		if(commitInfo == null || commitInfo.getURLCommitCount() > 0) {
    			state = "SCHEDULING" ;
    			URLDatumScheduleInfo sheduleInfo = preFetchScheduler.schedule() ;
    			urldatumScheduleInfos.addFirst(sheduleInfo) ;
    			if(urldatumScheduleInfos.size() >= MAX_HISTORY) urldatumScheduleInfos.removeLast() ;
    		}
    		state = "COMMITING" ; 
        commitInfo = postFetchScheduler.process() ;
        urldatumCommitInfos.addFirst(commitInfo) ;
        if(urldatumCommitInfos.size() >=  MAX_HISTORY) urldatumCommitInfos.removeLast() ;
        
//        if(lastUpdateDB + updatePeriod < System.currentTimeMillis()) {
//        	URLDatumDB urldatumDB = postFetchScheduler.getURLDatumDB() ;
//        	URLDatumDBUpdater updater = new URLDatumDBUpdater(postFetchScheduler.getSiteConfigManager()) ;
//        	urldatumDB.update(updater) ;
//        	logger.info("\n" + updater.getUpdateInfo()) ;
//        	lastUpdateDB = System.currentTimeMillis() ;
//        }
        Thread.sleep(500) ;
      }
    } catch(Throwable ex) {
      logger.error("URLDatumgFetchScheduler Error", ex) ;
    }
	}
	
	public void injectURL() throws Exception {
		URLDatumDB urlDatumDB = postFetchScheduler.getURLDatumDB() ;
		Segment<Text, URLDatum> segment = urlDatumDB.newSegment() ;
    Segment<Text, URLDatum>.Writer writer = segment.getWriter() ;
    long currentTime = System.currentTimeMillis() ;
    int count = 0 ;

    SiteContextManager siteConfigManager = postFetchScheduler.getSiteConfigManager() ;
    Iterator<Map.Entry<String, SiteContext>> i = siteConfigManager.getSiteConfigContexts().entrySet().iterator() ;
    while(i.hasNext()) {
    	Map.Entry<String, SiteContext> entry = i.next() ;
    	SiteContext context = entry.getValue() ;
    	String status = context.getSiteConfig().getStatus() ;
    	if(!SiteConfig.STATUS_OK.equals(status)) continue ;
      String[] url = context.getSiteConfig().getInjectUrl() ;
      for(String selUrl : url) {
        selUrl = selUrl.trim() ;
        if(selUrl.length() == 0) continue ;
        URLNormalizer newURLNorm = new URLNormalizer(selUrl) ;
        URLDatum datum = new URLDatum(currentTime) ;
        datum.setDeep((byte) 1) ;
        datum.setOriginalUrl(selUrl, newURLNorm) ;
        datum.setPageType(URLDatum.PAGE_TYPE_LIST) ;
        writer.append(datum.getId(), datum);
        count++ ;
      }
    }
    writer.close() ;
    urlDatumDB.autoCompact() ;
    logger.info("inject/update " + count + " urls") ;
  }
	
	public class ManageThread extends Thread {
		public void run() { URLDatumFetchScheduler.this.run() ; }
  }
}