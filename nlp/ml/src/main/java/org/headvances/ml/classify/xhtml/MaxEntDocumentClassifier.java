package org.headvances.ml.classify.xhtml;

import java.io.ObjectInputStream;

import org.headvances.html.dom.TDocument;
import org.headvances.ml.Classifier;
import org.headvances.ml.Predict;
import org.headvances.ml.feature.Feature;
import org.headvances.ml.feature.FeatureDictionary;
import org.headvances.ml.feature.FeatureExtractor;
import org.headvances.ml.maxent.FeatureInstanceFactory;
import org.headvances.util.IOUtil;
import org.headvances.util.statistic.StatisticsSet;

import cc.mallet.classify.MaxEnt;
import cc.mallet.types.Instance;
import cc.mallet.types.Labeling;
/**
 * $Author: Tuan Nguyen$
 **/
public class MaxEntDocumentClassifier implements Classifier<TDocument> {
	private MaxEnt                      maxent ;
	private FeatureInstanceFactory      factory  ;
	private FeatureExtractor<TDocument> featuerExtractor ;
	private double trustThreshold = 0.85;

	public MaxEntDocumentClassifier() throws Exception {
		this("classpath:ml/xhtml/ContentType.maxent.model", "classpath:ml/xhtml/ContentTypeDictionary.maxent.json") ;
	}

	public MaxEntDocumentClassifier(String modelRes, String dictRes) throws Exception {
		ObjectInputStream s = new ObjectInputStream(IOUtil.loadRes(modelRes));
		init((MaxEnt) s.readObject(), FeatureDictionary.load(IOUtil.loadRes(dictRes)));
	}

	private void init(MaxEnt maxent, FeatureDictionary dict) throws Exception {
		this.maxent = maxent ;
		factory = new FeatureInstanceFactory(maxent.getAlphabet(), maxent.getLabelAlphabet()) ;
		DocumentFeatureGenerator featureGenerator = new DocumentFeatureGenerator();
		this.featuerExtractor = MaxentDocumentTrainer.createFeatureExtractor(featureGenerator, dict) ;
	}

	public void setTrustThreshold(double val) { this.trustThreshold = val ; }

	public boolean isTrusted(Predict predict) {
		return predict.probability > trustThreshold ;
	}

	public Predict predict(TDocument tdoc) throws Exception {
		Predict[] predict = classify(tdoc) ;
		Predict best = Predict.getBestPredict(predict) ;
		return best ;
	}

	public Predict[] classify(TDocument tdoc) throws Exception {
		Instance instance = factory.createDecodeInstance(featuerExtractor.extract(tdoc)) ;
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

	public void report(StatisticsSet map, TDocument tdoc) {
		Feature[] features = featuerExtractor.extract(tdoc);
		map.incr("Extract Features", "all" , 1) ;
		Util.report(map, "Extract Features", features);
	}
	
	public static void main(String [] args) throws Exception{
	  MaxEntDocumentClassifier classifier = new MaxEntDocumentClassifier();
	}
}