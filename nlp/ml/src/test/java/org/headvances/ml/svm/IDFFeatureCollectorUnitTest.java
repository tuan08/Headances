package org.headvances.ml.svm;

import java.io.FileInputStream;

import junit.framework.Assert;

import org.headvances.data.Document;
import org.headvances.data.HtmlDocumentUtil;
import org.headvances.html.dom.TDocument;
import org.headvances.ml.classify.xhtml.DocumentFeatureGenerator;
import org.headvances.ml.classify.xhtml.feature.IDFFeatureCollector;
import org.headvances.ml.feature.Feature;
import org.headvances.ml.feature.FeatureDictionary;
import org.headvances.ml.feature.FeatureGenerator;
import org.junit.Test;
/**
 * $Author: Tuan Nguyen$ 
 **/
public class IDFFeatureCollectorUnitTest {
  static String HTML = 
    "<!DOCTYPE html PUBLIC '-//W3C//DTD XHTML 1.0 Transitional//EN'" +
    "  'http://www.w3.org/TR/xhtml1/DTD/xhtml1-transitional.dtd'>" +
    "<html xmlns='http://www.w3.org/1999/xhtml'>\n" +
    "  <head>\n" +
    "    <base href='http://www.vnexpress.net'/>" +
    "    <title>Hello world</title>\n" +
    "  </head>\n" +
    "  <body>\n" +
    "    <div><span><i></i></span></div>\n" +
    "    <div><span><i>test</i></span></div>\n" +
    "    <a href=''>a link</a>\n" +

    "    <div>\n" +
    "      <ul class='ULActionBlock' id='ULActionBlock'>\n" +
    "        <li><a href=''><img src=''/></a></li>\n" +
    "        <li><a href=''><img src=''/></a></li>\n" +
    "        <li><a href=''><img src=''/></a></li>\n" +
    "      </ul>\n" +
    "    </div>\n" +
    "    <br />" +
    "  </body>\n" +
    "</html>" ;

  @Test
  public void testTextNode() throws Exception {
    String url = "http://vnexpress.net/showthread.php?t=3611069" ;
    Document doc  = new Document() ;
    HtmlDocumentUtil.setHtmlLink(doc, "", url, 1, null) ;
    doc.setContent(HTML) ;

    FeatureGenerator<TDocument>    generator = new DocumentFeatureGenerator(); 
    IDFFeatureCollector collector = new IDFFeatureCollector(generator) ;
    collector.collect(doc) ;
    FeatureDictionary dictionary = collector.getFeatureDictionary() ;
    dictionary.dump(System.out) ;
    dictionary.save("target/dictionary.idf.json") ;

    FeatureDictionary loadDictionary = 
      FeatureDictionary.load(new FileInputStream("target/dictionary.idf.json")) ;
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