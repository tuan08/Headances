package org.headvances.ml.svm;

import java.io.FileInputStream;

import junit.framework.Assert;

import org.headvances.html.dom.TDocument;
import org.headvances.ml.classify.xhtml.DocumentFeatureGenerator;
import org.headvances.ml.classify.xhtml.feature.FeatureCollector;
import org.headvances.ml.classify.xhtml.feature.IDFFeatureCollector;
import org.headvances.ml.feature.Feature;
import org.headvances.ml.feature.FeatureDictionary;
import org.headvances.ml.feature.FeatureGenerator;
import org.junit.Test;
/**
 * $Author: Tuan Nguyen$ 
 **/
public class MultiDocFeatureCollectorUnitTest {
  
  @Test
  public void test() throws Exception {
    
    String path = "d:/headvances/test/data";
    
    FeatureGenerator<TDocument>    generator = new DocumentFeatureGenerator(); 
    FeatureCollector collector = new IDFFeatureCollector(generator) ;
//    FeatureCollector collector = new ChiSquareFeatureCollector(generator) ;

    collector.collect(path);
    FeatureDictionary dictionary = collector.getFeatureDictionary() ;
    dictionary.dump(System.out) ;
    dictionary.save("target/dictionary.json") ;

//    Assert Data
    FeatureDictionary loadDictionary = 
    FeatureDictionary.load(new FileInputStream("target/dictionary.json")) ;
   
    assertDictionary(dictionary, loadDictionary);
   
  }
  
  public void assertDictionary(FeatureDictionary expectDict, FeatureDictionary actualDict){
    String[] expectLabels = expectDict.getLabels().getLabels();
    String[] actualLabels = actualDict.getLabels().getLabels();
    Assert.assertEquals(expectLabels.length, actualLabels.length);
    for(int i = 0; i < expectLabels.length; i++){
      Assert.assertEquals(expectLabels[i], actualLabels[i]);
    }
    
    Feature[] expectFeatures = expectDict.getFeatures();
    Feature[] actualFeatures = actualDict.getFeatures();
    Assert.assertEquals(expectFeatures.length, actualFeatures.length);
    for(int i = 0; i < expectFeatures.length; i++){
      Assert.assertEquals(expectFeatures[i].getFeature(), actualFeatures[i].getFeature());
      Assert.assertEquals(expectFeatures[i].getWeight(), actualFeatures[i].getWeight());
    }
  }
}