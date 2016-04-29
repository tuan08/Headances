package org.headvances.ml.classify.text;

import org.headvances.ml.feature.FeatureDictionary;
import org.headvances.ml.feature.FeatureGenerator;

public class TextFeatureDictionaryBuilder {
  private TextFeatureCollector textCollector;
  
  public TextFeatureDictionaryBuilder(FeatureGenerator<TextDocument> generator){
    textCollector = new TextFeatureCollector(generator);
  }
  
  public void process(String dataDir) throws Exception{
    MultiTextFileReader reader = new MultiTextFileReader(dataDir);
    TextDocument doc;
    System.out.print("Collect Feature ") ;
    int count = 0;
    while((doc = reader.next()) != null){
      textCollector.collect(doc);
      count++ ;
      if(count % 1000 == 0) {
        System.out.print('.') ;
      }
    }
    reader.close();
    System.out.println() ;
  }
  
  public void setThreashold(double threashold){ textCollector.setChisquareThreashold(threashold); }
  
  public FeatureDictionary getDictionary(){
    FeatureDictionary idfDict       = textCollector.getIDFFeatureDictionary() ;
    FeatureDictionary chisquareDict = textCollector.getChisquareFeatureDictionary() ;
    
    chisquareDict.resetFeatureParameter(idfDict) ;
    FeatureDictionary retDict = new FeatureDictionary() ;
    retDict.setLabels(chisquareDict.getLabels()) ;
    retDict.add(chisquareDict, true) ;
    retDict.resetFeatureId() ;
    return retDict ;
  }
  
  public FeatureDictionary getIDFDictionary(){
    FeatureDictionary idfDict       = textCollector.getIDFFeatureDictionary() ;
    idfDict.resetFeatureId() ;
    return idfDict;
  } 
  
  public FeatureDictionary getDeltaIDFDictionary(){
    FeatureDictionary deltaIdfDict = textCollector.getDeltaIDFFeatureDictionary();
    deltaIdfDict.resetFeatureId(); 
    
    return deltaIdfDict;
  }
}