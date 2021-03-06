package org.headvances.html.dom.tagger;

import org.headvances.data.Document;
import org.headvances.html.dom.TDocument;
import org.headvances.html.dom.TNode;
import org.headvances.html.dom.processor.CleanEmptyNodeProcessor;
import org.headvances.html.dom.selector.TagSelector;
import org.headvances.html.fetcher.Fetcher;
import org.headvances.html.fetcher.HttpClientFetcher;
import org.headvances.util.text.CosineSimilarity;
import org.junit.Test;

public class TitleBlockTaggerUnitTest {
  static String HTML = 
    "<!DOCTYPE html PUBLIC '-//W3C//DTD XHTML 1.0 Transitional//EN'" +
    "  'http://www.w3.org/TR/xhtml1/DTD/xhtml1-transitional.dtd'>" +
    "<html xmlns='http://www.w3.org/1999/xhtml'>\n" +
    "  <head><title>Hà nội mùa này</title></head>" +
    "  <body>\n"   +
    "    <div id='BlockTextList'>\n"  +
    "      <div>\n"  +
    "         <a href=''>“Hà nội mùa này Cục Điện ảnh” </a>" +
    "         <p>Hà nội mùa này</p>" +
    "      </div>\n" +
    "      <div>\n"  +
    "         <a href=''>“Bộ sẽ cân nhắc kỹ vị trí Cục trưởng Cục Điện ảnh” </a>" +
    "         <p>Hà nội mùa này</p>" +
    "      </div>\n" +
    "      <div>\n"  +
    "         <a href=''>“Bộ sẽ cân nhắc kỹ vị trí Cục trưởng Cục Điện ảnh” </a>" +
    "         <p>Hà nội mùa này</p>" +
    "      </div>\n" +
    "      <div>\n"  +
    "         <a href=''>“Bộ sẽ cân nhắc kỹ vị trí Cục trưởng Cục Điện ảnh” </a>" +
    "      </div>\n" +
    "      <div>\n"  +
    "         <a href=''>“Bộ sẽ cân nhắc kỹ vị trí Cục trưởng Cục Điện ảnh” </a>" +
    "      </div>\n" +
    "    </div>\n" +
    "  </body>\n"  +
    "</html>" ;

  @Test
  public void testTitleBlockTagger() throws Exception {
    TDocument tdoc = new TDocument("Anchor Text", "http://vnexpress.net", HTML) ;
    TNode root = tdoc.getRoot() ;
    new CleanEmptyNodeProcessor().process(root) ;
    new TitleBlockTagger().tag(tdoc, tdoc.getRoot()) ;
    //TNodePrinter visitor = new TNodePrinter(System.out) ;
    //visitor.process(tdoc.getRoot()) ;
    assertTitleBlockTagger(root) ;
    CosineSimilarity a = new CosineSimilarity();
    System.out.println(a.similarity("Hà nội mùa này", "Hà nội mùa này")) ;
  }
  
  private void assertTitleBlockTagger(TNode root) {
  }
  
  //@Test
  public void testSite() throws Exception  {
    String url = "http://dantri.com.vn/";    
    Fetcher fetcher = new HttpClientFetcher();
    Document hdoc = fetcher.fetch(url);
    TDocument tdoc = new TDocument("", url, hdoc.getContent()) ;
    TNode root = tdoc.getRoot() ;
    new CleanEmptyNodeProcessor().process(root) ;
    new ContentListBlockTagger().tag(tdoc, root) ;
    
    TNode[] nodes = root.select(new TagSelector(ContentListBlockTagger.BLOCK_LIST)) ;
    //TNodePrinter visitor = new TNodePrinter(System.out) ;
    for(TNode sel : nodes) {
      //visitor.process(sel) ;
      System.out.println(sel.getTextContent());
    }
  }
}
