package org.headvances.nlp.ml.classify;

import java.io.ObjectInputStream;

import org.headvances.html.dom.TDocument;
import org.headvances.nlp.ml.classify.Feature;
import org.headvances.nlp.ml.classify.FeaturesGenerator;
import org.headvances.nlp.ml.classify.MalletMaxentFeatureInstanceFactory;
import org.headvances.nlp.ml.classify.Predict;
import org.headvances.util.IOUtil;

import cc.mallet.classify.MaxEnt;
import cc.mallet.types.Instance;
import cc.mallet.types.Labeling;
/**
 * $Author: Tuan Nguyen$
 **/
public class MaxEntClassifierBase<T> {
	private MaxEnt                      maxent ;
	private MalletMaxentFeatureInstanceFactory factory ;
	private FeaturesGenerator<T> fgenerator ;
	private double trustThreshold = 0.85;

	public MaxEntClassifierBase(FeaturesGenerator<T> fgenerator) throws Exception {
		this("classpath:nlp/xhtml/model.maxent", fgenerator) ;
	}

	public MaxEntClassifierBase(String modelRes, FeaturesGenerator<T> fgenerator) throws Exception {
		this.fgenerator = fgenerator ;
		ObjectInputStream s = new ObjectInputStream(IOUtil.loadRes(modelRes));
		this.maxent = (MaxEnt) s.readObject() ;
		maxent.getAlphabet().stopGrowth() ;
		maxent.getLabelAlphabet().stopGrowth() ;
		factory = new MalletMaxentFeatureInstanceFactory(maxent.getAlphabet(), maxent.getLabelAlphabet()) ;
	}

	public void setTrustThreshold(double val) { this.trustThreshold = val ; }

	public boolean isTrusted(Predict predict) {
		return predict.probability > trustThreshold ;
	}

	public Predict predict(T tdoc) throws Exception {
		Predict[] predict = classify(tdoc) ;
		Predict best = Predict.getBestPredict(predict) ;
		return best ;
	}

	public Predict[] classify(T tdoc) throws Exception {
		Feature[] feature = fgenerator.generate(tdoc).getFeature() ;
		Instance instance = factory.createDecodeInstance(feature) ;
		Labeling labeling = maxent.classify(instance).getLabeling();
		// print the labels with their weights in descending order (ie best first)                     
		Predict[] predict = new Predict[labeling.numLocations()] ;
		for (int rank = 0; rank < labeling.numLocations(); rank++){
			String label = labeling.getLabelAtRank(rank).toString();
			double probability = labeling.getValueAtRank(rank) ;
			predict[rank] = new Predict(label, probability) ;
			//System.out.print(label + ":" + probability + "\n");
		}
		return predict ;
	}
}