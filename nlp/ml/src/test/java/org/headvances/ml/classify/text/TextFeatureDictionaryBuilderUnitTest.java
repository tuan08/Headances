package org.headvances.ml.classify.text;

import org.headvances.ml.classify.text.TextDocument;
import org.headvances.ml.classify.text.TextFeatureDictionaryBuilder;
import org.headvances.ml.classify.text.TextFeatureGenerator;
import org.headvances.ml.feature.FeatureDictionary;
import org.headvances.ml.feature.FeatureGenerator;
import org.junit.Test;

public class TextFeatureDictionaryBuilderUnitTest {
  private String dataDir = "d:/headvances/test/text";
  @Test
  public void test() throws Exception{
    FeatureGenerator<TextDocument> generator = new TextFeatureGenerator();
    TextFeatureDictionaryBuilder dictBuilder = new  TextFeatureDictionaryBuilder(generator);
    dictBuilder.process(dataDir);
    
    FeatureDictionary dict = dictBuilder.getDictionary();
    dict.save("target/test.json");
  }
}
