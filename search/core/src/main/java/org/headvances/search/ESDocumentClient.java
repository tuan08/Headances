package org.headvances.search;

import java.util.List;

import org.elasticsearch.action.admin.indices.create.CreateIndexResponse;
import org.elasticsearch.action.bulk.BulkResponse;
import org.elasticsearch.action.count.CountResponse;
import org.elasticsearch.action.delete.DeleteResponse;
import org.elasticsearch.action.get.GetResponse;
import org.elasticsearch.action.search.SearchResponse;
import org.elasticsearch.action.search.SearchType;
import org.elasticsearch.client.Client;
import org.elasticsearch.client.action.admin.indices.create.CreateIndexRequestBuilder;
import org.elasticsearch.client.action.bulk.BulkRequestBuilder;
import org.elasticsearch.index.query.BaseQueryBuilder;
import org.elasticsearch.search.SearchHit;
import org.headvances.data.Document;
/**
 * $Author: Tuan Nguyen$
 **/
public class ESDocumentClient<T extends Document> extends ESClient {
	private String   index  ;
	private Class<T> type   ;

	public ESDocumentClient(String[] address, String index, Class<T> type) {
		super(address) ;
		this.index  = index ;
		this.type   = type ;
	}
	
	public ESDocumentClient(Client client, String index, Class<T> type) {
		super(client) ;
		this.index  = index ;
		this.type   = type ;
	}
	
	public ESDocumentClient(ESClient client, String index, Class<T> type) {
		super(client.client) ;
		this.index  = index ;
		this.type   = type ;
	}

	public String getIndex() { return this.index ; }
	
	public Class<T> getIDocumentType() { return this.type ; }
	
	public void createIndexWith(String settings, String mapping) throws Exception {
		CreateIndexRequestBuilder req = client.admin().indices().prepareCreate(index) ;
		if(settings != null) {
		  req.setSettings(settings) ;
		}
		if(mapping != null) {
		  req.addMapping(type.getSimpleName(), mapping) ;
		}
		CreateIndexResponse response = req.execute().actionGet() ;
	}
	
	public void updateSettings(String settings) throws Exception {
		updateSettings(index, settings) ;
	}
	
	public void updateMapping(String mapping) throws Exception {
		updateMapping(index, type.getSimpleName(), mapping) ;
	}
	
	public void put(Document idoc) throws Exception {
		BulkRequestBuilder bulkRequest = client.prepareBulk();
		byte[] data = Document.JSON_SERIALIZER.toBytes(idoc) ;
		bulkRequest.add(
			client.prepareIndex(index, type.getSimpleName(), idoc.getId()).
			setSource(data)
		);
		BulkResponse bulkResponse = bulkRequest.execute().actionGet();
		if(bulkResponse.hasFailures()) {
			throw new Exception("The operation has been failed!!!") ;
		}
	}
	
	public void put(List<Document> idocs) throws Exception {
		BulkRequestBuilder bulkRequest = client.prepareBulk();
		for(int i = 0; i < idocs.size(); i++) {
			Document idoc = idocs.get(i) ;
			byte[] data = Document.JSON_SERIALIZER.toBytes(idoc) ;
			bulkRequest.add(
					client.prepareIndex(index, type.getSimpleName(), idoc.getId()).
					setSource(data)
			);
		}
		BulkResponse bulkResponse = bulkRequest.execute().actionGet();
		if(bulkResponse.hasFailures()) {
			throw new Exception("The operation has been failed!!!") ;
		}
	}
	
  public T get(String id) throws Exception {
  	GetResponse response = client.prepareGet(index, type.getSimpleName(), id).execute().actionGet();
  	if(!response.exists()) return null ;
  	return Document.JSON_SERIALIZER.fromBytes(response.source(), type) ;
  }
  
  public boolean remove(String id) throws Exception {
  	DeleteResponse response = 
  		client.prepareDelete(index, type.getSimpleName(), id).execute().actionGet();
  	return !response.isNotFound() ;
  }
  
  public SearchResponse search(BaseQueryBuilder xqb) throws Exception {
  	return search(xqb, false, 0, 100) ;
  }
  
  public SearchResponse search(BaseQueryBuilder xqb, int from , int to) throws Exception {
  	return search(xqb, false, from, to) ;
  }
  
  public T getIDocument(SearchHit hit) throws Exception {
  	return Document.JSON_SERIALIZER.fromBytes(hit.source(), type) ;
  }
  
  public SearchResponse search(BaseQueryBuilder xqb, boolean explain, int from , int to) throws Exception {
  	SearchResponse response = 
			client.prepareSearch(index).setSearchType(SearchType.QUERY_THEN_FETCH).
			setQuery(xqb).
			setFrom(from).setSize(to).
			setExplain(explain).
			execute().actionGet();
  	return response ;
  }
  
  public long count(BaseQueryBuilder xqb) throws Exception {
  	CountResponse response = 
  		client.prepareCount(index).
  		setQuery(xqb).
  		execute().actionGet();
  	return response.count() ;
  }
}