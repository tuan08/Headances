package org.headvances.hadoop.batchdb.document;

import org.headvances.data.Document ;
import org.headvances.json.JSONMultiFileWriter ;

public class DocumentFileWriter implements DocumentWriter {
  private JSONMultiFileWriter writer ;
  
  public DocumentFileWriter(String dataLoc, int maxDocPerFile) throws Exception {
   writer =  new JSONMultiFileWriter(dataLoc) ;
   writer.setCompress(true) ;
   writer.setMaxDocumentPerFile(maxDocPerFile) ;
  }
  
  public void writer(Document doc) throws Exception {
    writer.write(doc);
  }

  public void close() throws Exception {
    writer.close() ;
  }
}
