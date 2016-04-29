package org.headvances.xhtml.url;

import org.apache.hadoop.io.Text;
import org.headvances.util.FileUtil;
import org.headvances.xhtml.url.HDFSUtil;
import org.headvances.xhtml.url.Segment;
import org.junit.Assert;
import org.junit.Test;
/**
 * Author : Tuan Nguyen
 *          tuan.nguyen@headvances.com
 * Apr 19, 2010  
 */
public class SegmentUnitTest {
  @Test
  public void test() throws Exception {
    String dblocation = "target/db" ;
    FileUtil.removeIfExist(dblocation) ;
    String segname = Segment.createSegmentName(0) ;
    Segment<Text, ARecord> segment = 
      new Segment<Text, ARecord>(HDFSUtil.getDaultConfiguration(), dblocation, segname, Text.class, ARecord.class) ;
    Segment<Text, ARecord>.Writer writer = segment.getWriter() ;
    for(int i = 0; i < 100; i++) {
      ARecord datum = new ARecord() ;
      datum.setURL("http://vnexpress.net/" + i) ;
      datum.setValue(i) ;
      datum.setSegment(0) ;
      writer.append(new Text(datum.getURL()), datum); 
    }
    writer.close() ;
    Assert.assertEquals("/GL/Xa-Hoi", "/GL/Xa-Hoi") ;
  }
}
