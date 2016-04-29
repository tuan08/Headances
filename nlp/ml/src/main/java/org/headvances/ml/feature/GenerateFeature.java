package org.headvances.ml.feature;

import org.headvances.ml.feature.Feature.Type;

/**
 * $Author: Tuan Nguyen$ 
 **/
public class GenerateFeature {
	private int     docId     ;
	private String  category  ;
	private String  token     ;
	private int     frequency ;
	private Feature.Type type = Feature.Type.TOKEN ; 
	
	public GenerateFeature() {} 
	
	public GenerateFeature(int docId, String category, String token) {
		this.docId = docId ;
		this.category = category ;
		this.token = token ;
		this.frequency = 1 ;
	} 

	public int  getDocId() { return docId; }
	public void setDocId(int docId) { this.docId = docId ; }

	public String getCategory() { return this.category ; }
	public void   setCategory(String cat) { this.category = cat ; }
	
	public String getToken() { return token; }
	public void   setToken(String token) { this.token = token; }

	public String getFeature() { return generateFeature(category, token) ; }
	
	public int  getFrequency() { return frequency; }
	public void setFrequency(int frequency) { this.frequency = frequency; }

	public void addFrequency(int amount) {
		this.frequency += amount ;
	}
	
	public Type getType() { return this.type ; }
	public void setType(Type type) { this.type =  type ; } 
	
	static public String generateFeature(String category, String token) {
		if(category == null) return token;
		return category + ":" + token ;
	}
}