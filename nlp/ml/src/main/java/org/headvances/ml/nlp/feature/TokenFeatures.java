package org.headvances.ml.nlp.feature;

import java.util.ArrayList;
import java.util.List;

import cc.mallet.types.Alphabet;
import cc.mallet.types.FeatureVector;
/**
 * $Author: Tuan Nguyen$ 
 **/
public class TokenFeatures {
	String[] feature ;
	String   targetFeature ;
	
	public TokenFeatures(String feature, String targetFeature) {
		this.feature = new String[] { feature } ;
		this.targetFeature = targetFeature ;
	}
	
	public TokenFeatures(String[] feature, String targetFeature) {
		this.feature = feature  ;
		this.targetFeature = targetFeature ;
	}
	
	public String[] getFeatures() { return this.feature ; }
	
	public String getTargetFeature() { return this.targetFeature ; }
	
	public FeatureVector getFeatureVector(Alphabet alphabet, boolean add) {
		if(add) {
			int[] featureIndices = new int[this.feature.length] ;
			for(int j = 0; j < featureIndices.length; j++) {
				featureIndices[j] = alphabet.lookupIndex(this.feature[j]);
			}
			return new FeatureVector(alphabet, featureIndices);
		} else {
			List<Integer> holder = new ArrayList<Integer>() ;
			for(int j = 0; j < this.feature.length; j++) {
				int featureIndex = alphabet.lookupIndex(this.feature[j], false);
				if(featureIndex >= 0) holder.add(featureIndex) ;
			}
			int[] featureIndices = new int[holder.size()] ;
			for(int i = 0; i < featureIndices.length; i++) {
				featureIndices[i] = holder.get(i) ;
			}
			return new FeatureVector(alphabet, featureIndices);
		}
	}

	public String  toIOB2Format() {
		StringBuilder b = new StringBuilder() ;
		for(int j = 0; j < feature.length; j++) {
			if(j > 0) b.append(' ') ;
			b.append(feature[j]) ;
		}
		if(targetFeature != null) b.append(' ').append(this.targetFeature) ;
		return b.toString() ;
	}
	
	static public TokenFeatures[] tokenize(String text) {
		String[] segs = text.split("[ \n]");
		TokenFeatures[] token = new TokenFeatures[segs.length] ;
		for (int i = 0; i < segs.length; i++) {
			int idx = segs[i].lastIndexOf('[') ;
			String feature = null, tfeature = null;
			if(idx > 0) {
			  feature = segs[i].substring(0, idx) ;
			  tfeature = segs[i].substring(idx + 1, segs[i].length() - 1) ;
			} else {
				feature = segs[i] ;
			}
			token[i] = new TokenFeatures(feature, tfeature);
		}
		return token;
	}
}
