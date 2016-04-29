package org.headvances.xhtml.url;

import java.util.Iterator;
import java.util.Map;
import java.util.TreeMap;

import junit.framework.Assert;

import org.apache.hadoop.io.Text;
import org.headvances.util.FileUtil;
import org.headvances.xhtml.site.SiteContextManager;
import org.headvances.xhtml.url.HDFSUtil;
import org.headvances.xhtml.url.MergeMultiSegmentIterator;
import org.headvances.xhtml.url.Segment;
import org.headvances.xhtml.url.URLDatum;
import org.headvances.xhtml.url.URLDatumDB;
import org.headvances.xhtml.url.URLDatumDBUpdater;
import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class URLDBTest {
	Logger logger = LoggerFactory.getLogger(URLDBTest.class) ;
  
	@Test
  public void test1() throws Exception {
  	String dblocation = "target/db" ;
  	FileUtil.removeIfExist(dblocation) ;
  	
    TreeMap<String, String> www = new TreeMap<String, String>() ;
    
    URLDatumDB db = new URLDatumDB(HDFSUtil.getDaultConfiguration(), dblocation) ;
    db.reload() ;
    MergeMultiSegmentIterator<Text, URLDatum> i = db.getMergeRecordIterator() ;
    int count = 0, dotcount = 0, wwwcount = 0 ;
    while(i.next()) {
    	++count ;
    	if(i.currentKey().toString().startsWith(".")) ++dotcount ;
    	if(i.currentKey().toString().startsWith("www.")) {
    		www.put(i.currentKey().toString(), i.currentValue().getOriginalUrlAsString()) ;
    		++wwwcount ;
    	}
    	if(count % 100000 == 0) {
    		logger.info("starts dot count = " + dotcount + " | count = " + count) ;
    		logger.info("starts www count = " + wwwcount + " | count = " + count) ;
    	}
    }
    logger.info("starts dot count = " + dotcount + " | count = " + count) ;
    logger.info("starts www count = " + wwwcount + " | count = " + count) ;
    
    Iterator<Map.Entry<String, String>> ite = www.entrySet().iterator() ;
    while(ite.hasNext()) {
    	Map.Entry<String, String> entry = ite.next() ;
    	System.out.println(entry.getKey() + " | " + entry.getValue() + "\n") ;
    }
  }
  
  @Test
  public void test2() throws Exception {
  	SiteContextManager siteConfigManager = new SiteContextManager() ;
  	siteConfigManager.onInit() ;
  	siteConfigManager.addCongfig("test.moom.vn", null, 5, "ok") ;

  	URLDatumDBUpdater updater = new URLDatumDBUpdater(siteConfigManager) ;
  	
  	String dblocation = "target/crawler/db" ;
    URLDatumDB db = new URLDatumDB(HDFSUtil.getDaultConfiguration(), dblocation) ;
    db.reload() ;
    Segment<Text, URLDatum> segment = db.newSegment() ;
    Segment<Text, URLDatum>.Writer writer = segment.getWriter() ;
    URLDatum datum = new URLDatum() ;
    datum.setOrginalUrl("http://www.test.moom.vn/test.jsp") ;
    datum.setId(new Text("www.test")) ;
    writer.append(datum.getId(), datum) ;
    writer.close() ;
    db.autoCompact() ;
    
  	db.update(updater) ;
  	logger.info("Update URLDatumDB, total  {} records", updater.getCount()) ;
  	logger.info("Update URLDatumDB, delete {} records", updater.getDeleteCount()) ;
  	logger.info("Update URLDatumDB, no config {} records", updater.getNoConfigCount()) ;
  	logger.info("Update URLDatumDB, ignore domain {} records", updater.getIgnoreDomainCount()) ;
  	logger.info("Update URLDatumDB, ignore page {} records", updater.getIgnorePageCount()) ;
  	logger.info("Update URLDatumDB, expire page list {} records", updater.getExpirePageListCount()) ;
  	logger.info("Update URLDatumDB, expire page detail {} records", updater.getExpirePageDetailCount()) ;
  	logger.info("Update URLDatumDB, rc 300 {} records", updater.getRC3xxCount()) ;
  	logger.info("Update URLDatumDB, rc 400 {} records", updater.getRC4xxCount()) ;
  	
  	Assert.assertEquals(1, updater.getDeleteCount()) ;
  	Assert.assertEquals(1, updater.getIgnoreDomainCount()) ;
  }
}