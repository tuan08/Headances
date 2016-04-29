package org.headvances.xhtml.url;

import org.apache.hadoop.io.Text;
import org.headvances.xhtml.url.HDFSUtil;
import org.headvances.xhtml.url.MergeMultiSegmentIterator;
import org.headvances.xhtml.url.Segment;
import org.headvances.xhtml.url.URLDatum;
import org.headvances.xhtml.url.URLDatumDB;
import org.junit.Test;
/**
 * Author : Tuan Nguyen
 *          tuan.nguyen@headvances.com
 * Apr 19, 2010  
 */
public class URLDatumDBPerformanceTest {
	@Test
	public void test() throws Exception {
		String dblocation = "urldb" ;
		URLDatumDB db = new URLDatumDB(HDFSUtil.getDaultConfiguration(), dblocation) ;
		db.reload() ;
		MergeMultiSegmentIterator<Text, URLDatum> mitr = db.getMergeRecordIterator() ;
		Segment<Text, URLDatum>.Writer writer = null ;
		long start = System.currentTimeMillis() ;
		int count  = 0 ;
		while(mitr.next()) {
			count++ ;
			if(count % 100000 == 0) {
				System.out.println("read " + count + " in " + (System.currentTimeMillis() - start) + "ms") ;
			}
		}
		System.out.println("Iterate in: " + (System.currentTimeMillis() - start) + "ms");
	}
}