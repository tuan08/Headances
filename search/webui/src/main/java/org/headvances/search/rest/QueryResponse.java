package org.headvances.search.rest;

import org.headvances.search.IndexInfo;

/**
 * $Author: Tuan Nguyen$ 
 **/
public class QueryResponse {
	private Query query ;
	private IndexInfo indexInfo ;
	private int currentPage;
	private int availablePage ;
	private RecordDescription[] recordDescription ;
	
	private String  status = "ok";
	private String  message ;

	public QueryResponse() {}

	public QueryResponse(Query query, RecordDescription[] recordDescription) {
		this.query = query ;
		this.recordDescription = recordDescription ;
	}

	public Query getQuery() { return query; }
	public void setQuery(Query query) { this.query = query;}

	public IndexInfo getIndexInfo() { return this.indexInfo ; }
	public void setIndexInfo(IndexInfo info) { this.indexInfo = info ;}

	public RecordDescription[] getRecordDescription() { return recordDescription;}
	public void setRecordDescription(RecordDescription[] recordDescription) {
		this.recordDescription = recordDescription;
	}

	public int getCurrentPage() { return this.currentPage ; }
	public void setCurrentPage(int page) { this.currentPage = page ;}
	
	public int getAvailablePage() { return this.availablePage ; }
	public void setAvailablePage(int page) { this.availablePage = page ;}
	
	public String getStatus() { return this.status ; }
	public void setStatus(String status) { this.status = status ; }

	public String getMessage() { return this.message ; }
	public void   setMessage(String mesg) { this.message = mesg ; }

	static QueryResponse mock() {
		Query query = new Query() ;
		query.setQuery("Iphone 3GS") ;
		RecordDescription[] recordDescription = new RecordDescription[10];
		for(int i = 0; i < recordDescription.length; i++) {
			recordDescription[i] = new RecordDescription() ;
			recordDescription[i].setId("id " + i) ;
			recordDescription[i].setUrl("url " + i) ;
			recordDescription[i].setTitle("title " + i) ;
			recordDescription[i].setDescription("description " + i) ;
		}
		return new QueryResponse(query, recordDescription) ;
	}
}