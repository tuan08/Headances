package org.headvances.html.dom.extract;

import org.headvances.data.Document;
import org.headvances.html.dom.TDocument;
import org.headvances.html.fetcher.Fetcher;
import org.headvances.html.fetcher.HttpClientFetcher;
import org.junit.Test;
/**
 * $Author: Tuan Nguyen$ 
 **/
public class MainContentExtractorUnitTest {
  @Test
  public void test() throws Exception {
  	String url = 
    	"http://www.aia.com.vn/vn/recruitment/for-consultants/agents/agent-recruitment" ;
    
  	Fetcher fetcher = new HttpClientFetcher();
    Document hdoc = fetcher.fetch(url);
    
    DocumentExtractor extractor = new DocumentExtractor() ;
    TDocument tdoc = new TDocument("", url, hdoc.getContent()) ;
    //TNodePrinter visitor = new TNodePrinter(System.out) ;
    //visitor.process(tdoc.getRoot()) ;
    ExtractContent extractContent = extractor.extract(null, tdoc) ;
    if(extractContent != null) {
  		extractContent.dump(System.out) ;
  	}
  }
}