package org.headvances.analysis.integration;

import org.headvances.data.Document;
import org.headvances.json.JSONMultiFileWriter;

/**
 * $Author: Tuan Nguyen$ 
 **/
public class FileOutputAdapter extends JSONMultiFileWriter {
	private boolean enable ;
	
	public void setEnable(boolean b ) { enable = b ; }
	
	public void save(Document doc) throws Exception {
		if(!enable) return ;
		String[] errorTag = doc.getTagWithPrefix("error:") ;
		if(errorTag != null && errorTag.length > 0) return ; 
		write(doc) ;
	}
}
