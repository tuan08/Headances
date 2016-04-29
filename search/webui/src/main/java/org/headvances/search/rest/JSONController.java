package org.headvances.search.rest;

import java.util.List;

import javax.servlet.http.HttpServletResponse;

import org.elasticsearch.search.SearchHit;
import org.headvances.data.Document;
import org.headvances.search.ESClientService;
import org.headvances.search.ESJSONClient;
import org.headvances.search.SearchHitPageIterator;
import org.headvances.search.TermHighlighter;
import org.headvances.util.cache.InmemoryCache;
import org.headvances.util.text.StringUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;

@Controller
@RequestMapping(value="/rest")
public class JSONController {
	@Autowired 
	private ESClientService service ;
	private QueryBuilder    queryBuilder = new QueryBuilder() ;
	private InmemoryCache<String, SearchHitPageIterator> iteratorCaches ;
	
	//@Autowired
	public JSONController() {
		iteratorCaches = new InmemoryCache<String, SearchHitPageIterator>("cache", 1000) ;
		iteratorCaches.setLiveTime(10 * 60 * 1000) ;
	}
	
	@RequestMapping(value="/query", method=RequestMethod.GET)
	public @ResponseBody QueryResponse search(Query query) {
		QueryResponse qresp = new QueryResponse() ;
		qresp.setQuery(query) ;
		try {
			SearchHitPageIterator iterator = getSearchHitPageIterator(query) ;
			TermHighlighter highlighter = new TermHighlighter(query.getQuery()) ;
			List<SearchHit> hits = iterator.getPage(query.getPage()) ;
			RecordDescription[] desc = new RecordDescription[hits.size()] ;
			int startIndex = (query.getPage() - 1) * iterator.getPageSize() ;
			for(int i = 0; i < desc.length; i++) {
				SearchHit hit = hits.get(i) ;
				Document doc = Document.JSON_SERIALIZER.fromString(hit.sourceAsString(), Document.class) ;
				desc[i] = new RecordDescription(doc, highlighter) ;
				desc[i].setIndex(startIndex + i + 1) ;
			}
			qresp.setIndexInfo(iterator.getIndexInfo());
			qresp.setRecordDescription(desc) ;
			qresp.setCurrentPage(query.getPage()) ;
			qresp.setAvailablePage(iterator.getAvailablePage()) ;
		} catch (Exception e) {
			e.printStackTrace();
			qresp.setStatus("fail") ;
			qresp.setMessage(e.getMessage()) ;
		}
		return qresp ;
	}
	
	@RequestMapping(value = "/cache/{id:[a-zA-Z0-9\\.\\:-]+}", method=RequestMethod.GET)
	public @ResponseBody CacheRecord cache(@PathVariable String id) {
		ESJSONClient client = service.getClient("web") ;
		CacheRecord record = null; 
		try {
			String json = client.get(id) ;
			if(json != null) {
				Document doc = Document.JSON_SERIALIZER.fromString(json, Document.class) ;
				String[] hword = {} ;
				TermHighlighter highlighter = new TermHighlighter(hword) ;
				record = new CacheRecord(doc, highlighter) ;
			} else {
				record = new CacheRecord() ;
				record.setTitle("Document " + id + " is not found!!!!!!!") ;
			}
		} catch (Exception e) {
	    e.printStackTrace();
    }
		return record ;
	}

	private SearchHitPageIterator getSearchHitPageIterator(Query query) throws Exception {
		ESJSONClient client = service.getClient(query.getIndex()) ;
		String queryJson =  queryBuilder.build(query) ;
		String key = query.getIndex() + ": " + queryJson ;
		SearchHitPageIterator iterator = iteratorCaches.getCachedObject(key) ;
		if(iterator == null) {
			iterator = new SearchHitPageIterator(client, queryJson, 25) ;
			iteratorCaches.putCachedObject(key, iterator) ;
		}
		return iterator ;
	}
	
	//========================================================================================
	@RequestMapping(method=RequestMethod.GET)
	public String getCreateForm(Model model) {
		model.addAttribute(new Query());
		return "account/createForm";
	}
	
	@RequestMapping(method=RequestMethod.POST)
	public @ResponseBody Query create(@RequestBody Query query, HttpServletResponse response) {
		return query ;
	}
}
