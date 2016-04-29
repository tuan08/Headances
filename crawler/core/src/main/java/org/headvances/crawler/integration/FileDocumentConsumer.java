package org.headvances.crawler.integration;

import org.headvances.data.Document;
import org.headvances.json.JSONMultiFileWriter;
/**
 * $Author: Tuan Nguyen$ 
 **/
public class FileDocumentConsumer extends DocumentConsumer {
	private JSONMultiFileWriter writer  ;
	
	public void onInit() throws Exception {
	  super.onInit() ;
	  writer = new JSONMultiFileWriter(getStoreDir()) ;
	  writer.setCompress(true) ;
	  writer.setMaxDocumentPerFile(5000) ;
	}
	
	public void onDestroy() throws Exception {
	  super.onDestroy() ;
	  writer.close() ;
	}
	
  public void consume(Document doc) throws Exception {
    writer.write(doc) ;
	}
	
	final public void dump(Document doc) {
	}
}