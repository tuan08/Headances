package org.headvances.ml.classify.text;

import org.headvances.ml.classify.text.MultiTextFileReader;
import org.headvances.ml.classify.text.TextDocument;
import org.headvances.ml.classify.text.TextFeatureCollector;
import org.headvances.ml.classify.text.TextFeatureGenerator;
import org.headvances.ml.feature.FeatureDictionary;
import org.junit.Test;

public class TextFeatureCollectorUnitTest {
  final static String dataDir = "d:/headvances/test/text";
  
  @Test
  public void test() throws Exception {
    TextFeatureGenerator generator = new TextFeatureGenerator();
    TextFeatureCollector collector = new TextFeatureCollector(generator);

    MultiTextFileReader reader = new MultiTextFileReader(dataDir);
    TextDocument doc;
    while((doc = reader.next()) != null){
      collector.collect(doc);
    }
    report(collector.getDeltaIDFFeatureDictionary());
//    report(collector.getChisquareFeatureDictionary());
  }
  
  private void report(FeatureDictionary dict) throws Exception{
    dict.resetFeatureId();
    dict.save("d:/dict.json");
//    Feature[] features = dict.getFeatures();
//    for(Feature f: features){
//      System.out.println(f.getFeature() + ": " + f.getWeight());
//    }
  }

}
