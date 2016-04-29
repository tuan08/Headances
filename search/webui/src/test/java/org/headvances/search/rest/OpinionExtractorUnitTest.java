package org.headvances.search.rest;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;

import org.headvances.data.Document;
import org.headvances.data.Entity;
import org.headvances.ml.nlp.opinion.KeywordFilter;
import org.headvances.ml.nlp.opinion.Opinion;
import org.headvances.ml.nlp.opinion.OpinionExtractor;
import org.headvances.ml.nlp.opinion.OpinionWriter;
import org.headvances.nlp.token.IToken;
import org.headvances.nlp.token.TextSegmenter;
import org.headvances.nlp.token.TokenCollection;
import org.headvances.nlp.token.analyzer.SentenceSplitterAnalyzer;
import org.headvances.search.ESClientService;
import org.headvances.search.SearchHitPageIterator;
import org.headvances.util.text.StringUtil;
import org.junit.Test;
/**
 * $Author: Tuan Nguyen$ 
 **/
public class OpinionExtractorUnitTest {
  @Test
  public void testDocumentOpinionExtractor() throws Exception {
    ESClientService service = new ESClientService() ;
    String[] url  = { "192.168.6.21:9300", "192.168.6.22:9300", "192.168.6.23:9300" } ;
    service.connect(url, "web", "webpage" );
    String[] keyword = { "iphone", "samsung galaxy" } ;
    OpinionExtractor oExtractor = new OpinionExtractor(keyword);
    KeywordFilter kFilter = new KeywordFilter(oExtractor.getTextSegmenter(), keyword);

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
        List<TokenCollection> tCollections = process(docs.get(j), oExtractor.getTextSegmenter());

        for(TokenCollection tCollection : tCollections) {
          if(!kFilter.containKeywords(tCollection)) continue;
          owriter.writeText("hasKeyword", tCollection.getOriginalForm());
          Opinion opinion = oExtractor.extract(tCollection);
          if(opinion != null) {
            owriter.writeByLabel(opinion);
            ids.add(opinion.getId()) ;
          } else owriter.writeText("Fail", tCollection.getOriginalForm());
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

  private List<TokenCollection> process(Document doc, TextSegmenter textSegmenter) throws Exception{
    List<TokenCollection> holder = new ArrayList<TokenCollection>();
    String content = getContent(doc);
    String[] text = content.split("\n") ;
    for(String txt: text) {
      IToken[] tokens = textSegmenter.segment(txt) ;
      TokenCollection[] sentence = SentenceSplitterAnalyzer.INSTANCE.analyze(tokens);
      for(TokenCollection tk: sentence) holder.add(tk);
    }
    return holder;
  }

  static public String getContent(Document doc) {
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