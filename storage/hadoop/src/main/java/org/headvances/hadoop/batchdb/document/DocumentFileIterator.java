package org.headvances.hadoop.batchdb.document;

import org.headvances.data.Document;
import org.headvances.json.JSONMultiFileReader;

public class DocumentFileIterator extends DocumentIterator {
  private JSONMultiFileReader reader  ;
  
  public DocumentFileIterator(String dataLoc) throws Exception {
    reader = new JSONMultiFileReader(dataLoc) ;
  }

  public Document next() throws Exception {
    return  reader.next(Document.class) ;
  }

  public void close() throws Exception {
    reader.close();
  }
}