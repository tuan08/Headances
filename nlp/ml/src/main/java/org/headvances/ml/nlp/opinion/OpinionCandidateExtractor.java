package org.headvances.ml.nlp.opinion;

import org.headvances.nlp.query2.Query;
import org.headvances.nlp.query2.QueryContext;
import org.headvances.nlp.query2.QueryDocument;
import org.headvances.nlp.query2.QueryTemplate;
import org.headvances.nlp.query2.match.MatcherResourceFactory;
import org.headvances.nlp.query2.match.RuleMatch;
import org.headvances.nlp.token.TokenCollection;

/**
 * $Author: Tuan Nguyen$ 
 **/
public class OpinionCandidateExtractor {
  static private String FILTER_QUERY =
    "{ \n" +
    "  \"name\" : \"candidate:filter\" , \n" +
    "  \"priority\":  1 , \n" +
    "  \"description\": \"Extract the opinion candidate\", \n" +
    "  \"matchmax\" : 3 , \n" +
    "  \"matchselector\" : \"first\" , \n" +
    "  \"prematch\": [" +
    "  ], \n" +
    "  \"match\": [ \n" +
    "    \"/p synset{name=product:sale:marketing|type=multi-word} .5. word{word=$keywords}\", \n" +
    "    \"/p word{word=$keywords} .5. synset{name=product:sale:marketing|type=multi-word}\", \n" +
    "    \"/p synset{name=product:sale:marketing|type=multi-word} .5. word{word=$keywords}\", \n" +
    
    "    \"/p synset{name=product:sale:marketing|type=single-word} .3. word{word=$keywords}\", \n" +
    "    \"/p word{word=$keywords} .3. synset{name=product:sale:marketing|type=single-word}\" \n" +
    "  ], \n" +
    "  \"extract\": [ \n" +
    "  ] \n" +
    "} \n" ;
  
  
  static private String COMMENT_QUERY =
    "{ \n" +
    "  \"name\" : \"candidate:comment\" , \n" +
    "  \"priority\":  1 , \n" +
    "  \"description\": \"Extract the opinion candidate\", \n" +
    "  \"matchmax\" : 3 , \n" +
    "  \"matchselector\" : \"first\" , \n" +
    "  \"prematch\": [" +
    "    \"if-match: / word{word=$keywords} ? $continue : $exit \" \n" +
    "  ], \n" +
    "  \"match\": [ \n" +
    "     \"/p synset{name=opinion:subject|type=multi-word} .7. word{word=$keywords} \", \n" +
    "     \"/p word{word=$keywords} .7. synset{name=opinion:subject|type=multi-word}\", \n" +

    "     \"/p word{word=$keywords} .7. synset{name=opinion:subject} .2. synset{name=opinion:exclamation}\", \n" +
    "     \"/p synset{name=opinion:subject} .2. synset{name=opinion:exclamation} .7. word{word=$keywords}\", \n" +
    "     \"/p synset{name=opinion:exclamation} .2. synset{name=opinion:subject} .7. word{word=$keywords}\", \n" +
    "     \"/p word{word=$keywords} .7. word{word=của} .1. synset{name=opinion:subject}\", \n" +
    "     \"/p synset{name=opinion:subject} .1. word{word=nên} .7. word{word=$keywords}\", \n" +
    
    "     \"/p synset{name=opinion:subject|type=single-word} .2. synset{name=opinion:verb} .7. word{word=$keywords} \", \n" +
    "     \"/p synset{name=opinion:verb} .2. synset{name=opinion:subject|type=single-word} .7. word{word=$keywords} \" \n" +
    "  ], \n" +
    "  \"extract\": [ \n" +
    "  ] \n" +
    "} \n" ;

  static private String TRUST_QUERY =
    "{ \n" +
    "  \"name\" : \"candidate:trust\" , \n" +
    "  \"priority\":  1 , \n" +
    "  \"description\": \"Extract the opinion candidate\", \n" +
    "  \"matchmax\" : 3 , \n" +
    "  \"matchselector\" : \"first\" , \n" +
    "  \"prematch\": [" +
    "    \"if-match: / word{word=$keywords} ? $continue : $exit \" \n" +
    "  ], \n" +
    "  \"match\": [ \n" +
    "     \"/p word{word=$keywords} .3. synset{name=product:taxonomy} .2. synset{name=opinion | type=multi-word} \", \n" +
    "     \"/p synset{name=product:taxonomy} .3. word{word=$keywords} .2. synset{name=opinion | type=multi-word}\", \n" +
    "     \"/p word{word=$keywords} .2. synset{name=opinion|type=multi-word}\", \n" +

    "     \"/p word{word=$keywords} .2. synset{name=product:taxonomy} .2. synset{name=opinion | type=single-word} \", \n" +
    "     \"/p synset{name=product:taxonomy} .2. word{word=$keywords} .2. synset{name=opinion | type=single-word}\", \n" +
    "     \"/p word{word=$keywords} .3. synset{name=nuance} .2. synset{name=opinion | type=single-word} \", \n" +
    "     \"/p word{word=$keywords} .3. synset{name=opinion | type=single-word} .2. synset{name=nuance} \" \n" +
    "  ], \n" +
    "  \"extract\": [ \n" +
    "    \"taxonomy   =  synset{name=product:taxonomy}\"," +
    "    \"opi:neg    =  synset{name=opinion | type=negative}\"," +
    "    \"opi:pos    =  synset{name=opinion | type=positive}\"," +
    "    \"opi:nue    =  synset{name=opinion | type=nuetral}\"," +
    "    \"nua:incr   =  synset{name=nuance | type=increase}\"," +
    "    \"nua:decr   =  synset{name=nuance | type=decrease}\"" +
    "  ] \n" +
    "} \n" ;

  static private String UNTRUST_QUERY =
    "{ \n" +
    "  \"name\" : \"candidate:untrust\" , \n" +
    "  \"priority\":  1 , \n" +
    "  \"description\": \"Extract the opinion candidate\", \n" +
    "  \"matchmax\" : 3 , \n" +
    "  \"matchselector\" : \"first\" , \n" +
    "  \"prematch\": [" +
    "    \"if-match: / word{word=$keywords} ? $continue : $exit \" \n" +
    "  ], \n" +
    "  \"match\": [ \n" +
    "     \"/p word{word=$keywords} .5. synset{name=product:taxonomy} .3. synset{name=opinion} \", \n" +
    "     \"/p synset{name=product:taxonomy} .5. word{word=$keywords} .3. synset{name=opinion}\", \n" +
    
//    "     \"/p synset{name=product:taxonomy} .2. synset{name=opinion}\", \n" +
    
    "     \"/p word{word=$keywords} .5. synset{name=nuance} .3. synset{name=opinion | type=multi-word} \", \n" +
    "     \"/p word{word=$keywords} .5. synset{name=opinion | type=multi-word} .3. synset{name=nuance} \", \n" +
    
    "     \"/p word{word=$keywords} .5. synset{name=opinion | type=multi-word}\", \n" +
//    "     \"/p synset{name=opinion | type=multi-word} .5. word{word=$keywords}\", \n" +

    "     \"/p word{word=$keywords} .1. synset{name=opinion | type=single-word}\" \n" +
    "  ], \n" +
    "  \"extract\": [ \n" +
    "    \"taxonomy   =  synset{name=product:taxonomy}\"," +
    "    \"opi:neg    =  synset{name=opinion | type=negative}\"," +
    "    \"opi:pos    =  synset{name=opinion | type=positive}\"," +
    "    \"opi:nue    =  synset{name=opinion | type=nuetral}\"," +
    "    \"nua:incr   =  synset{name=nuance | type=increase}\"," +
    "    \"nua:decr   =  synset{name=nuance | type=decrease}\"" +
    "  ] \n" +
    "} \n" ;

  private String keyword;
  private MatcherResourceFactory umFactory;
  private Query trustQuery ;
  private Query untrustQuery ;
  private Query commentQuery ;
  
  private Query filterQuery;
  
  public OpinionCandidateExtractor(String keyword, MatcherResourceFactory umFactory) throws Exception{
    this.keyword   = keyword;
    this.umFactory = umFactory;
    
    this.trustQuery = createQuery(TRUST_QUERY, keyword) ;
    this.untrustQuery = createQuery(UNTRUST_QUERY, keyword) ;
    this.commentQuery = createQuery(COMMENT_QUERY, keyword) ;
    this.filterQuery = createQuery(FILTER_QUERY, keyword);
  }

  public Opinion extract(TokenCollection sentence) throws Exception {
    Opinion opinion = query(filterQuery, sentence);
    if(opinion != null) return null;
    opinion =  query(commentQuery, sentence) ;
    if(opinion == null) opinion = query(trustQuery, sentence) ;
    if(opinion == null) opinion = query(untrustQuery, sentence) ;
    
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
    rMatch.setExtract("keyword", this.keyword);

    opinion.setOpinion(rMatch.getTokenCollection().getOriginalForm());
    opinion.setRuleMatch(rMatch);
    opinion.addTag(query.getName());
    return opinion ;
  }
  
  private Query createQuery(String template, String keyword) throws Exception {
  	QueryTemplate qtemplate = new QueryTemplate(template);
  	qtemplate.setParam("keywords", keyword);
  	Query query = qtemplate.getCompileQuery(this.umFactory);
  	return query ;
  }
}
