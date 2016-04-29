package org.headvances.search;

import java.util.ArrayList;
import java.util.List;

import org.codehaus.jackson.JsonNode;
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
import org.elasticsearch.common.Unicode;
import org.elasticsearch.index.query.BaseQueryBuilder;
import org.headvances.json.JSONReader;
import org.headvances.json.JSONSerializer;
/**
 * $Author: Tuan Nguyen$
 **/
public class ESJSONClient extends ESClient {
	private String   index  ;
	private String   type    ;

	public ESJSONClient(String[] address, String index, String type) {
		super(address) ;
		this.index  = index ;
		this.type   = type ;
	}
	
	public ESJSONClient(Client client, String index, String type) {
		super(client) ;
		this.index  = index ;
		this.type   = type ;
	}
	
	public ESJSONClient(ESClient client, String index, String type) {
		super(client.client) ;
		this.index  = index ;
		this.type   = type ;
	}

	public String getIndex() { return this.index  ; }
	
	public String getType() { return this.type ; }
	
	public void createIndex(String settings, String mapping) throws Exception {
		CreateIndexRequestBuilder req = client.admin().indices().prepareCreate(index) ;
		if(settings != null) {
		  req.setSettings(settings) ;
		}
		if(mapping != null) {
		  req.addMapping(type, mapping) ;
		}
		CreateIndexResponse response = req.execute().actionGet() ;
	}
	
	public void removeIndex() throws Exception {
		super.removeIndex(this.index) ;
	}
	
	public void put(String doc, String id) throws Exception {
		BulkRequestBuilder bulkRequest = client.prepareBulk();
		bulkRequest.add(
			client.prepareIndex(index, type, id).
			setSource(doc)
		);
		BulkResponse bulkResponse = bulkRequest.execute().actionGet();
		if(bulkResponse.hasFailures()) {
			throw new Exception("The operation has been failed!\n" + bulkResponse.buildFailureMessage()) ;
		}
	}
	
	public void put(String[] doc, String[] id) throws Exception {
		BulkRequestBuilder bulkRequest = client.prepareBulk();
		for(int i = 0; i < doc.length; i++) {
			bulkRequest.add(
					client.prepareIndex(index, type, id[i]).
					setSource(doc[i])
			);
		}
		BulkResponse bulkResponse = bulkRequest.execute().actionGet();
		if(bulkResponse.hasFailures()) {
			throw new Exception("The operation has been failed!!!") ;
		}
	}
	
	public void doImport(String file) throws Exception {
		JSONReader reader = new JSONReader(file) ;
		JsonNode node = null ;
		List<String> ids = new ArrayList<String>();
		List<String> docs = new ArrayList<String>();
		while((node = reader.read()) != null) {
			JsonNode idNode =node.get("id") ;
			String id = null ;
			if(idNode != null) id = idNode.getTextValue() ;
			String json = JSONSerializer.JSON_SERIALIZER.toString(node) ;
			ids.add(id) ;
			docs.add(json) ;
			if(ids.size() == 100) {
				put(docs.toArray(new String[docs.size()]), ids.toArray(new String[ids.size()])) ;
				ids.clear() ;
				docs.clear() ;
			}
		}
		if(ids.size() > 0) {
			put(docs.toArray(new String[docs.size()]), ids.toArray(new String[ids.size()])) ;
		}
		reader.close() ;
	}
	
  public String get(String id) throws Exception {
  	GetResponse response = client.prepareGet(index, type, id).execute().actionGet();
  	if(!response.exists()) return null ;
  	return response.sourceAsString() ;
  }
  
  public boolean remove(String id) throws Exception {
  	DeleteResponse response = 
  		client.prepareDelete(index, type, id).execute().actionGet();
  	return !response.isNotFound() ;
  }
  
  public SearchResponse search(String jsonQuery) throws Exception {
  	return search(jsonQuery, false, 0, 100) ;
  }
  
  public SearchResponse search(String jsonQuery, int from , int to) throws Exception {
  	return search(jsonQuery, false, from, to) ;
  }
  
  public SearchResponse search(String jsonQuery, boolean explain, int from , int size) throws Exception {
  	SearchResponse response = 
			client.prepareSearch(index).
			setTypes(type).
			setSearchType(SearchType.QUERY_THEN_FETCH).
			setQuery(jsonQuery).
			setFrom(from).setSize(size).
			setExplain(explain).
			execute().actionGet();
  	return response ;
  }
  
  public long count(String jsonQuery) throws Exception {
  	CountResponse response = 
  		client.prepareCount(index).
  		setTypes(type).
  		setQuery(Unicode.fromStringAsBytes(jsonQuery)).
  		execute().actionGet();
  	return response.count() ;
  }
  
  public long count(BaseQueryBuilder xqb) throws Exception {
  	CountResponse response = 
  		client.prepareCount(index).
  		setTypes(type).
  		setQuery(xqb).
  		execute().actionGet();
  	return response.count() ;
  }
}