package org.headvances.ml.nlp.opinion;

import java.io.IOException;

import org.codehaus.jackson.JsonParseException;
import org.codehaus.jackson.map.ObjectMapper;
import org.headvances.nlp.query2.Query;
import org.headvances.nlp.query2.QueryContext;
import org.headvances.nlp.query2.QueryDocument;
import org.headvances.nlp.query2.QueryTemplate;
import org.headvances.nlp.query2.match.MatcherResourceFactory;
import org.headvances.nlp.token.analyzer.CommonTokenAnalyzer;
import org.headvances.nlp.token.analyzer.PunctuationTokenAnalyzer;
import org.headvances.nlp.token.analyzer.TokenAnalyzer;
import org.headvances.nlp.token.analyzer.USDTokenAnalyzer;
import org.headvances.nlp.token.analyzer.VNDTokenAnalyzer;
import org.junit.Test;

public class OpinionQueryUnitTest  {
  static String QUERY = 
    "{ \n" +
    "  \"name\" : \"Product Opinion Query\" , \n" +
    "  \"priority\":  1 , \n" +
    "  \"description\": \"extract the opinion information\", \n" +
    "  \"matchmax\" : 3 , \n" +
    "  \"matchselector\" : \"first\" , \n" +
    "  \"match\": [ \n" +
    "    \"/s word{word=$keywords} .8. synset{name=opinion}\", \n" +
    "    \"/s synset{name=opinion} .5. word{word=$keywords}\" \n" +
    "  ], \n" +
    "  \"extract\": [ \n" +
    "  ] \n" +
    "} \n" ;

  @Test
  public void test() throws Exception {
    MatcherResourceFactory umFactory = new MatcherResourceFactory();

    QueryTemplate template = new QueryTemplate(QUERY) ;
    template.setParam("keywords", "iphone");
    template.setParam("type", "electronic");
    Query query = template.getCompileQuery(umFactory) ;

    String text = "Chiếc Iphone này rất đẹp" ;
    
    QueryDocument document = createDocument(
        "sentence:" + text
    );
   
    QueryContext context = new QueryContext() ;
    query.query(context, document) ;
    context.dump() ;
  }
  
  protected QueryDocument createDocument(String ... fields) throws Exception {
    QueryDocument document = new QueryDocument() ;
    TokenAnalyzer[] analyzer = {
      PunctuationTokenAnalyzer.INSTANCE, new CommonTokenAnalyzer(),
      new VNDTokenAnalyzer(), new USDTokenAnalyzer()
    };
    for(String selField : fields) {
      int idx      = selField.indexOf(':') ;
      String fname = selField.substring(0, idx).trim() ;
      String data  = selField.substring(idx + 1).trim() ;
      document.add(fname, data, analyzer) ;
    }
    return document ;
  }

  static public Query create(String json) throws JsonParseException, IOException {
    ObjectMapper mapper = new ObjectMapper(); // can reuse, share globally
    return mapper.readValue(json , Query.class);
  }
}