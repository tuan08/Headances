package org.headvances.nlp.ml.token;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

import cc.mallet.types.Alphabet;
import cc.mallet.types.FeatureVector;
import cc.mallet.types.FeatureVectorSequence;
import cc.mallet.types.Instance;
import cc.mallet.types.LabelAlphabet;
import cc.mallet.types.LabelSequence;
/**
 * $Author: Tuan Nguyen$ 
 **/
public class MalletCRFFeatureVectorFactory  implements Serializable {
	private LabelAlphabet targetLabelAlphabet ;
	private Alphabet dataAlphabet ;
	
	public MalletCRFFeatureVectorFactory () {
		this.dataAlphabet = new Alphabet() ;
		this.targetLabelAlphabet = new LabelAlphabet() ;
	}
	
	public MalletCRFFeatureVectorFactory(Alphabet dataAlphabet, LabelAlphabet targetLabelAlphabet) {
		this.dataAlphabet = dataAlphabet ;
		this.targetLabelAlphabet = targetLabelAlphabet ;
	}
	
	public Alphabet getDataAlphabet() { return this.dataAlphabet ; }
	
	public Alphabet getTargetAlphabet() { return this.targetLabelAlphabet ; }
	
	public Instance createTrainInstance(TokenFeatures[] tokenFeatures) {
		FeatureVector[] fvs = new FeatureVector[tokenFeatures.length];
		LabelSequence target = new LabelSequence(targetLabelAlphabet, tokenFeatures.length);
		for (int l = 0; l < tokenFeatures.length; l++) {
			String targetFeature  = tokenFeatures[l].getTargetFeature() ;
			target.add(targetFeature);
			fvs[l] = getFeatureVector(dataAlphabet, tokenFeatures[l], true);
		}
		Instance carrier = new Instance(null, null, null, null) ;
		carrier.setData(new FeatureVectorSequence(fvs)) ;
		carrier.setTarget(target) ;
		return carrier;
	}
	
	public Instance createDecodeInstance(TokenFeatures[] tokenFeatures) {
		FeatureVector[] fvs = new FeatureVector[tokenFeatures.length];
		for (int l = 0; l < tokenFeatures.length; l++) {
			fvs[l] = getFeatureVector(dataAlphabet, tokenFeatures[l], false);
		}
		Instance carrier = new Instance(null, null, null, null) ;
		carrier.setData(new FeatureVectorSequence(fvs)) ;
		carrier.setTarget(new LabelSequence(targetLabelAlphabet)) ;
		return carrier;
	}
	
	FeatureVector getFeatureVector(Alphabet alphabet, TokenFeatures tfeatures, boolean add) {
		String[] feature = tfeatures.getFeatures() ;
		if(add) {
			int[] featureIndices = new int[feature.length] ;
			for(int j = 0; j < featureIndices.length; j++) {
				featureIndices[j] = alphabet.lookupIndex(feature[j]);
			}
			return new FeatureVector(alphabet, featureIndices);
		} else {
			List<Integer> holder = new ArrayList<Integer>() ;
			for(int j = 0; j < feature.length; j++) {
				int featureIndex = alphabet.lookupIndex(feature[j], false);
				if(featureIndex >= 0) holder.add(featureIndex) ;
			}
			int[] featureIndices = new int[holder.size()] ;
			for(int i = 0; i < featureIndices.length; i++) {
				featureIndices[i] = holder.get(i) ;
			}
			return new FeatureVector(alphabet, featureIndices);
		}
	}
}