package org.headvances.analysis;

import java.io.Serializable;
import java.util.LinkedList;

import org.headvances.data.Document;
/**
 * $Author: Tuan Nguyen$ 
 **/
public class AnalysisHistory implements Serializable {
	private LinkedList<Document> histories = new LinkedList<Document>() ;
	private int maxSize = 100; 

	synchronized public Document[] getAnalysisDocuments() {
		return histories.toArray(new Document[histories.size()]) ;
	}

	synchronized public void add(Document doc) {
		histories.add(doc) ;
		if(histories.size() > maxSize) histories.removeLast() ;
	}
	
	synchronized public void clear() { histories.clear() ; }
}