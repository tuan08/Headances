package org.headvances.crawler.fetch;

import java.util.ArrayList;

import org.apache.hadoop.io.Text;
import org.headvances.crawler.channel.ChannelGateway;
import org.headvances.crawler.master.URLDatumScheduleInfo;
import org.headvances.util.MultiListHolder;
import org.headvances.xhtml.site.SiteContext;
import org.headvances.xhtml.site.SiteContextManager;
import org.headvances.xhtml.site.SiteScheduleStat;
import org.headvances.xhtml.url.MergeMultiSegmentIterator;
import org.headvances.xhtml.url.Segment;
import org.headvances.xhtml.url.URLContext;
import org.headvances.xhtml.url.URLDatum;
import org.headvances.xhtml.url.URLDatumDB;
import org.headvances.xhtml.url.URLDatumStatisticMap;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.jmx.export.annotation.ManagedAttribute;
import org.springframework.jmx.export.annotation.ManagedResource;
/**
 * Author : Tuan Nguyen
 *          tuan.nguyen@headvances.com
 * Apr 29, 2010  
 */
@ManagedResource(
	objectName="org.headvances.crawler.fetch:name=URLDatumPreFetchScheduler", 
	description="This bean is responsible to schedule the urls"
)
public class URLDatumPreFetchScheduler {
  private static final Logger logger = LoggerFactory.getLogger(URLDatumPreFetchScheduler.class);
  
  @Autowired
  protected URLDatumDB urlDatumDB ;
  
  @Autowired
  private SiteContextManager siteContextManager ;
  
  @Autowired
  @Qualifier("URLDatumFetchGateway")
  private ChannelGateway urldatumFetchGateway ;
  
  @Autowired
  protected FetcherPlugins fetcherPlugins ;
  private URLDatumFetchSchedulerVerifier  verifier = new URLDatumFetchSchedulerVerifier () ;
  private int maxSchedulePerSite = 50 ;
  private int scheduleCounter = 0;
  
  public void onInit() {
  }

  @ManagedAttribute(
    description="Maximum frequency of url can schedule per site. " + 
                "The real max frequency is the frequency of connection multiply by this frequency"
  )
  public int  getMaxSchedulePerSite() { return this.maxSchedulePerSite ; }
  @ManagedAttribute(
    description="set the maximum frequency of url can schedule per site.",
    defaultValue="5", currencyTimeLimit=20
  )
  public void setMaxSchedulePerSite(int max) { maxSchedulePerSite = max ; }
  
  @ManagedAttribute(description="The frequency of time this bean is invoked")
  public int getScheduleCounter() { return this.scheduleCounter ; }
   
  public URLDatumScheduleInfo schedule() throws Exception {
  	logger.info("Start scheduling the fetch request!!!!!!!!!!!!!!") ;
  	scheduleCounter += 1 ;
  	
  	MergeMultiSegmentIterator<Text, URLDatum> mitr = urlDatumDB.getMergeRecordIterator() ;
    Segment<Text, URLDatum>.Writer writer = null ;
    long currentTime = System.currentTimeMillis() ;
    int urlCount = 0;
    int errorCount = 0, delayScheduleCount = 0 ;
    int pendingCount = 0, expiredPendingCount = 0, waitingCount = 0, scheduleCount = 0 ;
    MultiListHolder<URLDatum> requestBuffer = new MultiListHolder<URLDatum>(300000) ;
    PriorityURLDatumHolder priorityUrlHolder = null ;
    while(mitr.next()) {
      urlCount++ ;
      URLDatum datum = mitr.currentValue() ;
      //In case The url has been schedule to fetch 6 hours ago. It could happen when the queue has problem
      //or the fetcher has problem and url datum is not updated, shedule to refetch.
      URLContext context = siteContextManager.getURLContext(datum.getOriginalUrlAsString()) ;
      if(context == null) {
        errorCount++ ;
        logger.info("Scheduler: URLContext for " + datum.getOriginalUrlAsString() + " is null !!!!!!!!!!!!!") ;
        continue ;
      }
      SiteContext siteContext = context.getSiteContext() ;
      siteContext.getAttribute(URLDatumStatisticMap.class).log(datum) ;
      boolean doFetch = false ;
      
      if(datum.getErrorCount() >= 3) continue ;
      
      if(currentTime > datum.getNextFetchTime()) {
        doFetch = true ;
        if(datum.getStatus() ==  URLDatum.STATUS_FETCHING) expiredPendingCount++ ;
      } else {
        if(datum.getStatus() ==  URLDatum.STATUS_FETCHING) {
        	pendingCount++ ;
        } else {
        	waitingCount++ ;
        }
      }
      if(!doFetch) continue ;
      SiteScheduleStat scheduleStat = siteContext.getAttribute(SiteScheduleStat.class) ;
      if(!scheduleStat.canSchedule(maxSchedulePerSite, siteContext)) {
      	delayScheduleCount++ ;
      	continue ;
      }
 
      if(priorityUrlHolder == null) {
      	priorityUrlHolder = 
      		new PriorityURLDatumHolder(siteContext, scheduleStat.getMaxSchedule(maxSchedulePerSite, siteContext), 3) ;
      } else if(priorityUrlHolder.getSiteConfigContext() != siteContext) {
      	if(requestBuffer.getCurrentSize() + priorityUrlHolder.getSize() > requestBuffer.getCapacity()) {
          if(writer == null) {
            Segment<Text, URLDatum> segment = urlDatumDB.newSegment() ;
            writer = segment.getWriter() ;
          }
          scheduleCount += flush(requestBuffer, writer) ;
        }
      	flushPriorityURLDatumHolder(priorityUrlHolder, requestBuffer) ;
      	delayScheduleCount += priorityUrlHolder.getDelayCount() ;
      	priorityUrlHolder = 
      		new PriorityURLDatumHolder(siteContext, scheduleStat.getMaxSchedule(maxSchedulePerSite, siteContext), 3) ;
      } 
      priorityUrlHolder.insert(datum) ;
    }
    
    flushPriorityURLDatumHolder(priorityUrlHolder, requestBuffer) ;
    if(priorityUrlHolder != null) delayScheduleCount += priorityUrlHolder.getDelayCount() ;
    if(requestBuffer.getCurrentSize() > 0) {
      if(writer == null) {
        Segment<Text, URLDatum> segment = urlDatumDB.newSegment() ;
        writer = segment.getWriter() ;
      }
      scheduleCount += flush(requestBuffer, writer) ;
    }
    if(writer != null) {
      writer.close() ;
      //urlDatumDB.autoCompact() ;
    }
    siteContextManager.onPostPreSchedule() ;
    logger.info(
      "Check {} urls, error {}, fetch pending {}, expired fetch pending {}, fetch waiting {}, schedule {}, delay schedule {}", 
      new Object[] {urlCount, errorCount, pendingCount, expiredPendingCount, waitingCount, scheduleCount, delayScheduleCount} 
    ) ;
    int checkCount = errorCount + pendingCount + waitingCount + scheduleCount + delayScheduleCount ;
    if(urlCount != checkCount) {
      logger.warn("The frequency of check url is " + checkCount + ", but frequency of url in the db is " + urlCount) ;
    }
    verifier.verify(logger, urlCount, waitingCount) ;

    long execTime = System.currentTimeMillis() - currentTime ;
    URLDatumScheduleInfo info = 
      new URLDatumScheduleInfo(currentTime, execTime, urlCount, scheduleCount, delayScheduleCount, pendingCount, waitingCount) ;
    return info ;
  }
  
  private void flushPriorityURLDatumHolder(PriorityURLDatumHolder holder, MultiListHolder<URLDatum> fRequestBuffer) throws Exception {
  	if(holder == null) return ;
  	for(URLDatum sel : holder.getURLDatum()) {
  		URLContext selUrlContext = siteContextManager.getURLContext(sel.getOriginalUrlAsString()) ;
  		fetcherPlugins.preFetch(selUrlContext, sel, System.currentTimeMillis()) ;
  		fRequestBuffer.add(selUrlContext.getUrlNormalizer().getHost(), sel) ;
  		SiteScheduleStat scheduleStat = selUrlContext.getSiteContext().getAttribute(SiteScheduleStat.class) ;
  		scheduleStat.addScheduleCount(1) ;
  	}
  }
  
  private int flush(MultiListHolder<URLDatum> urlDatumBuffer, Segment<Text, URLDatum>.Writer writer) throws Exception {
    MultiListHolder<URLDatum>.RandomIterator iterator = urlDatumBuffer.getRandomIterator() ;
    URLDatum datum = null ;
    int scheduleCount = 0 ;
    ArrayList<URLDatum> holder = new ArrayList<URLDatum>(100) ;
    while((datum = iterator.next()) != null) {
      writer.append(datum.getId(), datum) ;
      holder.add(datum) ;
      if(holder.size() == 100) {
        urldatumFetchGateway.send(holder) ;
        holder.clear() ;
      }
      scheduleCount++ ;
    }
    if(holder.size() > 0) {
      urldatumFetchGateway.send(holder) ;
      holder.clear() ;
    }
    urlDatumBuffer.assertEmpty() ;
    return scheduleCount ;
  }
}