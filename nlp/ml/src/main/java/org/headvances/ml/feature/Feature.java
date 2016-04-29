package org.headvances.ml.feature;

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
	    if(f1.docFrequency < f2.docFrequency) return  1 ;
	    if(f1.docFrequency > f2.docFrequency) return -1 ;
    	return 0;
    }
	} ;

	static public Comparator<Feature> TERM_FRQ_COMPARATOR = new Comparator<Feature>() {
    public int compare(Feature f1, Feature f2) {
	    if(f1.termFrequency < f2.termFrequency) return  1 ;
	    if(f1.termFrequency > f2.termFrequency) return -1 ;
    	return 0;
    }
	} ;

	static public enum Type { TOKEN, NGRAM } ;
	
	private int     id        ;
	private String  feature   ;
	private float   weight    ;
	private int     termFrequency = 1;
	private int     docFrequency  = 1;
	private Type    type = Type.TOKEN ;
	
	public Feature() {} 
	
	public Feature(String feature) {
		this.feature = feature ;
	}
	
  public int  getId() { return id; }
  public void setId(int id) { this.id = id; }

  public String getFeature() { return feature; }
	public void   setFeature(String feature) { this.feature = feature; }

  public float getWeight() { return weight; }
	public void setWeight(float weight) { this.weight = weight; }
	
	public int  getTermFrequency() { return termFrequency ; }
	public void setTermFrequency(int frq) { termFrequency = frq ; }
	
	public int  getDocFrequency() { return docFrequency ; }
	public void setDocFrequency(int frq) { docFrequency = frq ; }
	
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