package org.headvances.analysis.integration;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import javax.annotation.PostConstruct;
import javax.annotation.PreDestroy;

import org.headvances.data.Document;
import org.headvances.search.ESJSONClient;
import org.headvances.util.IOUtil;
import org.headvances.util.text.StringUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
/**
 * $Author: Tuan Nguyen$ 
 **/
public class IndexOutputAdapter {
	private static final Logger logger = LoggerFactory.getLogger(IndexOutputAdapter.class);
	private static final Map<Long, LocalThreadClient> clients = new HashMap<Long, LocalThreadClient> () ;

	private String index ;
	private String type ;
	private String  url[] ;
	private boolean enable = true ;
	
	public String getIndex() { return index; }
	public void   setIndex(String index) { this.index = index; }

	public String getType() { return type; }
	public void   setType(String type) { this.type = type; }

	public String[] getUrl() { return url; }
	public void     setUrl(String[] url) { this.url = url; }

	public void   setUrls(String urls) { 
		this.url = StringUtil.toStringArray(urls); 
	}
	
	public void setEnable(boolean b) { enable = b ; }
	
	@PostConstruct
	public void onInit() { }
	
	@PreDestroy
  public void onDestroy() {
    if(!enable) return ;
    commit() ;
  }
	
	public void write(Document doc)  {
		if(doc == null) return ;
		if(!enable) return ;
		String[] errorTag = doc.getTagWithPrefix("error:") ;
		if(errorTag != null && errorTag.length > 0) return ;
		
		LocalThreadClient client = clients.get(Thread.currentThread().getId()) ;
		if(client == null) {
			synchronized(clients) {
				client = new LocalThreadClient(index, type, url) ;
				clients.put(Thread.currentThread().getId(), client) ;
			}
		}
		String content = doc.getContent() ;
		doc.setContent("") ;
		client.write(doc) ;
		doc.setContent(content) ;
	}
	
	public void commit()  {
		if(!enable) return ;
		Iterator<LocalThreadClient> i = clients.values().iterator() ;
		while(i.hasNext()) i.next().commit() ;
	}
	
	
	static public class LocalThreadClient {
		private ESJSONClient esclient ;
		private List<Document> buffer = new ArrayList<Document>() ;
		private long lastFlushTime = System.currentTimeMillis() ;
		private boolean enable = true ;
		
		public LocalThreadClient(String index, String type, String[] url) {
			try {
				esclient = new ESJSONClient(url, index, type) ;
				if(!esclient.hasIndex(index)) {
					String setting = IOUtil.getResourceAsString("webpage.setting.json", "UTF8") ;
					String mapping = IOUtil.getResourceAsString("webpage.mapping.json", "UTF8") ;
					esclient.createIndex(setting, mapping) ;
				}
			} catch(Exception ex) {
				ex.printStackTrace() ;
				enable = false ;
			}
		}
		
		synchronized public void write(Document doc)  {
			if(doc == null) return ;
			if(!enable) return ;
			if(esclient == null) {
				logger.error("Index Server is not available!!!!!!!!!!!!") ;
				enable = false ;
				return ;
			}
			try {
				buffer.add(doc) ;
				if(buffer.size() > 100|| System.currentTimeMillis() > (lastFlushTime + 10000)) {
					flush(buffer) ;
					buffer.clear() ;
				}
			} catch(Exception ex) {
				logger.error("Cannot send the HtmlDocument to index", ex) ;
			}
		}
		
		synchronized public void commit()  {
			if(!enable) return ;
			if(esclient == null) {
				logger.error("Index Server is not available!!!!!!!!!!!!") ;
				enable = false ;
				return ;
			}
			try {
				flush(buffer) ;
				buffer.clear() ;
			} catch(Exception ex) {
				logger.error("Cannot send the HtmlDocument to index", ex) ;
			}
		}
		
		private void flush(List<Document> docs) throws Exception {
			String[] id = new String[docs.size()] ;
			String[] json = new String[docs.size()] ;
			for(int i = 0; i < docs.size(); i++) {
				Document doc = docs.get(i) ;
				id[i] = doc.getId() ;
				json[i] = Document.JSON_SERIALIZER.toString(doc) ;
			}
			try {
				esclient.put(json, id) ;
				this.lastFlushTime = System.currentTimeMillis() ;
			} catch(Exception ex) {
				//enable = false ;
				logger.error("Cannot send the HtmlDocument to index", ex) ;
			}
		}
	}
}