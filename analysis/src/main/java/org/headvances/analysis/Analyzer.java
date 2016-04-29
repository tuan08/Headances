package org.headvances.analysis;

import org.headvances.data.Document;
import org.headvances.html.dom.TDocument;

/**
 * $Author: Tuan Nguyen$ 
 **/
public interface Analyzer {
	public void analyze(Document ahDoc, TDocument tdoc) ;
}
