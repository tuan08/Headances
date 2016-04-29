package org.headvances.xhtml.url;

import org.apache.hadoop.io.Text;
import org.headvances.util.FileUtil;
import org.headvances.xhtml.url.HDFSUtil;
import org.headvances.xhtml.url.Segment;
import org.headvances.xhtml.url.URLDatum;
import org.headvances.xhtml.url.URLDatumDB;
import org.junit.After;
import org.junit.Test;
/**
 * Author : Tuan Nguyen
 *          tuan.nguyen@headvances.com
 * Apr 19, 2010  
 */
public class URLRecordDBUnitTest {
  @After
  public void clean() throws Exception { 
    FileUtil.removeIfExist("target/db") ;
  }
  
  @Test
  public void test() throws Exception {
    String dblocation = "target/db" ;
    FileUtil.removeIfExist(dblocation) ;
    URLDatumDB db = new URLDatumDB(HDFSUtil.getDaultConfiguration(), dblocation) ;
    db.reload() ;
    
    Segment<Text, URLDatum> segment = db.newSegment() ;
    Segment<Text, URLDatum>.Writer writer = segment.getWriter() ;
    for(int i = 0; i < 100; i++) {
      long random = (long)(Math.random() * 100000000) ;
      URLDatum datum = new URLDatum() ;
      datum.setNextFetchTime(System.currentTimeMillis() + 24 *60 * 60 *1000) ;
      datum.setDeep((byte)1) ;
      datum.setOrginalUrl("http://vnexpress.net/" + random) ;
      writer.append(datum.getId(), datum); 
    }
    writer.close() ;
  }
}