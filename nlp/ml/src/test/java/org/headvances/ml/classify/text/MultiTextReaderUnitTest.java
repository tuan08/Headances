package org.headvances.ml.classify.text;

import org.headvances.ml.classify.text.MultiTextFileReader;
import org.headvances.ml.classify.text.TextDocument;
import org.junit.Test;

public class MultiTextReaderUnitTest {
  private String dataDir = "d:/headvances/test/text";
  
  @Test
  public void test() throws Exception{
    MultiTextFileReader reader = new MultiTextFileReader(dataDir);
    
    TextDocument doc;
    while((doc = reader.next()) != null){
      System.out.println(doc.getLabel() + " : " + doc.getContent());
    }
    reader.close();
    
  }

}
