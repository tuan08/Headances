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
import org.headvances.util.text.StringUtil;

public class KeywordFilter {
  static private String FILTER_QUERY =
    "{ \n" +
    "  \"name\" : \"keyword:filter\" , \n" +
    "  \"priority\":  1 , \n" +
    "  \"description\": \"Check keywords\", \n" +
    "  \"matchmax\" : 3 , \n" +
    "  \"matchselector\" : \"first\" , \n" +
    "  \"prematch\": [" +
    "  ], \n" +
    "  \"match\": [ \n" +
    "    \"/s word{word=$keywords}\" \n" +
    "  ], \n" +
    "  \"extract\": [ \n" +
    "  ] \n" +
    "} \n" ;

  private Query filterQuery;
  private TextSegmenter textSegmenter ;

  public KeywordFilter(TextSegmenter textSegmenter, String[] keyword) throws Exception {
    this.textSegmenter = textSegmenter;
    filterQuery = createQuery(FILTER_QUERY, StringUtil.joinStringArray(keyword, ", "));
  }
  
  public boolean containKeywords(TokenCollection sentence) throws Exception {
   return containKeywords(filterQuery, sentence);
  }
  
  public TokenCollection[] findSentences(String text) throws Exception {
  	IToken[] token = textSegmenter.segment(text) ;
  	TokenCollection[] collection = SentenceSplitterAnalyzer.INSTANCE.analyze(token) ;
  	List<TokenCollection> holder = new ArrayList<TokenCollection>() ;
  	for(TokenCollection sel : collection) {
  		if(containKeywords(sel)) holder.add(sel); 
  	}
  	return holder.toArray(new TokenCollection[holder.size()]) ;
  }
  
  private boolean containKeywords(Query query, TokenCollection sentence) throws Exception {
    QueryDocument qDocument = new QueryDocument();
    qDocument.add("sentence", sentence.getTokens());

    QueryContext context = new QueryContext() ;
    query.query(context, qDocument) ;

    RuleMatch[] results = context.getRuleMatch();
    if(results != null && results.length > 0) return true ;
    return false ;
  }

  private Query createQuery(String template, String keyword) throws Exception {
    QueryTemplate qtemplate = new QueryTemplate(template);
    qtemplate.setParam("keywords", keyword);

    MatcherResourceFactory umFactory = new MatcherResourceFactory();
    Query query = qtemplate.getCompileQuery(umFactory);
    return query ;
  }
}