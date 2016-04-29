package org.headvances.search.rest;

import org.elasticsearch.index.query.BoolQueryBuilder;
import org.elasticsearch.index.query.QueryBuilders;
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

		String[] contentType = query.getContentType() ;
		if(contentType != null && contentType.length > 0) {
			BoolQueryBuilder typeQuery = QueryBuilders.boolQuery() ;
			for(String sel : contentType) {
				typeQuery.should(QueryBuilders.termQuery("tags", "content:" + sel)) ;
			}
			all.must(typeQuery) ;
		}
		if(!"all".equals(query.getPageType())) {
			all.must(QueryBuilders.termQuery("tags", "content:" + query.getPageType())) ;
		}
		return all.toString() ;
	}
}