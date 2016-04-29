package org.headvances.search.rest;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;

import org.headvances.data.Document;
import org.headvances.data.Entity;
import org.headvances.ml.nlp.opinion.KeywordFilter;
import org.headvances.ml.nlp.opinion.Opinion;
import org.headvances.ml.nlp.opinion.OpinionDocument;
import org.headvances.ml.nlp.opinion.OpinionExtractor;
import org.headvances.ml.nlp.opinion.OpinionReviewer;
import org.headvances.ml.nlp.opinion.OpinionWriter;
import org.headvances.nlp.token.TokenCollection;
import org.headvances.search.ESClientService;
import org.headvances.search.SearchHitPageIterator;
import org.headvances.util.FileUtil;
import org.headvances.util.IOUtil;
import org.headvances.util.text.StringUtil;
/**
 * $Author: Tuan Nguyen$ 
 **/
public class OpinionExtractorTool {
  private OpinionExtractor oExtractor ;
  private KeywordFilter    kFilter ;
  private OpinionWriter    owriter ;
  private HashSet<String>  opinionIds = new HashSet<String>() ;
  private String[] keyword;

  public OpinionExtractorTool(String[] keyword) throws Exception {
    this.keyword = keyword;
    oExtractor = new OpinionExtractor(keyword);
    kFilter = new KeywordFilter(oExtractor.getTextSegmenter(), keyword);
    owriter = new OpinionWriter("target/opinion") ;
  }

  public void processFromSearchEngine() throws Exception {
    ESClientService service = new ESClientService() ;
    String[] url  = { "192.168.6.21:9300", "192.168.6.22:9300", "192.168.6.23:9300" } ;
    service.connect(url, "web", "webpage" );

    Query query = new Query() ;
    query.setQuery(StringUtil.joinStringArray(keyword));
    query.setContentType(new String[] {"article", "blog", "forum"}) ;
    QueryBuilder qb = new QueryBuilder() ;
    String queryJson =  qb.build(query) ;
    SearchHitPageIterator	iterator = new SearchHitPageIterator(service.getClient("web"), queryJson, 200, 1) ;
    int count = 0 ;
    for(int i = 1; i <= iterator.getAvailablePage(); i++) {
      long start = System.currentTimeMillis() ;
      List<Document> docs = iterator.getPageAsDocument(i) ; 
      for(int j = 0; j < docs.size(); j++) {
        process(docs.get(j));
        count++ ;
      }
      owriter.report(System.out) ;
      iterator.clearCache() ;
      System.out.println("process " + docs.size() + " in " + (System.currentTimeMillis() - start) + "ms");
//      if(count > 30000) break ;
    }
  }

  public void processFromTextFile(String[] samples, boolean isReview) throws Exception {
    for(int i = 1; i <= samples.length; i++){
      processFromFile("file:" + samples[i-1], i, isReview);
    }
  }

  public void processFromFile(String sample, int id, boolean isReview) throws Exception {
    if(isReview)System.out.println("Reviewing file: " + sample);
    else System.out.println("Processing file: " + sample);
    InputStreamReader inputStreamReader = new InputStreamReader(IOUtil.loadRes(sample), "utf8");
    BufferedReader reader = new BufferedReader(inputStreamReader);

    String line = null;
    int count = 0 ;
    while((line = reader.readLine()) != null){
      count++ ;
      line = line.trim() ;
      int idx = line.indexOf("\t");
      String sentence = line.substring(idx + 1, line.length()).trim();
      if(isReview) review(id*count + "", sentence);
      else process(id*count + "", sentence);
      
      if(count%50 == 0) owriter.report(System.out) ;
    }

    System.out.println("Done!");
  }

  public void close() throws Exception {
    owriter.closeWriter() ;
  }

  public void process(Document doc) throws Exception {
    process(doc.getId(), getContent(doc));
  }

  private void process(String id, String text) throws Exception {
    TokenCollection[] sentences = kFilter.findSentences(text) ;
    for(TokenCollection tCollection : sentences) {
      if(tCollection.getNormalizeForm().length() == 0) continue;

      List<Opinion> opinions = oExtractor.extract(new OpinionDocument(id, tCollection.getOriginalForm()));

      if(opinions == null || opinions.size() == 0) {
        Opinion opinion = new Opinion() ;
        opinion.setOpinion(tCollection.getOriginalForm()) ;
        opinion.setLabel("keyword") ;
        opinion.computeId(id, tCollection) ;

        opinions = new ArrayList<Opinion>();
        opinions.add(opinion);
      }

      for(Opinion opinion: opinions){
        if(opinionIds.contains(opinion.getId())) continue ;
        normalize(opinion);
        owriter.writeByLabel(opinion);
//        owriter.writeFile(opinion.getLabel(), opinion);
        opinionIds.add(opinion.getId()) ;
      }
    }
  }

  private void review(String id, String text) throws Exception {
    TokenCollection[] sentences = kFilter.findSentences(text) ;
    OpinionReviewer reviewer = new OpinionReviewer(oExtractor.getTextSegmenter());

    for(TokenCollection tCollection : sentences) {
      if(tCollection.getNormalizeForm().length() == 0) continue;
      for(Opinion rOp: reviewer.review(tCollection.getOriginalForm())){
        if(rOp != null){
          String query = rOp.getTag()[0];
          query = query.replace(':', '.');
          rOp.setLabel(query);
        } else {
          rOp = new Opinion();
          rOp.setOpinion(tCollection.getOriginalForm());
          rOp.setLabel("fail");
        }
        normalize(rOp);
        owriter.write("review", rOp);
      }
    }
  }

  private void normalize(Opinion opinion){
    String string = opinion.getOpinion();

    string = string.replace("\n", "") ;
    string = string.replace("\r", "") ;

    opinion.setOpinion(string);
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

  public static void main(String[] args) throws Exception{
    String[] keyword = {"iphone", "samsung galaxy", "htc desire", "nokia lumia", "xt720"};
    OpinionExtractorTool tool = new OpinionExtractorTool(keyword);
//    tool.processFromSearchEngine();
//        String[] files = new String[]{
//            "file:C:\\Users\\Admin\\Desktop\\iphone\\label\\i.txt"
//        };
//        tool.processFromTextFile(files, false);
   
    String[]  files = FileUtil.findFiles("C:\\Users\\Admin\\Desktop\\all-keyword\\search", ".*\\.txt");
    tool.processFromTextFile(files, false);
    tool.close();
  }
}