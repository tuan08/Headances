package org.headvances.analysis.xhtml;

import java.util.List;

import javax.annotation.PostConstruct;

import org.headvances.analysis.Analyzer;
import org.headvances.analysis.statistic.ContentTypeStatistic;
import org.headvances.data.Document;
import org.headvances.data.Entity;
import org.headvances.html.dom.TDocument;
import org.headvances.html.dom.extract.ExtractBlock;
import org.headvances.html.dom.extract.ExtractContent;
import org.headvances.nlp.ml.classify.Predict;
import org.headvances.nlp.ml.classify.xhtml.MaxEntClassifier;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jmx.export.annotation.ManagedOperation;
import org.springframework.jmx.export.annotation.ManagedResource;
/**
 * $Author: Tuan Nguyen$ 
 **/
@ManagedResource(
	objectName="org.headvances.analysis.xhtml:name=XHTMLTextAnalyzer", 
	description="This component is responsible for classifying and extracting the web content"
)
public class XHTMLTextAnalyzer implements Analyzer {
	@Autowired
	private ContentTypeStatistic typeStatistic ;
	
	@Autowired
	private MaxEntClassifier classifier ;
	
	@Autowired
	private XHTMLContentExtractor defaultExtractor  ;
	
	@Autowired
	private XHTMLContentXPathExtractor xpathExtractor  ;
	
	public XHTMLTextAnalyzer() { }
	
	@PostConstruct
	public void onInit() throws Exception { }

	@ManagedOperation(description="Analysis Service invoke monitor")
  public String formatContentTypeStatistic() { 
		return typeStatistic.toString() ;  
	}
	
	public void analyze(Document hDoc, TDocument tdoc) {
		String[] errorTag = hDoc.getTagWithPrefix("error:") ;
		if(errorTag != null && errorTag.length > 0) return ;
		
		try {
			Predict predict = classifier.predict(tdoc) ;
			hDoc.addTag(predict.label) ;
			setPredictTag(predict, hDoc);
			typeStatistic.log(hDoc, predict);
			extract(hDoc, tdoc, predict) ;
		} catch(Exception ex) {
			ex.printStackTrace() ;
		}
	}
	
	private void extract(Document hDoc, TDocument tdoc, Predict predict) {
		ExtractContent extractContent = xpathExtractor.extract(hDoc, tdoc, predict.label) ;
		if(extractContent == null) {
			extractContent = defaultExtractor.extract(hDoc, tdoc, predict.label) ;
		}
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
	}
	
	private void setPredictTag(Predict predict, Document hdoc){
	  if(classifier.isTrusted(predict)) { hdoc.addTag("content:predict:trust") ; }
	  else { hdoc.addTag("content:predict:untrust");}

	  if(predict.probability < 0.2)   hdoc.addTag("content:predict:0.0-0.2") ;
	  else if(predict.probability < 0.4)   hdoc.addTag("content:predict:0.2-0.4") ;
	  else if(predict.probability < 0.6)   hdoc.addTag("content:predict:0.4-0.6") ;
	  else if(predict.probability < 0.8)   hdoc.addTag("content:predict:0.6-0.8") ;
	  else   hdoc.addTag("content:predict:0.8-1.0") ;  
	}
}