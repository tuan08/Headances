package org.headvances.search.webui;

import org.elasticsearch.index.query.BoolQueryBuilder;
import org.elasticsearch.index.query.QueryBuilders;
import org.elasticsearch.index.query.TermQueryBuilder;
import org.elasticsearch.index.query.TextQueryBuilder;
import org.headvances.util.text.StringUtil;
/**
 * $Author: Tuan Nguyen$ 
 **/
public class QueryBuilder {
	public String build(Query query) {
		BoolQueryBuilder all = QueryBuilders.boolQuery() ;
		String tquery = query.getQuery() ;
		if(!StringUtil.isEmpty(tquery)) {
			BoolQueryBuilder textQuery = QueryBuilders.boolQuery() ;
			if(query.getTitle()) {
				TextQueryBuilder fbuilder = QueryBuilders.textPhrase("entities.mainContent.title", query.getQuery()) ;
				fbuilder.boost(2) ;
				textQuery.should(fbuilder) ;
			}
			if(query.getDescription()) {
				TextQueryBuilder fbuilder = QueryBuilders.text("entities.mainContent.description", query.getQuery()) ;
				fbuilder.boost(1.5f) ;
				textQuery.should(fbuilder) ;
			}
			if(query.getContent()) {
				TextQueryBuilder fbuilder = QueryBuilders.text("entities.mainContent.content", query.getQuery()) ;
				fbuilder.slop(30) ;
				textQuery.should(fbuilder) ;
			}
			all.must(textQuery) ;
		}

		String[] tag = query.getTagsArray() ;
		if(tag.length > 0) {
			for(String sel : tag) {
				all.must(QueryBuilders.termQuery("tags", sel)) ;
			}
		}
		
		int deep = query.getDeep() ;
		if(deep > 0) {
			TermQueryBuilder deepQuery = QueryBuilders.termQuery("entities.htmlLink.deep", deep) ;
			all.must(deepQuery) ;
		}

		String site = query.getSite() ;
		if(!StringUtil.isEmpty(site)) {
			TermQueryBuilder siteQuery = QueryBuilders.termQuery("entities.htmlLink.url", site) ;
			all.must(siteQuery) ;
		}
		return all.toString() ;
	}
}