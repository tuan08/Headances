package org.headvances.html.dom.tagger;

import org.headvances.data.Document;
import org.headvances.html.dom.TDocument;
import org.headvances.html.dom.TNode;
import org.headvances.html.dom.processor.CleanEmptyNodeProcessor;
import org.headvances.html.fetcher.Fetcher;
import org.headvances.html.fetcher.HttpClientFetcher;
import org.junit.Test;
/**
 * $Author: Tuan Nguyen$ 
 **/
public class RepeatBlockTaggerUnitTest {
  @Test
  public void testSite() throws Exception  {
  	String url      = "http://mediamart.vn/ProductDetail.aspx?ProductId=11623";    
  	Fetcher fetcher = new HttpClientFetcher();
    Document  hdoc  = fetcher.fetch(url);
    TDocument tdoc  = new TDocument("", url, hdoc.getContent()) ;
    TNode root = tdoc.getRoot() ;
    new CleanEmptyNodeProcessor().process(root) ;
    TNode[] nodes = new RepeatBlockTagger().tag(tdoc, root) ;
    for(TNode sel : nodes) {
    	//visitor.process(sel) ;
    	//System.out.println("-------------------------------------------------------");
      System.out.println(sel.getXPath() + ", " + sel.getTextSize());
      System.out.println(sel);
    }
  }
}