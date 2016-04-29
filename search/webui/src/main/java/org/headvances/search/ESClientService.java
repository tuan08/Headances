package org.headvances.search;

import java.util.HashMap;
import java.util.Map;

import javax.annotation.PostConstruct;

import org.headvances.search.ESJSONClient;
/**
 * $Author: Tuan Nguyen$ 
 **/
public class ESClientService {
	private Map<String, ESJSONClient> clients = new HashMap<String, ESJSONClient>() ;
	
	private String   index; 
	private String   type ;
	private String[] url ;
	
	public String getIndex() { return index; }
	public void   setIndex(String index) { this.index = index; }

	public String getType() { return type; }
	public void   setType(String type) { this.type = type; }

	public String[] getUrl() { return url; }
	public void     setUrl(String[] url) { this.url = url; }

	@PostConstruct
	public void onInit() {
		connect(url, index, type) ;
		System.out.println("onInit() Done!!!!!!!!!!!!!!!!!!");
	}
	
	public ESJSONClient getClient() {  return clients.get("web") ; }
	
	public ESJSONClient getClient(String index) {  return clients.get(index)  ; }
	
	public void connect(String[] url, String index, String type) {
		ESJSONClient client = clients.get(index) ;
		if(client != null) client.close() ;
		client = new ESJSONClient(url, index, type) ;
		clients.put(index, client) ;
	}
	
	public String[] getAvailableIndex() { 
		return clients.keySet().toArray(new String[clients.size()]) ;
	}
}