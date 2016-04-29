package org.headvances.analysis.xhtml;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.headvances.data.Document;
import org.headvances.data.Entity;
import org.headvances.html.dom.TDocument;
import org.headvances.html.dom.extract.DocumentExtractor;
import org.headvances.html.dom.extract.ExtractBlock;
import org.headvances.html.dom.extract.ExtractContent;

public class XHTMLContentExtractor {
	private Map<String, DocumentExtractor.Type> hints = new HashMap<String, DocumentExtractor.Type>() ;
	private DocumentExtractor extractor = new DocumentExtractor() ;
	
	public XHTMLContentExtractor() {
		hints.put("content:article",    DocumentExtractor.Type.article) ;
		hints.put("content:blog",       DocumentExtractor.Type.blog) ;
		hints.put("content:forum",      DocumentExtractor.Type.forum) ;
		hints.put("content:product",    DocumentExtractor.Type.product) ;
		hints.put("content:classified", DocumentExtractor.Type.classified) ;
		hints.put("content:job",        DocumentExtractor.Type.job) ;
		hints.put("content:other",      DocumentExtractor.Type.other) ;
		hints.put("content:ignore",     DocumentExtractor.Type.ignore) ;
	}
	
	public ExtractContent extract(Document hDoc, TDocument tdoc, String hint) {
		DocumentExtractor.Type hintType = hints.get(hint) ;
		ExtractContent extractContent = extractor.extract(hintType, tdoc) ;
		List<ExtractBlock> extractBlocks = extractContent.getExtractBlocks() ;
		for(int i = 0; i < extractBlocks.size(); i++) {
			ExtractBlock block = extractBlocks.get(i) ;
			String blockName = block.getBlockName() ;

			Entity entity = new Entity() ;
			entity.add("title", block.getTitle()) ;
			entity.add("description", block.getDescription()) ;
			entity.add("content", block.getContent()) ;
			hDoc.addEntity(blockName, entity) ;
			hDoc.addTag(block.getTags()) ;
		}
		return extractContent ;
	}
}