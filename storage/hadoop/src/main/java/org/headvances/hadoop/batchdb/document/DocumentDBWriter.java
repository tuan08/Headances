package org.headvances.hadoop.batchdb.document;

import org.headvances.data.Document ;
import org.headvances.hadoop.batchdb.Database ;
import org.headvances.hadoop.batchdb.Row ;
import org.headvances.hadoop.batchdb.document.DocumentMapper ;

public class DocumentDBWriter implements DocumentWriter {
  private Database.Writer writer ;
  private DocumentMapper mapper ;

  
  public DocumentDBWriter(Database db) throws Exception {
    writer = db.getWriter() ;
    mapper = new DocumentMapper() ;
  }
  
  public void writer(Document doc) throws Exception {
    Row row = mapper.toRow(doc) ;
    writer.write(row.getRowId(), row, null) ;
  }

  public void close() throws Exception {
    writer.close() ;
  }
}
