package org.headvances.analysis.xhtml;

import java.util.List;

import javax.annotation.PostConstruct;

import org.apache.hadoop.conf.Configuration;
import org.apache.nutch.analysis.lang.LanguageIdentifier;
import org.headvances.analysis.Analyzer;
import org.headvances.data.Document;
import org.headvances.data.Entity;
import org.headvances.html.dom.TNode;
import org.headvances.html.dom.TDocument;
/**
 * $Author: Tuan Nguyen$ 
 **/
public class LanguageAnalyzer implements Analyzer {
	private static final ThreadLocal <LanguageIdentifier> identifiers = 
		new ThreadLocal <LanguageIdentifier> () {
		  protected LanguageIdentifier initialValue() {
			  return new LanguageIdentifier(new Configuration());
		  }
	};
	
	@PostConstruct
	public void onInit() throws Exception {
	}

	public void analyze(Document hDoc, TDocument tdoc) {
		LanguageIdentifier identifier = identifiers.get() ;
		String text = findText(hDoc, tdoc.getRoot()) ;
		if(text == null) text = "abc" ;
		String lang = identifier.identify(text) ;
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