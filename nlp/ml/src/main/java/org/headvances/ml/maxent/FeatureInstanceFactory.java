package org.headvances.ml.maxent;

import java.io.Serializable;

import org.headvances.ml.feature.Feature;

import cc.mallet.types.Alphabet;
import cc.mallet.types.FeatureVector;
import cc.mallet.types.Instance;
import cc.mallet.types.LabelAlphabet;
import cc.mallet.types.SparseVector;
/**
 * $Author: Tuan Nguyen$ 
 **/
public class FeatureInstanceFactory  implements Serializable {
	private LabelAlphabet targetLabelAlphabet ;
	private Alphabet dataAlphabet ;

	public FeatureInstanceFactory () {
		this.dataAlphabet = new Alphabet() ;
		this.targetLabelAlphabet = new LabelAlphabet() ;
	}

	public FeatureInstanceFactory(Alphabet dataAlphabet, LabelAlphabet targetLabelAlphabet) {
		this.dataAlphabet = dataAlphabet ;
		this.targetLabelAlphabet = targetLabelAlphabet ;
	}

	public Alphabet getDataAlphabet() { return this.dataAlphabet ; }

	public Alphabet getTargetAlphabet() { return this.targetLabelAlphabet ; }

	public Instance createTrainInstance(Feature[] feature, String label) {
		SparseVector fv = createFeatureVector(feature, true);
		Instance carrier = new Instance(null, null, null, null) ;
		carrier.setData(fv);
		int labelIndex = targetLabelAlphabet.lookupIndex(label, true) ;
		carrier.setTarget(targetLabelAlphabet.lookupLabel(labelIndex));
		return carrier;
	}

	public Instance createDecodeInstance(Feature[] feature) {
		SparseVector fvector = createFeatureVector(feature, false);
		Instance carrier = new Instance(null, null, null, null) ;
		carrier.setData(fvector);
		carrier.setTarget(targetLabelAlphabet);
		return carrier;
	}
	
	public SparseVector createFeatureVector(Feature[] feature, boolean add) {
		int[] findices = new int[feature.length] ;
		double[] weight = new double[feature.length] ;
		for(int i = 0; i < findices.length; i++) {
			int index  = dataAlphabet.lookupIndex(feature[i].getFeature(), add);
			if(index < 0) {
				throw new RuntimeException("Feature " + feature[i].getFeature() + " is not found in the dictionary") ;
			}
			findices[i] = index;
			weight[i]   = feature[i].getWeight() * feature[i].getTermFrequency() ;
			weight[i] = 1d ;
		}
		//weight = normalize(weight) ;
		SparseVector fv = new FeatureVector(dataAlphabet, findices, weight);
		return fv ;
	}
	
	private double[] normalize(double[] weight) {
		double max = 0d ;
		for(int i = 0; i < weight.length; i++) {
			if(weight[i] > max) max = weight[i] ;
		}
		for(int i = 0; i < weight.length; i++) weight[i] = weight[i]/max  ;
		return weight ;
	}
}