package org.headvances.nlp.ml.classify;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.regex.Pattern;
/**
 * $Author: Tuan Nguyen$ 
 **/
public class Feature {
	static public Comparator<Feature> WEIGHT_COMPARATOR = new Comparator<Feature>() {
    public int compare(Feature f1, Feature f2) {
	    if(f1.weight < f2.weight) return  1 ;
	    if(f1.weight > f2.weight) return -1 ;
    	return 0;
    }
	} ;
	
	static public Comparator<Feature> DOC_FRQ_COMPARATOR = new Comparator<Feature>() {
    public int compare(Feature f1, Feature f2) {
	    if(f1.docFreq < f2.docFreq) return  1 ;
	    if(f1.docFreq > f2.docFreq) return -1 ;
    	return 0;
    }
	} ;

	static public Comparator<Feature> TERM_FRQ_COMPARATOR = new Comparator<Feature>() {
    public int compare(Feature f1, Feature f2) {
	    if(f1.featureFreq < f2.featureFreq) return  1 ;
	    if(f1.featureFreq > f2.featureFreq) return -1 ;
    	return 0;
    }
	} ;

	static public enum Type { TOKEN, NGRAM } ;
	
	private String  feature   ;
	private float   weight    = 1f;
	private int     featureFreq = 1;
	private int     docFreq  = 1;
	private Type    type = Type.TOKEN ;
	
	public Feature() {} 
	
	public Feature(String feature) {
		this.feature = feature ;
	}
	
  public String getFeature() { return feature; }
	public void   setFeature(String feature) { this.feature = feature; }

	public float getWeight() { return weight; }
	public void  setWeight(float weight) { this.weight = weight ; }
	public void  setIDFWeight(int numberOfDoc) { 
		weight = (float) Math.log10((double)numberOfDoc/this.docFreq) ;
	}
	
	public void addFeatureFreq(int count) { this.featureFreq += count ; }
	public int  getFeatureFreq() { return featureFreq ; }
	public void setFeatureFreq(int frq) { featureFreq = frq ; }
	
	public void addDocFreq(int count) { this.docFreq += count ; }
	public int  getDocFreq() { return docFreq ; }
	public void setDocFreq(int frq) { docFreq = frq ; }
	
	public Type getType() { return this.type ; }
	public void setType(Type type) { this.type =  type ; } 
	
	static public Feature[] filterByRegrex(Feature[] features, String exp) {
		Pattern pattern = Pattern.compile(exp) ;
		List<Feature> holder = new ArrayList<Feature>() ;
		for(Feature feature : features) {
			if(pattern.matcher(feature.getFeature()).matches()) {
				holder.add(feature) ;
			}
		}
		return holder.toArray(new Feature[holder.size()]) ;
	}
}