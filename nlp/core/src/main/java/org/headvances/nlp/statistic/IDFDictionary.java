package org.headvances.nlp.statistic;

import java.io.InputStream;
import java.util.LinkedHashMap;
import java.util.Map;

import org.headvances.json.JSONReader;
import org.headvances.json.JSONWriter;
import org.headvances.util.text.TabularPrinter;
/**
 * $Author: Tuan Nguyen$ 
 **/
public class IDFDictionary {
	transient private int idTracker = 0 ;
	
	private int documentCount ;
	private Map<String, IDFFeature> idfFeatures = new LinkedHashMap<String, IDFFeature>() ;
	
	public IDFDictionary() {
	}
	
	public int getDocumentCount() { return documentCount; }
	public void setDocumentCount(int documentCount) { this.documentCount = documentCount; }
	
	public IDFFeature[] getFeatures() {
		IDFFeature[] feature = new IDFFeature[idfFeatures.size()];
		idfFeatures.values().toArray(feature);
		return feature;
	}

	public void setFeatures(IDFFeature[] feature) {
		for (IDFFeature sel : feature) {
			idfFeatures.put(sel.getFeature(), sel);
		}
	}
	
	public IDFFeature getFeature(String feature) { return idfFeatures.get(feature) ; }
	
	public void add(String feature, int docFreq) {
		IDFFeature idfFeature = new IDFFeature() ;
		idfFeature.setId(idTracker++) ;
		idfFeature.setFeature(feature) ;
		idfFeature.setDocFrequency(docFreq) ;
		idfFeature.setIdf((float) Math.log10((double)documentCount/docFreq)) ;
		idfFeatures.put(feature, idfFeature);
	}
	
	public void printTable(Appendable appendable) {
		String[] header = {"Feature", "Doc Freq", "IDF"} ;
		int[]    width  = {40,        10,         10} ;
		TabularPrinter printer = new TabularPrinter(appendable, width) ;
		printer.printHeader(header) ;
		IDFFeature[] features = getFeatures() ;
		String docCount = "/" + documentCount ;
		for(int i = 0; i < features.length; i++) {
			Object[] cells = {
				features[i].getFeature(), 
				features[i].getDocFrequency() + docCount , 
				features[i].getIdf() 	
			};
			printer.printRow(cells, false) ;
		}
	}
	
	public void saveAsJSON(String file) throws Exception {
		JSONWriter writer = new JSONWriter(file) ;
		writer.write(this) ;
		writer.close() ;
	}
	
	static public IDFDictionary load(InputStream is) throws Exception {
  	JSONReader reader = new JSONReader(is) ;
  	IDFDictionary dict = reader.read(IDFDictionary.class) ;
  	reader.close() ;
  	return dict ;
  }
	
	static public class IDFFeature {
		private int    id ;
		private String feature ;
		private int    docFrequency ;
		private float  idf ;
		
		public int getId() { return id; }
		public void setId(int id) { this.id = id; }
		
		public String getFeature() { return feature; }
		public void setFeature(String feature) { this.feature = feature; }
		
		public int getDocFrequency() { return docFrequency; }
		public void setDocFrequency(int docFrequency) { this.docFrequency = docFrequency; }
		
		public float getIdf() { return idf; }
		public void setIdf(float idf) { this.idf = idf; }
	}
}
