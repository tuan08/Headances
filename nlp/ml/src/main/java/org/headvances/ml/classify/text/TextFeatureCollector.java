package org.headvances.ml.classify.text;

import org.headvances.ml.feature.ChiSquareFeatures;
import org.headvances.ml.feature.DeltaIDFFeatures;
import org.headvances.ml.feature.Feature;
import org.headvances.ml.feature.FeatureDictionary;
import org.headvances.ml.feature.FeatureGenerator;
import org.headvances.ml.feature.FeatureHolder;
import org.headvances.ml.feature.GenerateFeature;
import org.headvances.ml.feature.IDFFeatures;
import org.headvances.ml.feature.Labels;
import org.headvances.ml.feature.MapFeatureHolder;

public class TextFeatureCollector {
	private FeatureGenerator<TextDocument> generator;
	private int docIdTracker;
	private Labels labels = new Labels() ;
	private double threashold = 0.125;

	private ChiSquareFeatures chiSquare;
	private IDFFeatures idf;
	private DeltaIDFFeatures deltaIdf;

	public TextFeatureCollector(FeatureGenerator<TextDocument> generator) {
		this.generator = generator;
		chiSquare = new ChiSquareFeatures();
		idf = new IDFFeatures();
		deltaIdf = new DeltaIDFFeatures();
	}

	public void collect(TextDocument doc){
		FeatureHolder holder = new MapFeatureHolder(docIdTracker) ;
		generator.generate(holder, doc) ;
		labels.collect(doc.getLabel());
		collect(holder.getFeatures(), doc.getLabel());
		docIdTracker++;
	}

	protected void collect(GenerateFeature[] gfeatures, String labelName){
		//chiSquare.add(gfeatures, labelName);
		idf.add(gfeatures, labelName);
		//deltaIdf.add(gfeatures, labelName);
	}

	public void setChisquareThreashold(double threashold){ this.threashold = threashold; }

	public FeatureDictionary getChisquareFeatureDictionary() {
		FeatureDictionary dict = new FeatureDictionary() ;
		chiSquare.setNumOfDocId(this.docIdTracker);
		dict.setLabels(labels);

		Feature[] features = chiSquare.getFeatures(threashold);
		for(Feature f: features) dict.addFeature(f);
		return dict;
	}

	public FeatureDictionary getIDFFeatureDictionary() {
		FeatureDictionary dict = new FeatureDictionary() ;
		idf.setNumOfDocId(this.docIdTracker);
		dict.setLabels(labels);

		Feature[] features = idf.getFeatures();
		for(Feature f: features) {
			if(Feature.Type.NGRAM == f.getType()) {
				int spaceCount = 0;
				for(char c : f.getFeature().toCharArray()) {
					if(c == ' ') spaceCount++ ;
				}
				if(spaceCount == 1) {
					if(f.getDocFrequency() > 3) dict.addFeature(f);
				} else {
					if(f.getDocFrequency() > 2) dict.addFeature(f);
				}
			} else {
				dict.addFeature(f);
			}
		}
		return dict;
	}
	
	public FeatureDictionary getDeltaIDFFeatureDictionary(){
	  FeatureDictionary dict = new FeatureDictionary();
	  deltaIdf.setNumOfDocTracker(docIdTracker);
	  dict.setLabels(labels);

	  Feature[] features = deltaIdf.getFeatures();
	  for(Feature f: features) dict.addFeature(f);
	  return dict;
	}
}