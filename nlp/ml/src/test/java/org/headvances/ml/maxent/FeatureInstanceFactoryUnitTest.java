package org.headvances.ml.maxent;

import org.headvances.ml.feature.Feature;
import org.junit.Test;

import cc.mallet.types.Alphabet;
import cc.mallet.types.SparseVector;

/**
 * $Author: Tuan Nguyen$ 
 **/
public class FeatureInstanceFactoryUnitTest {
	@Test
	public void testWeight() {
		FeatureInstanceFactory factory = new FeatureInstanceFactory() ;
		Feature[] feature = {
			createFeature("a"), createFeature("b", 2), createFeature("a")
		} ;
		SparseVector fv = factory.createFeatureVector(feature, true) ;
		Alphabet dataAlphabet = factory.getDataAlphabet() ;
		System.out.println("a value: " + fv.value(dataAlphabet.lookupIndex("a", false)));
		System.out.println("b value: " + fv.value(dataAlphabet.lookupIndex("b", false)));
	}
	
	Feature createFeature(String feature) {
		Feature f = new Feature(feature) ;
		f.setWeight(1) ;
		return f ;
	}
	
	Feature createFeature(String feature, float weight) {
		Feature f = new Feature(feature) ;
		f.setWeight(weight) ;
		return f ;
	}
}
