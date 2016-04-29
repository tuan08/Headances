package org.headvances.ml.nlp.opinion;

import java.util.ArrayList;
import java.util.List;

import org.headvances.nlp.query2.Query;
import org.headvances.nlp.query2.QueryContext;
import org.headvances.nlp.query2.QueryDocument;
import org.headvances.nlp.query2.QueryTemplate;
import org.headvances.nlp.query2.match.MatcherResourceFactory;
import org.headvances.nlp.query2.match.RuleMatch;
import org.headvances.nlp.token.IToken;
import org.headvances.nlp.token.TextSegmenter;
import org.headvances.nlp.token.TokenCollection;
import org.headvances.nlp.token.analyzer.SentenceSplitterAnalyzer;

public class OpinionReviewer {

  static private String FILTER_BY_TAXONOMY =
    "{ \n" +
    "  \"name\" : \"review:taxonomy\" , \n" +
    "  \"priority\":  1 , \n" +
    "  \"description\": \"Extract the opinion candidate\", \n" +
    "  \"matchmax\" : 3 , \n" +
    "  \"matchselector\" : \"first\" , \n" +
    "  \"prematch\": [" +
    "  ], \n" +
    "  \"match\": [ \n" +
    "    \"/s synset{name=product:taxonomy}\" \n" +
    "  ], \n" +
    "  \"extract\": [ \n" +
    "  ] \n" +
    "} \n" ;
  
  static private String FILTER_BY_SUBJECT =
    "{ \n" +
    "  \"name\" : \"review:subject\" , \n" +
    "  \"priority\":  1 , \n" +
    "  \"description\": \"Extract the opinion candidate\", \n" +
    "  \"matchmax\" : 3 , \n" +
    "  \"matchselector\" : \"first\" , \n" +
    "  \"prematch\": [" +
    "  ], \n" +
    "  \"match\": [ \n" +
    "    \"/s synset{name=opinion:subject|type=multi-word}\", \n" +
    "    \"/s synset{name=opinion:subject|type=single-word} .2. synset{name=opinion:exclamation}\", \n" +
    "    \"/s synset{name=opinion:subject|type=single-word} .2. synset{name=opinion}\" \n" +
    "  ], \n" +
    "  \"extract\": [ \n" +
    "  ] \n" +
    "} \n" ;
  
  private Query subjectQuery;
  private Query taxonomyQuery;
  
  private TextSegmenter textSegmenter;
  
  public OpinionReviewer(TextSegmenter textSegmenter) throws Exception{
    this.textSegmenter = textSegmenter;
    subjectQuery = createQuery(FILTER_BY_SUBJECT, "product");
    taxonomyQuery = createQuery(FILTER_BY_TAXONOMY, "product");
  }
  
  public List<Opinion> review(String text) throws Exception {
    List<Opinion> holder = new ArrayList<Opinion>();
    
    IToken[] token = textSegmenter.segment(text) ;
    TokenCollection[] collection = SentenceSplitterAnalyzer.INSTANCE.analyze(token) ;

    for(TokenCollection tCollection: collection){
      Opinion op = review(tCollection);
      holder.add(op);
    }
    return holder;
  }
  
  public Opinion review(TokenCollection sentence) throws Exception {
    Opinion opinion = query(subjectQuery, sentence);
    if(opinion == null) opinion = query(taxonomyQuery, sentence);
    return opinion ;
  }
  
  private Opinion query(Query query, TokenCollection sentence) throws Exception {
    QueryDocument qDocument = new QueryDocument();
    qDocument.add("sentence", sentence.getTokens());

    QueryContext context = new QueryContext() ;
    query.query(context, qDocument) ;

    RuleMatch[] results = context.getRuleMatch();
    if(results == null || results.length == 0) return null ;

    Opinion opinion = new Opinion();
    RuleMatch rMatch = results[0];

    opinion.setOpinion(rMatch.getTokenCollection().getNormalizeForm());
    opinion.setRuleMatch(rMatch);
    opinion.addTag(query.getName());
    return opinion ;
  }
  
  private Query createQuery(String template, String keyword) throws Exception {
    QueryTemplate qtemplate = new QueryTemplate(template);
    MatcherResourceFactory umFactory = new MatcherResourceFactory();
    Query query = qtemplate.getCompileQuery(umFactory);
    return query ;
  }
}
