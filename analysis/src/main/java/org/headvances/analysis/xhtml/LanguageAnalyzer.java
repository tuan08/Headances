package org.headvances.analysis.xhtml;

import java.io.IOException;
import java.util.List;

import javax.annotation.PostConstruct;

import org.headvances.analysis.Analyzer;
import org.headvances.data.Document;
import org.headvances.data.Entity;
import org.headvances.html.dom.TDocument;
import org.headvances.html.dom.TNode;

import com.google.common.base.Optional;
import com.optimaize.langdetect.LanguageDetector;
import com.optimaize.langdetect.LanguageDetectorBuilder;
import com.optimaize.langdetect.i18n.LdLocale;
import com.optimaize.langdetect.ngram.NgramExtractors;
import com.optimaize.langdetect.profiles.LanguageProfile;
import com.optimaize.langdetect.profiles.LanguageProfileReader;
/**
 * $Author: Tuan Nguyen$ 
 **/
public class LanguageAnalyzer implements Analyzer {
  static LanguageDetector languageDetector ;
  static {
    
    try {
      List<LanguageProfile> languageProfiles = new LanguageProfileReader().readAllBuiltIn();
      languageDetector =  LanguageDetectorBuilder.create(NgramExtractors.standard()).withProfiles(languageProfiles).build();
    } catch (IOException e) {
      e.printStackTrace();
    }
    //build language detector:
  }
  
  @PostConstruct
  public void onInit() throws Exception {
  }

  public void analyze(Document hDoc, TDocument tdoc) {
    String text = findText(hDoc, tdoc.getRoot()) ;
    languageDetector.detect(text);
    if(text == null) text = "abc" ;
    String lang = "en";
    Optional<LdLocale> langOptional = languageDetector.detect(text);
    if(langOptional.isPresent()) lang = langOptional.get().getLanguage();
    if("vi".equals(lang)) {
      hDoc.addTag("lang:vi") ;
    } else if("en".equals(lang)) {
      hDoc.addTag("lang:en") ;
    } else {
      hDoc.addTag("lang:other") ;
    }
  }

  private String findText(Document doc, TNode node) {
    StringBuilder b = new StringBuilder() ;
    buildEntity(b, doc.getEntity("icontent")) ;
    buildEntity(b, doc.getEntity("comment")) ;
    if(b.length() < 10) {
      findText(b, node) ;
    }
    return b.toString() ;
  }

  private void findText(StringBuilder b, TNode node) {
    String candidate = node.getNodeValue() ;
    if(candidate != null && candidate.length() > 40) {
      b.append(candidate).append("\n") ;
    }
    List<TNode> children = node.getChildren() ;
    if(children == null) return ;
    for(int i = 0; i < children.size(); i++) {
      TNode child = children.get(i) ;
      findText(b, child) ;
    }
  }

  private void buildEntity(StringBuilder b, Entity entity) {
    if(entity == null) return ;
    b.append(entity.get("title")).append("\n\n") ;
    b.append(entity.get("description")).append("\n\n") ;
    b.append(entity.get("content\n\n")) ;
  }
}