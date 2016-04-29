package org.headvances.ml.classify.xhtml;

import java.io.FileOutputStream;
import java.io.PrintStream;
import java.util.HashMap;
import java.util.Map;

import org.headvances.html.dom.TDocument;
import org.headvances.ml.classify.xhtml.DocumentFeatureGenerator;
import org.headvances.ml.classify.xhtml.FeatureDictionaryBuilder;
import org.headvances.ml.feature.Feature;
import org.headvances.ml.feature.FeatureDictionary;
import org.headvances.ml.feature.FeatureGenerator;
import org.headvances.util.FileUtil;
import org.headvances.util.statistic.StatisticsSet;
import org.junit.Test;

/**
 * $Author: Tuan Nguyen$ 
 **/
public class DocumentDictionaryFeatureBuilderUnitTest {
  final static String saveDir = "d:/headvances/test/dict";
  @Test
  public void report() throws Exception {
    FeatureGenerator<TDocument> generator = new DocumentFeatureGenerator(); 
    FeatureDictionaryBuilder dictBuilder = new FeatureDictionaryBuilder(generator);
    dictBuilder.setThreshold(0.075);
    dictBuilder.process("e:/label-data");
    report(dictBuilder.getIDFDictionary(), "IDF Dictionary");
    report(dictBuilder.getChisquareDictionary(), "Final Dictionary");
    
//    save(dictBuilder.getDictionary(), saveDir);
  }
  
  private void save(FeatureDictionary dict, String saveDir) throws Exception{
    if(!FileUtil.exist(saveDir)) FileUtil.mkdirs(saveDir);
    Feature[] feature = dict.getFeatures() ;
    Map<String, PrintStream> writers = new HashMap<String, PrintStream>();
    
    System.out.println("--------------------------------------------------------------") ;
    StatisticsSet reporter = new StatisticsSet() ;

    for(Feature sel : feature) {
      String ft = sel.getFeature() ;
      int idx = ft.lastIndexOf(':') ;
      String category = ft.substring(0, idx) ;
      
      reporter.incr("Feature", "all", 1) ;
      reporter.incr("Feature", category, "all", 1) ;
      
      if(category.equals("link") || category.equals("text")){ category = "short-text";}
      else if(category.startsWith("url")){ category = "url"; }
      
      PrintStream out = writers.get(category);
      if(out == null){ 
        out = new PrintStream(new FileOutputStream(saveDir + "/" + category +".txt", true));
        writers.put(category, out);
      } else out.println(ft.substring(idx + 1, ft.length()));
    }
    reporter.report(System.out) ;
  }
  
  private void report(FeatureDictionary dict, String dictName) {
    System.out.println(dictName) ;
    System.out.println("--------------------------------------------------------------") ;
    StatisticsSet reporter = new StatisticsSet() ;
    Feature[] feature = dict.getFeatures() ;
    for(Feature sel : feature) {
      String ft = sel.getFeature() ;
      int idx = ft.lastIndexOf(':') ;
      String category = ft.substring(0, idx) ;
      reporter.incr("Feature", "all", 1) ;
      reporter.incr("Feature", category, "all", 1) ;
      int docFrq = sel.getDocFrequency() ;
      
      reporter.incr("Doc Frequency",  "all", 1) ;
      if(docFrq == 1)       reporter.incr("Doc Frequency", "      1" , "all", 1) ;
      else if(docFrq == 2)  reporter.incr("Doc Frequency", "      2" , "all", 1) ;
      else if(docFrq == 3)  reporter.incr("Doc Frequency", "      3" , "all", 1) ;
      else if(docFrq == 4)  reporter.incr("Doc Frequency", "      4" , "all", 1) ;
      else if(docFrq == 5)  reporter.incr("Doc Frequency", "      5" , "all", 1) ;
      else if(docFrq <= 10) reporter.incr("Doc Frequency", " 5 - 10" , "all", 1) ;
      else if(docFrq <= 20) reporter.incr("Doc Frequency", "10 - 20" , "all", 1) ;
      else if(docFrq <= 30) reporter.incr("Doc Frequency", "20 - 30" , "all", 1) ;
      else if(docFrq <= 40) reporter.incr("Doc Frequency", "30 - 40" , "all", 1) ;
      else if(docFrq <= 50) reporter.incr("Doc Frequency", "40 - 50" , "all", 1) ;
      else                  reporter.incr("Doc Frequency", "   > 50" , "all", 1) ;
    }
    reporter.report(System.out) ;
  }
}