package org.headvances.hadoop.batchdb.document;

import org.headvances.data.Document ;
import org.headvances.hadoop.batchdb.Database ;
import org.headvances.hadoop.batchdb.Row ;
import org.headvances.hadoop.batchdb.document.DocumentMapper ;

public class DocumentDBIterator extends DocumentIterator {
  private Database database ;
  private Database.Reader reader ;
  private DocumentMapper mapper = new DocumentMapper() ;
  
  public DocumentDBIterator(Database database) throws Exception {
    this.database = database ;
    this.reader = database.getReader() ;
  }
  
  public Document next() throws Exception {
    Row row = reader.next() ;
    if(row == null) return null ;
    return  mapper.fromRow(row) ;
  }

  public void close() throws Exception {
    reader.close() ;
  }
}
