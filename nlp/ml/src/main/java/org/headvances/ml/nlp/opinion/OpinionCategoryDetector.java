package org.headvances.ml.nlp.opinion;

import org.headvances.nlp.query2.Query;
import org.headvances.nlp.query2.QueryContext;
import org.headvances.nlp.query2.QueryDocument;
import org.headvances.nlp.query2.QueryTemplate;
import org.headvances.nlp.query2.match.RuleMatch;
import org.headvances.nlp.query2.match.MatcherResourceFactory;
import org.headvances.nlp.token.TokenCollection;

public class OpinionCategoryDetector {
  static private String DETECT_QUERY =
    "{ \n" +
    "  \"name\" : \"Product Opinion Query\" , \n" +
    "  \"priority\":  1 , \n" +
    "  \"description\": \"Detect the category\", \n" +
    "  \"matchmax\" : 3 , \n" +
    "  \"matchselector\" : \"first\" , \n" +
    "  \"match\": [ \n" +
    "    \"/s synset{name=product:taxonomy|type=$category} .7. synset{name=opinion}\", \n" +
    "    \"/s synset{name=opinion} .7. synset{name=product:taxonomy|type=$category}\" \n" +
    "  ], \n" +
    "  \"extract\": [ \n" +
    "  ] \n" +
    "} \n" ;

  static public  String[] CATEGORIES = { 
    "battery", "design", "sound", "display", "size", "keypad", "weight", 
    "camera", "speed", "usability", "reception", "feature", "problem", "price"
  };

  private QueryTemplate cTemplate;
  private MatcherResourceFactory umFactory;

  public OpinionCategoryDetector(MatcherResourceFactory umFactory) throws Exception {
    this.cTemplate = new QueryTemplate(DETECT_QUERY);
    this.umFactory = umFactory;
  }

  public void detect(Opinion opinion, TokenCollection sentence) throws Exception {
    QueryDocument qDocument = new QueryDocument() ;
    qDocument.add("sentence", sentence.getTokens());

    for(String cat : CATEGORIES){
      cTemplate.setParam("category", cat);

      QueryContext context = new QueryContext() ;
      Query query = cTemplate.getCompileQuery(umFactory) ;
      query.query(context, qDocument) ;

      RuleMatch[] results = context.getRuleMatch();
      if(results == null || results.length == 0) continue;
      opinion.addCategory("product:" + cat);
    }
    if(opinion.getCategory() == null) {
      opinion.addCategory("product:general");
    }
  }
}
