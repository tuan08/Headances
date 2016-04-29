package org.headvances.ml.nlp.opinion;

import java.util.List;

import org.headvances.ml.feature.FeatureDictionary;
import org.headvances.ml.feature.FeatureGenerator;


public class OpinionFeatureDictionaryBuilder {
  private OpinionFeatureCollector oCollector;
  
  public OpinionFeatureDictionaryBuilder(FeatureGenerator<Opinion> generator){
    oCollector = new OpinionFeatureCollector(generator);
  }
  
  public void process(List<Opinion> opinions){
    for(Opinion op: opinions) oCollector.collect(op);
  }

  public FeatureDictionary getIDFDictionary(){
    FeatureDictionary idfDict       = oCollector.getIDFFeatureDictionary() ;
    idfDict.resetFeatureId() ;
    return idfDict;
  } 
}
