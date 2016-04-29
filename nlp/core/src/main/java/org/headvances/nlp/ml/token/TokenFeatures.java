package org.headvances.nlp.ml.token;

/**
 * $Author: Tuan Nguyen$ 
 **/
public class TokenFeatures {
	private  String token ;
	private  String[] feature ;
	private  String   targetFeature ;
	
	public TokenFeatures(String token, String[] feature, String targetFeature) {
		this.token = token ;
		this.feature = feature  ;
		this.targetFeature = targetFeature ;
	}
	
	public String   getToken() { return this.token ; }
	
	public String[] getFeatures() { return this.feature ; }
	
	public String getTargetFeature() { return this.targetFeature ; }
	
	public String toString() {
		StringBuilder b = new StringBuilder() ;
		for(int i = 0; i < feature.length; i++) {
			if(i > 0) b.append(' ') ;
			b.append(feature[i]) ;
		}
		if(targetFeature != null) {
			b.append(' ').append(targetFeature) ;
		}
		return b.toString() ;
	}
	
	static public void dump(TokenFeatures[] tfeatures) {
		for(TokenFeatures sel : tfeatures) {
			System.out.println(sel);
		}
	}
}
