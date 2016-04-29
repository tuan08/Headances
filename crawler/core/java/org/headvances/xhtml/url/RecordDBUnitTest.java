package org.headvances.xhtml.url;

import java.text.DecimalFormat;

import org.apache.hadoop.conf.Configuration;
import org.apache.hadoop.io.Text;
import org.apache.hadoop.io.Writable;
import org.headvances.util.FileUtil;
import org.headvances.xhtml.url.HDFSUtil;
import org.headvances.xhtml.url.MergeMultiSegmentIterator;
import org.headvances.xhtml.url.RecordDB;
import org.headvances.xhtml.url.RecordUpdater;
import org.headvances.xhtml.url.Segment;
import org.junit.Assert;
import org.junit.Test;
/**
 * Author : Tuan Nguyen
 *          tuan.nguyen@headvances.com
 * Apr 19, 2010  
 */
public class RecordDBUnitTest {
  static DecimalFormat NFORMATER = new DecimalFormat("0000000000000") ;
  
  @Test
  public void test() throws Exception {
    String dblocation = "target/db" ;
    FileUtil.removeIfExist(dblocation) ;
    
    Configuration conf = HDFSUtil.getDaultConfiguration() ;
    RecordDB<Text, ARecord> db = 
      new RecordDB<Text, ARecord>(conf, dblocation, Text.class, ARecord.class) {
      public Text createKey() { return new Text(); }
      public ARecord createValue() { return new ARecord(); }
    } ;
    db.reload() ;
    
    Segment<Text, ARecord> segment0 = db.newSegment() ;
    createData(segment0, 10, 1) ;
  
    Segment<Text, ARecord> segment1 = db.newSegment() ;
    createData(segment1, 20, 2) ;
    
    assertData(db, 15) ;
    db.autoCompact() ;
    assertData(db, 15) ;
    
    RecordUpdater<ARecord> updater = new RecordUpdater<ARecord>() {
      public ARecord update(Writable key, ARecord record) {
      	if(record.getValue() % 2 == 0) {
          Assert.assertEquals(0, record.getSegment()) ;
          return null ;
      	} else {
          Assert.assertEquals(1, record.getSegment()) ;
          return record;
      	}
      }
    } ;
    db.update(updater) ;
    assertData(db, 10) ;
  }
  
  private void assertData(RecordDB<Text, ARecord> db, int expectSize) throws Exception {
    MergeMultiSegmentIterator<Text, ARecord> mitr = db.getMergeRecordIterator() ;
    Text pkey = null, key = new Text() ;
    ARecord pvalue = null, value  = new ARecord() ;
    int count = 0 ;
    while(mitr.next()) {
      key = mitr.currentKey() ;
      value = mitr.currentValue() ;
      if(pkey != null) {
        Assert.assertTrue(pkey.compareTo(key) < 0) ;
      }
      if(value.getValue() % 2 == 0) {
        Assert.assertEquals(0, value.getSegment()) ;
      } else {
        Assert.assertEquals(1, value.getSegment()) ;
      }
      pkey = new Text(key.getBytes()) ; pvalue = value ;
      count++ ;
    }
    mitr.close() ;
    Assert.assertEquals(expectSize, count) ;
  }
  
  private void createData(Segment<Text, ARecord> seg, int size, int incr) throws Exception {
    Segment<Text, ARecord>.Writer writer = seg.getWriter() ;
    for(int i = seg.getIndex(); i < size; i += incr) {
      ARecord datum = createDatum(seg.getIndex(), i) ;
      writer.append(new Text(datum.getURL()), datum); 
    }
    writer.close() ;
  }
  
  private ARecord createDatum(int segment, int value) {
    ARecord datum = new ARecord() ;
    datum.setURL("http://vnexpress.net/" + NFORMATER.format(value)) ;
    datum.setValue(value) ;
    datum.setSegment(segment) ;
    return datum ;
  }
}