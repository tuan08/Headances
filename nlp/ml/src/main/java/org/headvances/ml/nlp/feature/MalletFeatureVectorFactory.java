package org.headvances.ml.nlp.feature;

import java.io.Serializable;

import org.headvances.nlp.token.IToken;

import cc.mallet.types.Alphabet;
import cc.mallet.types.FeatureVector;
import cc.mallet.types.FeatureVectorSequence;
import cc.mallet.types.Instance;
import cc.mallet.types.LabelAlphabet;
import cc.mallet.types.LabelSequence;
/**
 * $Author: Tuan Nguyen$ 
 **/
public class MalletFeatureVectorFactory  implements Serializable {
	private LabelAlphabet targetLabelAlphabet ;
	private Alphabet dataAlphabet ;
	private TokenFeaturesGenerator featuresGenerator ;
	
	public MalletFeatureVectorFactory (TokenFeaturesGenerator featuresGenerator) {
		this.dataAlphabet = new Alphabet() ;
		this.targetLabelAlphabet = new LabelAlphabet() ;
		this.featuresGenerator = featuresGenerator ;
	}
	
	public MalletFeatureVectorFactory(Alphabet dataAlphabet, LabelAlphabet targetLabelAlphabet, 
			                              TokenFeaturesGenerator featuresGenerator) {
		this.dataAlphabet = dataAlphabet ;
		this.targetLabelAlphabet = targetLabelAlphabet ;
		this.featuresGenerator = featuresGenerator ;
	}
	
	public Alphabet getDataAlphabet() { return this.dataAlphabet ; }
	
	public Alphabet getTargetAlphabet() { return this.targetLabelAlphabet ; }
	
	public Instance createTrainInstance(IToken[] token) {
		TokenFeatures[] tokenFeatures = featuresGenerator.generate(token, true) ;
		FeatureVector[] fvs = new FeatureVector[tokenFeatures.length];
		LabelSequence target = new LabelSequence(targetLabelAlphabet, tokenFeatures.length);
		for (int l = 0; l < tokenFeatures.length; l++) {
			String targetFeature  = tokenFeatures[l].getTargetFeature() ;
			target.add(targetFeature);
			fvs[l] = tokenFeatures[l].getFeatureVector(dataAlphabet, true);
		}
		Instance carrier = new Instance(null, null, null, null) ;
		carrier.setData(new FeatureVectorSequence(fvs)) ;
		carrier.setTarget(target) ;
		return carrier;
	}
	
	public Instance createDecodeInstance(IToken[] token) {
		TokenFeatures[] tokenFeatures = featuresGenerator.generate(token, false) ;
		FeatureVector[] fvs = new FeatureVector[tokenFeatures.length];
		for (int l = 0; l < tokenFeatures.length; l++) {
			fvs[l] = tokenFeatures[l].getFeatureVector(dataAlphabet, false);
		}
		Instance carrier = new Instance(null, null, null, null) ;
		carrier.setData(new FeatureVectorSequence(fvs)) ;
		carrier.setTarget(new LabelSequence(targetLabelAlphabet)) ;
		return carrier;
	}
}