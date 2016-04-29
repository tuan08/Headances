package org.headvances.search.webui;

import java.util.LinkedHashMap;
import java.util.Map;
/**
 * $Author: Tuan Nguyen$ 
 **/
public class LabelHistories extends LinkedHashMap<String, String> {
	final static public int MAX_SIZE = 100000;
	
	protected boolean removeEldestEntry(Map.Entry eldest) {
		return size() > MAX_SIZE;
	}

	public void label(String docId, String label) {
		put(docId, label) ;
	}
}