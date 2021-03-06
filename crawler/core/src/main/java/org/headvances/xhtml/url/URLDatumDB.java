package org.headvances.xhtml.url;


import org.apache.hadoop.conf.Configuration;
import org.apache.hadoop.fs.FileSystem;
import org.apache.hadoop.io.Text;
import org.headvances.hadoop.util.ConfigurationFactory;
/**
 * Author : Tuan Nguyeni  
 *          tuan.nguyen@headvances.com
 * Apr 21, 2010  
 */
public class URLDatumDB extends RecordDB<Text, URLDatum>{
	private boolean cleandb ;
	
	public URLDatumDB() {}
	
  public URLDatumDB(String dblocation) throws Exception {
    super(ConfigurationFactory.getConfiguration(), dblocation, Text.class, URLDatum.class);
    reload() ;
  }

  public void onInit() throws Exception {
  	Configuration conf = ConfigurationFactory.getConfiguration() ;
  	FileSystem fs = FileSystem.get(conf) ;
  	if(cleandb) {
    	HDFSUtil.removeIfExists(fs, getDbLocation()) ;
    }
  	onInit(conf, getDbLocation(), Text.class, URLDatum.class);
    reload() ;
  }
  
  public boolean getCleanDB() { return this.cleandb ; }
  public void setCleanDB(boolean b) { cleandb = b ; }
  
  public URLDatumDB(Configuration configuration, String dblocation) throws Exception {
    super(configuration, dblocation, Text.class, URLDatum.class);
  }

  public Text createKey() { return new Text(); }

  public URLDatum createValue() { return new URLDatum(); }
  
  protected RecordMerger<URLDatum> createRecordMerger() { return new SegmentRecordMerger() ; }
  
  static public class SegmentRecordMerger implements RecordMerger<URLDatum> {
    public URLDatum merge(URLDatum r1, URLDatum r2) {
      if(r2.getCreatedTime() <= r1.getCreatedTime()) {
        return r2 ;
      }
      return r1 ;
    }
  }
}
