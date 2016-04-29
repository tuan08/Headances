package org.headvances.ml.feature;

import java.io.FileOutputStream;
import java.io.InputStream;
import java.io.PrintStream;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Comparator;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import org.headvances.json.JSONReader;
import org.headvances.json.JSONWriter;
import org.headvances.util.text.StringUtil;
import org.headvances.util.text.TabularPrinter;
/**
 * $Author: Tuan Nguyen$ 
 **/
public class FeatureDictionary {
	private Labels labels;
	
	private Map<String, Feature> features = new LinkedHashMap<String, Feature>() ; 
	
	public FeatureDictionary() {
	}

	public FeatureDictionary(Labels labels) {
		this.labels = labels;
	}

	public int size() { return features.size() ; }
	
	public Labels getLabels() { return this.labels; }
	public void     setLabels(Labels labels) { this.labels = labels; }

	public Feature[] getFeatures() {
		Feature[] feature = new Feature[features.size()];
		features.values().toArray(feature);
		return feature;
	}

	public void setFeatures(Feature[] feature) {
		for (Feature sel : feature) {
			features.put(sel.getFeature(), sel);
		}
	}
	
	public void addFeature(Feature feature) {
		features.put(feature.getFeature(), feature);		
	}

	public void addFeature(Feature[] feature) {
		for(Feature sel : feature) {
			features.put(sel.getFeature(), sel);
		}
	}
	
	public void resetFeatureId() {
		Iterator<Feature> i = features.values().iterator() ;
		int idx = 0 ;
		while(i.hasNext()) {
			i.next().setId(idx++) ;
		}
	}
	
	public void resetFeatureParameter(FeatureDictionary withDict) {
		Iterator<Feature> i = features.values().iterator() ;
		while(i.hasNext()) {
			Feature feature = i.next() ;
			feature.setWeight(0f) ;
			Feature withFeature = withDict.getFeature(feature.getFeature()) ;
			if(withFeature != null) { 
			  feature.setTermFrequency(withFeature.getTermFrequency());
			  feature.setDocFrequency(withFeature.getDocFrequency());
			  feature.setWeight(withFeature.getWeight()) ;
			}
		}
	}
	
	public Feature[] getFeatures(Comparator<Feature> comparator) {
		Iterator<Feature> i = features.values().iterator() ;
		List<Feature> holder = new ArrayList<Feature>() ;
		while(i.hasNext()) {
			Feature feature = i.next() ;
			holder.add(feature) ;
		}
		Feature[] array = holder.toArray(new Feature[holder.size()]) ;
		Arrays.sort(array, comparator) ;
		return array ;
	}
	
	public void add(FeatureDictionary otherDict, boolean overwrite) {
		for(Feature feature : otherDict.getFeatures()) {
			if(overwrite || !features.containsKey(feature.getFeature())) {
				features.put(feature.getFeature(), feature) ;
			}
		}
	}

	public void add(Feature[] features , boolean overwrite) {
		for(Feature feature : features) {
			if(overwrite || !this.features.containsKey(feature.getFeature())) {
				this.features.put(feature.getFeature(), feature) ;
			}
		}
	}

	public Feature getFeature(String feature) { return features.get(feature) ; }

	public void dump(PrintStream out) {
		out.println("Labels: " + StringUtil.joinStringArray(labels.getLabels()));
		for(Feature f : features.values()) {
			out.println(f.getFeature() + " : " + f.getWeight()) ;
		}
	}
	
	public void save(String file) throws Exception {
		JSONWriter writer = new JSONWriter(file) ;
		writer.write(this) ;
		writer.close() ;
	}
	
	public void printFeatures(String file, Comparator<Feature> comparator) throws Exception {
		String[] header = {"Feature", "Doc Freq", "Term Freq", "Weight"} ;
		int[]    width  = {40,        10,         10,           10} ;
		FileOutputStream os = new FileOutputStream(file) ;
		TabularPrinter printer = new TabularPrinter(os, width) ;
		printer.printHeader(header) ;
		Feature[] features = getFeatures(comparator) ;
		for(int i = 0; i < features.length; i++) {
			Object[] cells = {
				features[i].getFeature(),features[i].getDocFrequency(), 
				features[i].getTermFrequency(), features[i].getWeight() 	
			};
			printer.printRow(cells, false) ;
		}
		os.close() ;
	}
	
	public void remove(FeatureFilter filter) {
		Iterator<Feature> i = features.values().iterator() ;
		while(i.hasNext()) {
			Feature feature = i.next() ;
			if(filter.accept(feature)) i.remove() ;
		}
	}
	
	static public interface FeatureFilter {
		public boolean accept(Feature feature) ;
	}

  static public FeatureDictionary load(InputStream is) throws Exception {
  	JSONReader reader = new JSONReader(is) ;
  	FeatureDictionary dict = reader.read(FeatureDictionary.class) ;
  	reader.close() ;
  	return dict ;
  }
}