package org.headvances.ml.classify.xhtml;

import java.io.IOException;
import java.io.PrintStream;

import org.headvances.data.Document;
import org.headvances.html.dom.TDocument;
import org.headvances.html.fetcher.HttpClientFetcher;
import org.headvances.ml.classify.xhtml.DocumentFeatureGenerator;
import org.headvances.ml.feature.Feature;
import org.headvances.ml.feature.FeatureDictionary;
import org.headvances.ml.feature.FeatureExtractor;
import org.headvances.ml.feature.FeatureGenerator;
import org.headvances.ml.svm.SVMNodeBuilder;
import org.headvances.util.IOUtil;
import org.junit.Test;

public class FeatureExtractorUnitTest {
  private FeatureExtractor<TDocument> fextractor;
  private SVMNodeBuilder svmNodeBuilder;
  private String dictRes = "file:d:/headvances/test/features.dict";
 
  private String url = "http://dantri.com.vn/c20/s20-527484/xe-buyt-xung-dang-nhan-may-diem.htm";
  
  @Test
  public void test() throws Exception{
    FeatureGenerator<TDocument> generator = new DocumentFeatureGenerator();
    fextractor = new FeatureExtractor<TDocument>(generator);
    
    FeatureDictionary dict = FeatureDictionary.load(IOUtil.loadRes(dictRes));
    svmNodeBuilder = new SVMNodeBuilder(dict);
    
    HttpClientFetcher fetcher = new HttpClientFetcher();
    Document doc = fetcher.fetch(url);
    report(System.out, TDocument.create(doc));
    
  }
  
  public void report(PrintStream out, TDocument tdoc) throws IOException{
    Feature[] gfeatures = fextractor.extract(tdoc);
    Feature[] vfeatures = svmNodeBuilder.getValidFeatures(gfeatures);
    report(out, tdoc, gfeatures, vfeatures);
  }
  
  public void report(PrintStream out, TDocument tdoc, Feature[] gfeatures, Feature[] vfeatures) throws IOException{
    StringBuilder b = new StringBuilder();
    b.append("\nURL:   ").append(tdoc.getUrl()).append("\n");
    b.append("Title: ").append(tdoc.getAnchorText()).append("\n\n");
   
    b.append("Generate features: ").append(gfeatures.length).append("\n");
    for(int i = 0; i < gfeatures.length; i++){
      if(i % 5 == 0) b.append("\r");
      b.append(gfeatures[i].getFeature()).append(", "); 
    }
    b.append("\n");
    
    b.append("\nValid features: ").append(vfeatures.length).append("\n");
    for(int i = 0; i < vfeatures.length ; i++){
      if(i % 5 == 0) b.append("\r");
      b.append(vfeatures[i].getFeature()).append(", "); 
    }
    b.append("\n\n***************************************************************\n");
    out.append(b.toString());
  }
  

}
