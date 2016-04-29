package org.headvances.search.rest.webmonitor;

import java.util.ArrayList;
import java.util.List;

import org.headvances.data.Document;
import org.headvances.data.Entity;
import org.headvances.ml.nlp.opinion.Opinion;
import org.headvances.ml.nlp.opinion.OpinionDocument;
import org.headvances.ml.nlp.opinion.OpinionExtractor;
import org.headvances.search.ESClientService;
import org.headvances.search.SearchHitPageIterator;
import org.headvances.search.rest.Query;
import org.headvances.search.rest.QueryBuilder;
import org.headvances.util.text.StringUtil;
/**
 * $Author: Tuan Nguyen$ 
 **/
public class WebMonitorExecutor extends Thread {
	private ESClientService service ;
	private WebMonitor wmonitor ;
	private OpinionExtractor opinionExtractor ;
	
	public WebMonitorExecutor(ESClientService service, WebMonitor wmonitor) throws Exception {
		this.service = service; 
		String[] entity = StringUtil.toStringArray(wmonitor.getEntity()) ;
		this.opinionExtractor = new OpinionExtractor(entity) ;
		this.wmonitor = wmonitor ;
	}
	
	public void run() {
		ExecuteInfo executeInfo = new ExecuteInfo() ;
		executeInfo.setStartTime(System.currentTimeMillis()) ;
		wmonitor.setExecuteInfo(executeInfo) ;
		try { 
			QueryBuilder queryBuilder = new QueryBuilder() ;
			Query query = new Query() ;
			query.setQuery(wmonitor.getEntity()) ;
			query.setContentType(new String[] {"article", "blog", "forum"}) ;
			String queryJson =  queryBuilder.build(query) ;
			SearchHitPageIterator	iterator = 
				new SearchHitPageIterator(service.getClient("web"), queryJson, 200, 1) ;
			executeInfo.setTotalRecord(iterator.getAvailable()) ;
			for(int i = 1; i <= iterator.getAvailablePage(); i++) {
				long start = System.currentTimeMillis() ;
				List<Document> docs = iterator.getPageAsDocument(i) ; 
				process(docs);
				executeInfo.setElapsedTime(System.currentTimeMillis()) ;
				long exec = System.currentTimeMillis() - start ;
				System.out.println("Process " + docs.size() + " in " + exec + "ms") ;
				iterator.clearCache() ;
			}
		} catch(Exception ex) {
			ex.printStackTrace() ;
		} finally {
			executeInfo.setFinishTime(System.currentTimeMillis()) ;
		}
	}
	
	private void process(List<Document> docs) throws Exception {
		for(int i = 0; i < docs.size(); i++) {
			Document doc = docs.get(i) ;
			String contentType = getContentType(doc) ;
			wmonitor.addContentDistribution(contentType, 1) ;
			List<Opinion> opinions = findOpinion(doc);
			for(int j = 0; j < opinions.size(); j++) {
				wmonitor.addOpinion(contentType, opinions.get(j)) ;
			}
		}
		wmonitor.getExecuteInfo().addProcessedRecord(docs.size()) ;
	}
	
	static int OPINION_ID_TRACKER = 0 ;
	
	private List<Opinion>  findOpinion(Document doc) throws Exception {
		String content = getContent(doc) ;
		String[] text = content.split("\n") ;
		List<Opinion> holder = new ArrayList<Opinion>() ;
		for(int i = 0; i < text.length; i++) {
		  extractOpinion(holder, new OpinionDocument(doc.getId(), text[i])) ;
		}
		return holder ;
	}
	
	private void  extractOpinion(List<Opinion> holder, OpinionDocument odoc) throws Exception {
		List<Opinion> opinions = opinionExtractor.extract(odoc) ;
		for(int i = 0; i < opinions.size(); i++) {
			holder.add(opinions.get(i)) ;
		}
	}
	
	private String getContentType(Document doc) {
		if(doc.hasTag("content:article")) return "article" ;
		if(doc.hasTag("content:blog")) return "blog" ;
		if(doc.hasTag("content:forum")) return "forum" ;
		if(doc.hasTag("content:product")) return "product" ;
		if(doc.hasTag("content:classified")) return "classified" ;
		if(doc.hasTag("content:job")) return "job" ;
		return "other" ;
	}
	
	private String getContent(Document doc) {
		Entity entity = doc.getEntity("mainContent") ;
		StringBuilder b = new StringBuilder() ;
		String title = entity.getString("title") ;
		if(title != null) b.append(title).append(".\n") ;
		String description = entity.getString("description") ;
		if(description != null) b.append(description).append(".\n") ;
		String content = entity.getString("content") ;
		if(content != null) b.append(content).append(".\n") ;
		return b.toString() ;
	}
}