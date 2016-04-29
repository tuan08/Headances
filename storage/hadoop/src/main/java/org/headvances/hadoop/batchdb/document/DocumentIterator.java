package org.headvances.hadoop.batchdb.document;

import org.apache.hadoop.conf.Configuration;
import org.headvances.data.Document ;
import org.headvances.hadoop.batchdb.Database;
import org.headvances.hadoop.util.HDFSUtil;

abstract public class DocumentIterator {
	abstract public Document next() throws Exception ;
	abstract public void close() throws Exception ;
  
  static public DocumentIterator getIterator(String dataDir, String type) throws Exception {
    if("db".equals(type)) {
      Configuration conf = HDFSUtil.getDaultConfiguration() ;
      Database database = Database.getDatabase(dataDir, conf) ;
      return new DocumentDBIterator(database) ;
    } else if("file".equals(type)) {
      return new DocumentFileIterator(dataDir) ;
    } else {
      throw new Exception("Unknown type " + type + " data");
    }
  }
}