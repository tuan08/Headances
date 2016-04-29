package org.headvances.ml.classify.xhtml.feature;

import org.headvances.data.Document;
import org.headvances.html.dom.TDocument;
import org.headvances.json.JSONMultiFileReader;
import org.headvances.ml.feature.FeatureDictionary;
import org.headvances.ml.feature.FeatureGenerator;
import org.headvances.ml.feature.FeatureHolder;
import org.headvances.ml.feature.GenerateFeature;
import org.headvances.ml.feature.Labels;
import org.headvances.ml.feature.MapFeatureHolder;

/**
 * $Author: Tuan Nguyen$ 
 **/
abstract public class FeatureCollector {
	private FeatureGenerator<TDocument> generator ;
	private Labels  labels = new Labels() ;
	protected int docIdTracker ;

	public FeatureCollector(FeatureGenerator<TDocument> generator) {
		this.generator = generator ;
	}
  
	final public void collect(Document doc) {
  	TDocument tdoc = TDocument.create(doc) ;
  	String labelName = getLabel(doc);
  	collect(tdoc, labelName) ;
	}
	
	final public void collect(TDocument tdoc, String labelName) {
		labels.collect(labelName) ;
		FeatureHolder holder = new MapFeatureHolder(docIdTracker);
		generator.generate(holder, tdoc) ;
		collect(holder.getFeatures(), labelName);
		docIdTracker++;
	}

	abstract protected void collect(GenerateFeature[] gfeatures, String labelName) ;
	
	public Labels getLabels() { return this.labels ; }
	
	abstract public FeatureDictionary getFeatureDictionary() ;
	
	public void collect(String dataDir) throws Exception {
		JSONMultiFileReader reader = new JSONMultiFileReader(dataDir);
    Document doc;
    int count = 0;
    System.out.print("Collect Feature") ;
    while ((doc = reader.next(Document.class)) != null) {
      if(doc.hasLabel("content:error")) continue ;
      collect(doc) ;
    	count++ ;
    	if(count % 1000 == 0) {
    		System.out.print('.') ;
    	}
    }
    System.out.println() ;
    reader.close() ;
	}
	
	static public String getLabel(Document doc) {
		String labelName = doc.getLabels()[0];
		labelName = labelName.replace(":detail", "");
		labelName = labelName.replace(":list", "");
		return labelName ;
	}
}
