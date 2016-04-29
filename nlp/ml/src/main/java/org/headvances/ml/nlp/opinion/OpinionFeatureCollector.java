package org.headvances.ml.nlp.opinion;

import org.headvances.ml.feature.Feature;
import org.headvances.ml.feature.FeatureDictionary;
import org.headvances.ml.feature.FeatureGenerator;
import org.headvances.ml.feature.FeatureHolder;
import org.headvances.ml.feature.GenerateFeature;
import org.headvances.ml.feature.IDFFeatures;
import org.headvances.ml.feature.Labels;
import org.headvances.ml.feature.MapFeatureHolder;

public class OpinionFeatureCollector {
  private FeatureGenerator<Opinion> generator;
  
  private int opinionIdTracker;
  private Labels labels = new Labels() ;
  
  private IDFFeatures idf;
  
  
  public OpinionFeatureCollector(FeatureGenerator<Opinion> generator){
    this.generator = generator;
    idf = new IDFFeatures();
  }
  
  public void collect(Opinion op){
    FeatureHolder holder = new MapFeatureHolder(opinionIdTracker) ;
    generator.generate(holder, op) ;
    
    String targetLabel = op.getLabel();
    collect(holder.getFeatures(), targetLabel);
    labels.collect(targetLabel);
    opinionIdTracker++;
  }
  
  protected void collect(GenerateFeature[] gfeatures, String targetLabel){
    idf.add(gfeatures, targetLabel);
  }
  
  public FeatureDictionary getIDFFeatureDictionary() {
    FeatureDictionary dict = new FeatureDictionary() ;
    idf.setNumOfDocId(this.opinionIdTracker);
    dict.setLabels(labels);
    Feature[] features = idf.getFeatures();
    for(Feature f: features) {
    	dict.addFeature(f);
    }
    return dict;
  }
}
