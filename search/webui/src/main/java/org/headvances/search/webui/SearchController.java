package org.headvances.search.webui;

import java.util.List;

import javax.servlet.http.HttpSession;
import javax.validation.Valid;

import org.headvances.data.Document;
import org.headvances.search.ESClientService;
import org.headvances.search.ESJSONClient;
import org.headvances.search.SearchHitPageIterator;
import org.headvances.search.TermHighlighter;
import org.headvances.util.cache.InmemoryCache;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

@Controller
public class SearchController {
	@Autowired 
	private ESClientService service ;
	@Autowired
	private LabelDocumentGateway labelDocumentGateway ;
	
	private InmemoryCache<String, SearchHitPageIterator> iteratorCaches ;
	private LabelHistories lbHistories = new LabelHistories() ;
	private QueryBuilder    queryBuilder = new QueryBuilder() ;
	
	public SearchController() {
		iteratorCaches = new InmemoryCache<String, SearchHitPageIterator>("cache", 1000) ;
		iteratorCaches.setLiveTime(10 * 60 * 1000) ;
	}
	
	@RequestMapping("/")
  public String welcome(Model model) {
		model.addAttribute("query", new Query()) ;
		model.addAttribute("filterOptions", FilterOptions.INSTANCE) ;
		model.addAttribute("indices", service.getAvailableIndex()) ;
	  return "search/Search";
  }
	
	@RequestMapping("search")
  public String search(HttpSession session, Model model, Query query) {
		SearchContext context = new SearchContext(session) ;
		context.setCurrentQuery(query) ;
		try {
			SearchHitPageIterator iterator = getSearchHitPageIterator(query) ;
			TermHighlighter highlighter = new TermHighlighter(query.getQuery()) ;
			List<SearchRecord> records = 
				new SearchRecords(iterator.getPage(query.getPage()), highlighter, lbHistories) ;

			model.addAttribute("filterOptions", FilterOptions.INSTANCE) ;
			model.addAttribute("iterator", iterator) ;
			model.addAttribute("records",  records) ;
			model.addAttribute("query",    query) ;
			model.addAttribute("queryJson", iterator.getJSONQuery()) ;
			model.addAttribute("indices",  service.getAvailableIndex()) ;
			model.addAttribute("uiconfig", context.getUIConfig()) ;
		} catch (Exception e) {
	    e.printStackTrace();
    }
		return "search/Search";
  }
	
	@RequestMapping(value = "search", params="setPage")
  public String search(HttpSession session, Model model, Query query, 
  		                 @RequestParam(required = true) int setPage) {
		query.setPage(setPage) ;
		return search(session, model, query) ;
	}
	
	@RequestMapping(value = "search", params="setPageType")
  public String setPageType(HttpSession session, Model model, Query query, 
  		                      @RequestParam(required = true) String setPageType) {
		query.rmTag(FilterOptions.INSTANCE.getPageTypeTags()) ;
		if(!"clear".equals(setPageType)) {
			query.addTag(setPageType) ;
		}
		query.setPage(1) ;
		return search(session, model, query) ;
	}
	
	@RequestMapping(value = "search", params="setContentType")
  public String setContentType(HttpSession session, Model model, Query query, 
  		                         @RequestParam(required = true) String setContentType) {
		query.rmTag(FilterOptions.INSTANCE.getContentTags()) ;
		if(!"clear".equals(setContentType)) {
			query.addTag(setContentType) ;
		}
		query.setPage(1) ;
		return search(session, model, query) ;
	}
	
	@RequestMapping(value = "search", params="setLanguage")
  public String setLanguage(HttpSession session, Model model, Query query, 
  		                      @RequestParam(required = true) String setLanguage) {
		query.rmTag(FilterOptions.INSTANCE.getLanguageTags()) ;
		if(!"clear".equals(setLanguage)) {
			query.addTag(setLanguage) ;
		}
		query.setPage(1) ;
		return search(session, model, query) ;
	}
	
	@RequestMapping(value = "search", params="setSuspectTag")
  public String setSuspectTag(HttpSession session, Model model, Query query, 
  		                      @RequestParam(required = true) String setSuspectTag) {
		query.rmTag(FilterOptions.INSTANCE.getSuspectTags()) ;
		if(!"clear".equals(setSuspectTag)) {
			query.addTag(setSuspectTag) ;
		}
		query.setPage(1) ;
		return search(session, model, query) ;
	}
	
	@RequestMapping(value = "search", params="setOtherTag")
  public String setOtherTag(HttpSession session, Model model, Query query, 
  		                      @RequestParam(required = true) String setOtherTag) {
		query.rmTag(FilterOptions.INSTANCE.getOtherTags()) ;
		if(!"clear".equals(setOtherTag)) {
			query.addTag(setOtherTag) ;
		}
		query.setPage(1) ;
		return search(session, model, query) ;
	}
	
	@RequestMapping(value = "search", params="setPredictTag")
  public String setPredictTag(HttpSession session, Model model, Query query, 
                             @RequestParam(required = true) String setPredictTag) {
    query.rmTag(FilterOptions.INSTANCE.getPredictTags()) ;
    if(!"clear".equals(setPredictTag)) {
      query.addTag(setPredictTag) ;
    }
    query.setPage(1) ;
    return search(session, model, query) ;
  }

	@RequestMapping(value = "search", params="setDeep")
  public String setDeep(HttpSession session, Model model, Query query, 
  		                      @RequestParam(required = true) int setDeep) {
		query.setDeep(setDeep) ;
		query.setPage(1) ;
		return search(session, model, query) ;
	}
	
	@RequestMapping(value = "search", params="setSite")
  public String setSite(HttpSession session, Model model, Query query, 
  		                  @RequestParam(required = true) String setSite) {
		query.setSite(setSite) ;
		query.setPage(1) ;
		return search(session, model, query) ;
	}

	@RequestMapping(value = "search", params="setIndex")
	public String setIndex(HttpSession session, Model model, Query query, 
		                   	 @RequestParam(required = true) String setIndex) {
		query.setIndex(setIndex) ;
		query.setPage(1) ;
		return search(session, model, query) ;
	}
	
	@RequestMapping(value = "search", params="clearCache")
	public String clearCache(HttpSession session, Model model, Query query, 
		                   	   @RequestParam(required = true) String clearCache) {
		query.setPage(1) ;
		iteratorCaches.clearCache() ;
		return search(session, model, query) ;
	}
	
	@RequestMapping(value = "search/label")
  public  @ResponseBody String label(HttpSession session, 
  		                               @RequestParam(required = true) String label,
  		                               @RequestParam(required = true) String docId) {
		SearchContext context = new SearchContext(session) ;
		Query query = context.getCurrentQuery() ;
		ESJSONClient client = service.getClient(query.getIndex()) ;
		try {
			String json = client.get(docId) ;
			Document doc = Document.JSON_SERIALIZER.fromString(json, Document.class) ;
			doc.setLabels(new String[] { label }) ;
			labelDocumentGateway.send(doc) ;
			lbHistories.label(docId, label) ;
		} catch (Exception e) {
	    e.printStackTrace();
	    return "error" ;
    }
		return "ok" ;
	}
	
	@RequestMapping(value = "search/cache", params="id")
  public String cache(HttpSession session, Model model, 
  		                @RequestParam(required = true) String id) {
		SearchContext context = new SearchContext(session) ;
		Query query = context.getCurrentQuery() ;
		ESJSONClient client = service.getClient(query.getIndex()) ;
		try {
			String json = client.get(id) ;
			SearchRecord record = null ;
			if(json != null) {
				Document doc = Document.JSON_SERIALIZER.fromString(json, Document.class) ;
				TermHighlighter highlighter = new TermHighlighter(query.getQuery()) ;
				record = new SearchRecord(doc, highlighter, true) ;
			} else {
				record = new SearchRecord() ;
				record.setTitle("Document " + id + " is not found!!!!!!!") ;
			}
			model.addAttribute("record",  record) ;
			model.addAttribute("query",    query) ;
		} catch (Exception e) {
	    e.printStackTrace();
    }
		return "search/Cache";
	}
	
	@RequestMapping("search/uiconfig")
  public String uiconfig(HttpSession session, Model model) {
		SearchContext context = new SearchContext(session) ;
		model.addAttribute("uiconfig", context.getUIConfig() ) ;
	  return "search/config/UIConfig";
  }
	
	@RequestMapping(value="search/uiconfig", method=RequestMethod.POST)
	public String uiconfig(HttpSession session, 
			                   @Valid UIConfig config, BindingResult result) {
		if (result.hasErrors()) {
			return "/search/config/UIConfig" ;
		}
		SearchContext context = new SearchContext(session) ;
		context.setUIConfig(config) ;
		return "redirect:/" ;
	}
	
	private SearchHitPageIterator getSearchHitPageIterator(Query query) throws Exception {
		ESJSONClient client = service.getClient(query.getIndex()) ;
		String queryJson =  queryBuilder.build(query) ;
		String key = query.getIndex() + ": " + queryJson ;
		SearchHitPageIterator iterator = iteratorCaches.getCachedObject(key) ;
		if(iterator == null) {
			iterator = new SearchHitPageIterator(client, queryJson) ;
			iteratorCaches.putCachedObject(key, iterator) ;
		}
		return iterator ;
	}
}