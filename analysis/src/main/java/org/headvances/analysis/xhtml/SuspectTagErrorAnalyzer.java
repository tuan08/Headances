package org.headvances.analysis.xhtml;

import org.headvances.analysis.Analyzer;
import org.headvances.data.Document;
import org.headvances.data.Entity;
import org.headvances.data.HtmlDocumentUtil;
import org.headvances.html.dom.TDocument;
import org.headvances.util.text.StringUtil;
/**
 * $Author: Tuan Nguyen$ 
 **/
public class SuspectTagErrorAnalyzer implements Analyzer {
  static String MAIN_CONTENT_ENTITY = HtmlDocumentUtil.MCONTENT ;

  private TagAnalyzer[] tagAnalyzer = {
      new ListTagAnalyzer() , new DetailTagAnalyzer(),
      new ArticleTagAnalyzer(), new ProductTagAnalyzer() 
  } ;

  public void analyze(Document hDoc, TDocument tdoc) {
    for(TagAnalyzer sel : tagAnalyzer) {
      sel.analyze(hDoc, tdoc) ;
    }
  }

  static abstract class TagAnalyzer {
    abstract public void analyze(Document hDoc, TDocument tdoc) ;

    protected int sumLength(String ... text) {
      int sum = 0;
      for(String sel : text) {
        if(sel != null) sum += sel.length() ;
      }
      return sum ;
    }
  }

  static class ListTagAnalyzer extends TagAnalyzer {
    public void analyze(Document hDoc, TDocument tdoc) {
      if(!hDoc.hasTag("content:list")) return ;
      Entity icontent = hDoc.getEntity(MAIN_CONTENT_ENTITY) ;
      if(icontent == null) {
        hDoc.addTag("content:list:suspect") ;
        return ;
      }
      String title = icontent.get("title") ;
      if(!StringUtil.isEmpty(title) && title.length() > 20) {
        hDoc.addTag("content:list:suspect") ;
      }
    }
  }

  static class DetailTagAnalyzer extends TagAnalyzer {
    public void analyze(Document hDoc, TDocument tdoc) {
      if(!hDoc.hasTag("content:detail")) return ;
      Entity icontent = hDoc.getEntity(MAIN_CONTENT_ENTITY) ;
      if(icontent == null) {
        hDoc.addTag("content:detail:suspect") ;
        return ;
      }
      String title = icontent.get("title") ;
      if(StringUtil.isEmpty(title) || title.length() < 15) {
        hDoc.addTag("content:detail:suspect") ;
        return ;
      }
      if(sumLength(title, icontent.get("description"), icontent.get("content")) < 75) {
        hDoc.addTag("content:detail:suspect") ;
        return ;
      }
    }
  }

  static class ArticleTagAnalyzer extends TagAnalyzer {
    public void analyze(Document hDoc, TDocument tdoc) {
      if(!hDoc.hasTag("content:article")) return ;
      Entity icontent = hDoc.getEntity(MAIN_CONTENT_ENTITY) ;
      if(icontent == null) {
        hDoc.addTag("content:article:suspect") ;
        return ;
      }

      String title = icontent.get("title") ;
      if(StringUtil.isEmpty(title) || title.length() < 15) {
        hDoc.addTag("content:article:suspect") ;
        return ;
      }

      if(hDoc.hasTag("content:article:detail")) {
        if(sumLength(title, icontent.get("description"), icontent.get("content")) < 150) {
          hDoc.addTag("content:article:suspect") ;
          return ;
        }
      }
    }
  }

  static class ProductTagAnalyzer extends TagAnalyzer {
    public void analyze(Document hDoc, TDocument tdoc) {
      if(!hDoc.hasTag("content:product")) return ;
      Entity icontent = hDoc.getEntity(MAIN_CONTENT_ENTITY) ;
      if(icontent == null) {
        hDoc.addTag("content:product:suspect") ;
        return ;
      }

      String title = icontent.get("title") ;
      if(StringUtil.isEmpty(title)) {
        hDoc.addTag("content:product:suspect") ;
        return ;
      }

      if(hDoc.hasTag("content:product:detail")) {
        if(sumLength(icontent.get("description")) == 0) {
          hDoc.addTag("content:product:suspect") ;
          return ;
        }
      }
    }
  }
}