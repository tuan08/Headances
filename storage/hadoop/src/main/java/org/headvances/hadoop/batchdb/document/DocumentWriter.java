package org.headvances.hadoop.batchdb.document;

import org.headvances.data.Document ;

public interface DocumentWriter {
  public void writer(Document doc) throws Exception ;
  public void close() throws Exception ;
}
