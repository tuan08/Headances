package org.headvances.hadoop.batchdb.document;

import org.headvances.data.Document;
import org.headvances.data.Entity;
import org.headvances.data.HtmlDocumentUtil;
import org.headvances.json.JSONMultiFileReader;
import org.headvances.json.JSONMultiFileWriter;
import org.headvances.util.statistic.StatisticsSet;

public class DocumentFilter {
  
  public static void main(String[] args) throws Exception{
    String importDir = "D:/sample/samplejson";
    String okDir = "D:/sample/sampleOK";
    String filterDir = "D:/sample/sampleFilter";
    
    JSONMultiFileWriter okWriter =  new JSONMultiFileWriter(okDir);
    okWriter.setCompress(true) ;
    okWriter.setMaxDocumentPerFile(250) ;
    
    JSONMultiFileWriter fWriter =  new JSONMultiFileWriter(filterDir);
    fWriter.setCompress(true) ;
    fWriter.setMaxDocumentPerFile(250) ;
    
    StatisticsSet reporter = new StatisticsSet();
    
    JSONMultiFileReader reader = new JSONMultiFileReader(importDir) ;
    Document doc = null ;
    while((doc = reader.next(Document.class)) != null) {
      Entity htmlLink = HtmlDocumentUtil.getHtmlLink(doc) ;
      String anchorText = htmlLink.get("anchorText") ;
      System.out.println(anchorText);
      String label = doc.getLabels()[0];
      reporter.incr("Document", "All", 1) ;
      if(label.equals("content:ignore")){
        reporter.incr("Document", "ignore", "All", 1);
        fWriter.write(doc);
      } else { 
        okWriter.write(doc);
        reporter.incr("Document", "Ok", "All", 1);
      }
    }
    
    reader.close() ;
    fWriter.close();
    okWriter.close();
    
    reporter.report(System.out);
  }
  

}
