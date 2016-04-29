package org.headvances.ml.nlp.opinion;

import java.util.ArrayList;
import java.util.List;

import org.headvances.data.Document;
import org.headvances.data.Entity;
/**
 * $Author: Tuan Nguyen$ 
 **/
public class DocumentOpinionExtractor  {
	private OpinionExtractor opinionExtractor ;
	
	public DocumentOpinionExtractor(OpinionExtractor opinionExtractor) {
		this.opinionExtractor = opinionExtractor ;
	}

	public List<Opinion> process(Document doc) throws Exception {
		String content = getContent(doc) ;
		String contentType = getContentType(doc) ;
		String[] text = content.split("\n") ;
		
		List<Opinion> extractedOpinions = new ArrayList<Opinion>() ;
		for(int i = 0; i < text.length; i++) {
			extractOpinion(extractedOpinions, new OpinionDocument(doc.getId(), text[i])) ;
		}

		for(int i = 0; i < extractedOpinions.size(); i++) {
			Opinion opinion = extractedOpinions.get(i) ;
			opinion.addCategory(contentType) ;
			opinion.computeId(doc.getId()) ;
		}
		return extractedOpinions;
	}
	
	private void  extractOpinion(List<Opinion> holder, OpinionDocument doc) throws Exception {
		List<Opinion> opinions = opinionExtractor.extract(doc) ;
		for(int i = 0; i < opinions.size(); i++) {
			holder.add(opinions.get(i)) ;
		}
	}

	static public String getContentType(Document doc) {
		if(doc.hasTag("content:article")) return "article" ;
		if(doc.hasTag("content:blog")) return "blog" ;
		if(doc.hasTag("content:forum")) return "forum" ;
		if(doc.hasTag("content:product")) return "product" ;
		if(doc.hasTag("content:classified")) return "classified" ;
		if(doc.hasTag("content:job")) return "job" ;
		return "other" ;
	}

	static public String getContent(Document doc) {
		Entity entity = doc.getEntity("mainContent") ;
		StringBuilder b = new StringBuilder() ;
		String title = entity.getString("title") ;
		if(title != null) b.append(title).append(".\n") ;
		String description = entity.getString("description") ;
		if(description != null) b.append(description).append(".\n") ;
		String content = entity.getString("content") ;
		if(content != null) b.append(content).append(".\n") ;
		return b.toString() ;
	}
}