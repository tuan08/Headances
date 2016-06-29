package org.headvances.html.dom;

import org.headvances.data.Document;
import org.headvances.data.HtmlDocumentUtil;
import org.headvances.html.JSoupParser;
/**
 * $Author: Tuan Nguyen$ 
 **/
public class TDocument {
  private String   xhtml ;
  private String   anchorText ;
  private String   url   ;
  private TNode    root ;

  public TDocument(String anchorText, String url, String xhtml) {
    this.anchorText = anchorText ;
    this.url = url ;
    this.xhtml = xhtml ;
    root = JSoupParser.INSTANCE.toTNode(xhtml) ;
  }

  public String getAnchorText() { return anchorText; }
  public void setAnchorText(String anchorText) { this.anchorText = anchorText; }

  public String getUrl() { return url; }
  public void   setUrl(String url) { this.url = url; }

  public String getXHTML() { return this.xhtml ; }

  public TNode getRoot() { return root; }

  public void setRoot(TNode root) { this.root = root; }

  static public TDocument create(Document doc) {
    String url = HtmlDocumentUtil.getHtmlLink(doc).get("url");
    String anchorText = HtmlDocumentUtil.getHtmlLink(doc).get("anchorText");
    TDocument tdoc = new TDocument(anchorText, url, doc.getContent()) ;
    return tdoc ;
  }
}
