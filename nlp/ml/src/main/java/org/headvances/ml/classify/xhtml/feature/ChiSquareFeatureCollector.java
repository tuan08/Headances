package org.headvances.ml.classify.xhtml.feature;

import org.headvances.html.dom.TDocument;
import org.headvances.ml.feature.ChiSquareFeatures;
import org.headvances.ml.feature.Feature;
import org.headvances.ml.feature.FeatureDictionary;
import org.headvances.ml.feature.FeatureGenerator;
import org.headvances.ml.feature.GenerateFeature;

public class ChiSquareFeatureCollector extends FeatureCollector {
  private ChiSquareFeatures chiSquare; 
  private double threshold = 0.1;

  public ChiSquareFeatureCollector(FeatureGenerator<TDocument> generator){
  	super(generator) ;
  	chiSquare = new ChiSquareFeatures();
  }
  
  public void collect(GenerateFeature[] gfeatures, String labelName) {
    chiSquare.add(gfeatures, labelName);
  }
  
  public void setThreshold(double threashold){ this.threshold = threashold; }
  
  public FeatureDictionary getFeatureDictionary() {
    FeatureDictionary dict = new FeatureDictionary();
    chiSquare.setNumOfDocId(this.docIdTracker);
    dict.setLabels(this.getLabels());
    
    Feature[] features = chiSquare.getFeatures(threshold);
    for(Feature feature: features) dict.addFeature(feature);
    
   return dict; 
  }
}