package org.headvances.xhtml.url;

import java.io.IOException;

import org.apache.hadoop.conf.Configuration;
import org.apache.hadoop.fs.FileStatus;
import org.apache.hadoop.fs.FileSystem;
import org.apache.hadoop.fs.Path;
import org.apache.hadoop.io.SequenceFile;
import org.apache.hadoop.io.WritableComparable;
import org.apache.hadoop.io.compress.CompressionCodec;
import org.apache.hadoop.io.compress.DefaultCodec;
import org.apache.hadoop.io.compress.GzipCodec;

import com.hadoop.compression.lzo.LzoCodec;
/**
 * Author : Tuan Nguyen
 *          tuan.nguyen@headvances.com
 * Apr 19, 2010  
 */
public class Segment<K extends WritableComparable, V extends Record> implements Comparable<Segment<K,V>>{
  static CompressionCodec DEFAULT_CODEC = new DefaultCodec() ;
  static CompressionCodec GZIP_CODEC    = new GzipCodec() ;
  static CompressionCodec LZO_CODEC     = new LzoCodec() ;
  
  
  private String dblocation ;
  private String name ;
  private int    index   ;
  private Configuration configuration ;
  private Class<K> keyType ;
  private Class<V> valueType ;
  
  public Segment(Configuration configuration, String dblocation, String name, 
                 Class<K> keyType, Class<V> valueType) throws Exception {
    this.configuration = configuration ;
    this.dblocation = dblocation ;
    this.name = name ;
    this.index = Integer.parseInt(name.substring(name.indexOf('-') + 1)) ;
    this.keyType = keyType; 
    this.valueType = valueType ;
    HDFSUtil.mkdirs(FileSystem.get(configuration), dblocation + "/" + name) ;
  }
  
  public String getName() { return this.name ; }
  
  public int getIndex() { return index ; }
  
  public String getDBLocation() { return this.dblocation ; }
  
  public String getSegmentPath() {
    return dblocation + "/" + name ;
  }
  
  public Configuration getConfiguration() { return this.configuration ; }
  
  public long getDataSize() throws Exception {
    FileSystem fs = FileSystem.get(this.configuration) ;
    Path path = new Path(getDatFilePath()) ;
    if(!fs.exists(path)) return 0 ;
    FileStatus status = fs.getFileStatus(path) ;
    return status.getLen() ;
  }
  
  public SequenceFile.Reader getReader() throws Exception {
    Configuration conf = new Configuration(this.configuration) ;
    FileSystem fs = FileSystem.get(conf) ;
    Path path = new Path(getDatFilePath()) ;
    if(!fs.exists(path)) return null ;
    SequenceFile.Reader reader = new SequenceFile.Reader(fs, path, conf) ;
    return reader ;
  }
  
  public void delete() throws Exception {
    Configuration conf = new Configuration(this.configuration) ;
    FileSystem fs = FileSystem.get(conf) ;
    if(!fs.delete(new Path(getSegmentPath()), true)) {
      throw new Exception("Cannot delete path: " + getSegmentPath()) ;
    }
  }
  
  public Writer getWriter() throws Exception {
    Configuration conf = new Configuration(this.configuration) ;
    FileSystem fs = FileSystem.get(conf) ;
    return new Writer(fs, getDatFilePath()) ;
  }
  
  private String getDatFilePath() {
    return dblocation + "/" + name + "/segment.dat" ;
  }

  public int compareTo(Segment<K, V> other) {
    return index - other.index;
  }
  
  static public String createSegmentName(int index) {
    return "segment-" + index ;
  }
  
  public class Writer {
    final public String path ;
    final SequenceFile.Writer writer ;
    final FileSystem fs ;
    private K pkey ;
    private boolean keyOutOfOrder ;
    
    public Writer(FileSystem fs, String path) throws Exception {
      this.path = path ;
      this.fs = fs ;
      Configuration conf = fs.getConf() ;
      String compressionCodec = conf.get("compression.default.codec") ;
      CompressionCodec codec  = DEFAULT_CODEC;
      if("gzip".equals(compressionCodec)) codec = GZIP_CODEC ;
      else if("lzo".equals(compressionCodec)) codec = LZO_CODEC ;
      
      SequenceFile.Metadata meta = new SequenceFile.Metadata() ;
      this.writer = SequenceFile.createWriter(
          fs,                   //FileSystem 
          fs.getConf(),  // configuration 
          new Path(path + ".writer"),          // the path
          keyType,            // Key Class
          valueType,      // Value Class
          fs.getConf().getInt("io.file.buffer.size", 4096), //buffer size 
          fs.getDefaultReplication(),  //frequency of replication 
          fs.getDefaultBlockSize(), // default to 32MB: large enough to minimize the impact of seeks 
          SequenceFile.CompressionType.BLOCK,   //Compress method
          codec,      // compression codec
          null,                  //progressor
          meta            // File Metadata
      ) ;
    }

    public void append(K key, V value) throws IOException {
      this.writer.append(key, value) ;
      if(!keyOutOfOrder && pkey != null) {
        if(pkey.compareTo(key) > 0) keyOutOfOrder = true ;
      }
      pkey = key ;
    }
    
    public void close() throws Exception {
      this.writer.close() ;
      SequenceFile.Sorter sorter = new SequenceFile.Sorter(fs, keyType, valueType, fs.getConf()) ;
      Path pwriter = new Path(path + ".writer") ;
      if(keyOutOfOrder) {
        Path psort   = new Path(path + ".sort") ;
        sorter.sort(new Path[]{pwriter}, psort, true) ;
        HDFSUtil.removeIfExists(fs, path) ;
        HDFSUtil.mv(fs, psort, new Path(path)) ;
      } else {
        HDFSUtil.mv(fs, pwriter, new Path(path)) ;
      }
    }
  }
}