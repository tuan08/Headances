package org.headvances.nlp.query2;

import java.io.IOException;

import org.codehaus.jackson.JsonParseException;
import org.codehaus.jackson.map.ObjectMapper;
import org.headvances.nlp.dict.EntityDictionary;
import org.headvances.nlp.dict.SynsetDictionary;
import org.headvances.nlp.query2.match.MatcherResourceFactory;
import org.headvances.nlp.token.analyzer.CommonTokenAnalyzer;
import org.headvances.nlp.token.analyzer.DateTokenAnalyzer;
import org.headvances.nlp.token.analyzer.EmailTokenAnalyzer;
import org.headvances.nlp.token.analyzer.PunctuationTokenAnalyzer;
import org.headvances.nlp.token.analyzer.TimeTokenAnalyzer;
import org.headvances.nlp.token.analyzer.TokenAnalyzer;
import org.headvances.nlp.token.analyzer.USDTokenAnalyzer;
import org.headvances.nlp.token.analyzer.VNDTokenAnalyzer;
import org.headvances.nlp.token.analyzer.VNMobileTokenAnalyzer;
import org.headvances.nlp.token.analyzer.VNPhoneTokenAnalyzer;
import org.junit.Test;

public class QueryUnitTest {
  final static public String LIENHE_QUERY = 
      "{" +
          "  \"name\" : \"test query\" ," +
          "  \"priority\":  1 ," +
          "  \"description\": \"extract the place information\"," +
          "  \"matchmax\" : 3 ," +
          "  \"matchselector\" : \"first\" ," +
          "  \"prematch\": [" +
          "    \"print: call prematch for query test query\", " +
          "    \"if-match: / synset{name=lienhe}  ? $print{match keyword diadiem} : $exit \"" +
          "  ]," +
          "  \"match\": [" +
          "    \"/p synset{name=lienhe} .4. digit .2. entity{type=street}\"," +
          "    \"/p synset{name=lienhe} .4. entity{type=street, district, quarter} .4. entity{type=district, quarter, city}\"" +
          "  ]," +
          "  \"extract\": [" +
          "    \"diadiem:lienhe   =  synset{name=lienhe}\"," +
          "    \"diadiem:duong    =  entity{type = street }\"," +
          "    \"diadiem:phuong   =  entity{type = quarter}\"," +
          "    \"diadiem:quan     =  entity{type = district}\"," +
          "    \"diadiem:thanhpho =  entity{type = city}\"," +
          "    \"diadiem:tinh     =  entity{type = province}\"," +
          "    \"diadiem:quocgia  =  entity{type = country}\"" +
          "  ]," +
          "  \"postmatch\": [" +
          "    \"print: call postmatch for query test query\", " +
          "    \"if: $extractCount{diadiem:*} > 3 ? $print{extract count > 3} : $print{extract count < 3}\", " +
          "    \"if: $extractCount{diadiem:*} > 3 ? $tag{address}\"" +
          "  ]"  +
          "}" ;

  @Test
  public void test() throws Exception {
    SynsetDictionary dict = new SynsetDictionary() ;
    dict.add("lienhe", new String[]{}, new String[]{"liên hệ"}) ;
    EntityDictionary entityDict = new EntityDictionary(EntityDictionary.DICT_RES) ;
    MatcherResourceFactory umFactory = new MatcherResourceFactory(dict, entityDict) ;

    Query query=  create(LIENHE_QUERY);
    query.compile(umFactory) ;

    QueryDocument document = createDocument(
        "title: this is a document title",
        "body:  liên hệ 186 trương định quận hai bà trưng hà nội việt nam"
        ) ;
    QueryContext context = new QueryContext() ;
    query.query(context, document) ;
    context.dump() ;
  }

  protected QueryDocument createDocument(String ... fields) throws Exception {
    QueryDocument document = new QueryDocument() ;
    TokenAnalyzer[] analyzer = {
        PunctuationTokenAnalyzer.INSTANCE, new CommonTokenAnalyzer(), 
        new DateTokenAnalyzer(), new TimeTokenAnalyzer(), 
        new VNDTokenAnalyzer(), new USDTokenAnalyzer(),
        new VNPhoneTokenAnalyzer(), new VNMobileTokenAnalyzer(),
        new EmailTokenAnalyzer()	
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
