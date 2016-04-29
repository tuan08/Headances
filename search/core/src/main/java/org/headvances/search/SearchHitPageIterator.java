package org.headvances.search;

import java.util.ArrayList;
import java.util.List;

import org.elasticsearch.action.search.SearchResponse;
import org.elasticsearch.search.SearchHit;
import org.headvances.data.Document;
import org.headvances.util.PageList;
import org.headvances.util.cache.InmemoryCache;
/**
 * $Author: Tuan Nguyen$ 
 **/
public class SearchHitPageIterator extends PageList<SearchHit> {
	private ESJSONClient   client ;
	private String         jsonQuery ;
	private SearchResponse response ;
	private int            subRangeFrom = 1;
  private int            subRangeTo   = 10;
	private int            preLoad = 3 ;
  
	private InmemoryCache<Integer, List<SearchHit>> caches ;

	public SearchHitPageIterator(ESJSONClient client, String jsonQuery) throws Exception {
	  this(client, jsonQuery, 10);
  }
	
  public SearchHitPageIterator(ESJSONClient client, String jsonQuery, int pageSize) throws Exception {
  	this(client, jsonQuery, pageSize, 3) ;
  }
  
  public SearchHitPageIterator(ESJSONClient client, String jsonQuery, int pageSize, int preLoad) throws Exception {
	  super(pageSize);
	  this.client = client ;
	  this.jsonQuery = jsonQuery ;
	  this.preLoad = preLoad ;
	  caches = new InmemoryCache<Integer, List<SearchHit>>("", 10) ;
	  getPage(1) ;
  }
  
  public ESJSONClient getClient() { return this.client ; }

  public String getJSONQuery() { return this.jsonQuery ; }
  
  public SearchResponse getSearchResponse() { return this.response ;  }

  public IndexInfo getIndexInfo() { return new IndexInfo(response) ; }
  
  public int getSubRangeFrom() { return subRangeFrom; }

  public int getSubRangeTo() { return subRangeTo; }

  public int getPreviousPage() {
    int page = getCurrentPage() - 1;
    if (page < 1) page = 1;
    return page;
  }

  public int getNextPage() {
    int page = getCurrentPage() + 1;
    if (page > getAvailablePage()) page = getAvailablePage();
    return page;
  }

  public void clearCache() throws Exception {
  	caches.clearCache() ;
  }
  
	protected void populateCurrentPage(int page) throws Exception {
		List<SearchHit> holder = caches.getCachedObject(page) ;
		if(holder != null) {
			setCurrentPage(page, holder) ;
		} else {
		  populateCache(page) ;
		  holder = caches.getCachedObject(page) ;
		  setCurrentPage(page, holder) ;
		}
		if(this.getAvailable() == 0) {
			setAvailablePage((int)response.getHits().getTotalHits()) ;
		}
		int[] range = getSubRange(page, 10);
		subRangeFrom = range[0];
		subRangeTo   = range[1];
	}
	
	private void populateCache(int fromPage) throws Exception {
		int from = (fromPage - 1) * getPageSize() ;
		int to = from + (preLoad * getPageSize()) ;
		
		System.out.println("From: " + from + ", To: " + to) ;
		response = client.search(jsonQuery, from, to - from) ;
		System.out.println("Hits: " + response.getHits().getTotalHits());
		SearchHit[] hit = response.getHits().getHits() ;
		List<SearchHit> holder = new ArrayList<SearchHit>(10) ;
		int page = fromPage ;
		for(int i = 0; i < hit.length; i++) {
			holder.add(hit[i]) ;
			if(holder.size() == getPageSize()) {
				System.out.println("Page " + page + ":"  + holder.size()) ;
				caches.putCachedObject(page++, holder) ;
				holder = new ArrayList<SearchHit>(10) ;
			}
		}
		if(page == fromPage || holder.size() > 0) {
		  caches.putCachedObject(page, holder) ;
		  System.out.println("Page(last) " + page + ":"  + holder.size()) ;
		}
	}
	
	public List<Document> getPageAsDocument(int page) throws Exception {
		List<SearchHit> hits = getPage(page) ;
		List<Document> docs = new ArrayList<Document>() ;
		for(int i = 0; i < hits.size(); i++) {
			SearchHit hit = hits.get(i) ;
			Document doc = Document.JSON_SERIALIZER.fromString(hit.sourceAsString(), Document.class) ;
			docs.add(doc) ;
		}
		return docs ;
	}
}
