package org.headvances.search.rest;

import java.util.HashSet;
import java.util.List;

import org.headvances.data.Document;
import org.headvances.ml.nlp.opinion.DocumentOpinionExtractor;
import org.headvances.ml.nlp.opinion.Opinion;
import org.headvances.ml.nlp.opinion.OpinionExtractor;
import org.headvances.ml.nlp.opinion.OpinionWriter;
import org.headvances.search.ESClientService;
import org.headvances.search.SearchHitPageIterator;
import org.headvances.util.text.StringUtil;
import org.junit.Test;

/**
 * $Author: Tuan Nguyen$ 
 **/
public class DocumentOpinionExtractorUnitTest {
  @Test
  public void testDocumentOpinionExtractor() throws Exception {
    ESClientService service = new ESClientService() ;
    String[] url  = { "192.168.6.21:9300", "192.168.6.22:9300", "192.168.6.23:9300" } ;
    service.connect(url, "web", "webpage" );
    String[] keyword = { 
        "iphone", "xt720"
    } ;
    OpinionExtractor oExtractor = new OpinionExtractor(keyword);
    DocumentOpinionExtractor extractor = new DocumentOpinionExtractor(oExtractor);
    Query query = new Query() ;
    query.setQuery(StringUtil.joinStringArray(keyword));
    query.setContentType(new String[] {"article", "blog", "forum"}) ;
    QueryBuilder qb = new QueryBuilder() ;
    String queryJson =  qb.build(query) ;
    SearchHitPageIterator	iterator = 
      new SearchHitPageIterator(service.getClient("web"), queryJson, 200, 1) ;
    OpinionWriter owriter = new OpinionWriter("target/opinion") ;
    HashSet<String> ids = new HashSet<String>() ;
    int count = 0 ;
    for(int i = 1; i <= iterator.getAvailablePage(); i++) {
      long start = System.currentTimeMillis() ;
      List<Document> docs = iterator.getPageAsDocument(i) ; 
      for(int j = 0; j < docs.size(); j++) {
        List<Opinion> ops = extractor.process(docs.get(j)) ;

        for(Opinion sel : ops) {
          if(ids.contains(sel.getId())) continue ;
          owriter.writeByLabel(sel) ;
          if("-1".equals(sel.getLabel())) {
            owriter.write("train", sel) ;
          }
          ids.add(sel.getId()) ;
        }
        count++ ;
      }
      owriter.flush() ;
      owriter.report(System.out) ;
      iterator.clearCache() ;
      System.out.println("process " + docs.size() + " in " + (System.currentTimeMillis() - start) + "ms");
      if(count > 30000) break ;
    }
    owriter.closeWriter() ;
  }
}
