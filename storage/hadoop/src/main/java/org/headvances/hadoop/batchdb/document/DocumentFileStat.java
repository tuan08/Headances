package org.headvances.hadoop.batchdb.document;

import org.headvances.data.Document;
import org.headvances.data.Entity;
import org.headvances.data.HtmlDocumentUtil;
import org.headvances.json.JSONMultiFileReader;
import org.headvances.util.CommandParser;
import org.headvances.util.html.URLNormalizer;
import org.headvances.util.statistic.StatisticsSet;


public class DocumentFileStat {

  void run(String[] args){
    CommandParser command = new CommandParser("Document check:") ;
    command.addMandatoryOption("data", true, "The data location") ;

    if(!command.parse(args)) return ;
    command.printHelp() ;

    String data = command.getOption("data", null);
    StatisticsSet reporter = new StatisticsSet();

    JSONMultiFileReader reader;
    int count = 0;

    try {
      reader = new JSONMultiFileReader(data);
      Document doc = null;
      while((doc = reader.next(Document.class)) != null){ 
        Entity entity = HtmlDocumentUtil.getHtmlLink(doc);
        if(entity == null) {
          reporter.incr("Entity", "NULL", 1);
          continue;
        }
        String url = entity.get("url") ;
        URLNormalizer urlNorm = new URLNormalizer(url) ;
        reporter.incr("Domain", "All", 1);
        reporter.incr("Domain", urlNorm.getHost(), "All", 1);

        if(count % 10000 == 0) reporter.report(System.out);
        count++; 
      };
      reader.close();
    } catch (Exception e) {
      System.out.println("Predicted document: " + count);
      System.out.println("Predicted file: " + (count/500));
    }
  }

  public static void main(String[] args) {
    if(args == null || args.length == 0)
      args = new String[] {
        "-data", "e:/test"
    };
    DocumentFileStat reporter = new DocumentFileStat();
    reporter.run(args);
  }

}
