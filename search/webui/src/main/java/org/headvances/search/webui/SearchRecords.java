package org.headvances.search.webui;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import org.elasticsearch.search.SearchHit;
import org.headvances.data.Document;
import org.headvances.search.TermHighlighter;

/**
 * $Author: Tuan Nguyen$ 
 **/
public class SearchRecords extends ArrayList<SearchRecord> {
	public SearchRecords(List<SearchHit> hits, TermHighlighter highlighter, LabelHistories lbHistories) throws IOException {
		if(hits == null) return ;
		for(int i = 0; i < hits.size(); i++) {
			SearchHit hit = hits.get(i) ;
			Document doc = 
				Document.JSON_SERIALIZER.fromString(hit.sourceAsString(), Document.class) ;
			SearchRecord record = new SearchRecord(doc, highlighter, false) ;
			record.updateLabel(lbHistories) ;
			add(record) ;
		}
	}
}