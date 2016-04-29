package org.headvances.hadoop.batchdb.document;

import org.apache.hadoop.conf.Configuration;
import org.headvances.data.Document;
import org.headvances.hadoop.batchdb.Database;
import org.headvances.hadoop.batchdb.Row;
import org.headvances.hadoop.util.ConfigurationFactory;
import org.headvances.json.JSONMultiFileWriter;
import org.headvances.util.CommandParser;
import org.headvances.util.TimeReporter;
import org.headvances.util.statistic.StatisticsSet;
/**
 * $Author: Tuan Nguyen$ 
 **/
public class Export {
  static public void main(String[] args) throws Exception {
    if(args.length == 0) {
      args = new String[] {
          "-db",   "target/db",
          "-exportDir", "target/samplejson"
      };
    }

    CommandParser command = new CommandParser("export:") ;
    command.addMandatoryOption("db", true, "The database location") ;
    command.addMandatoryOption("exportDir", true, "The output file location") ;
    command.addOption("maxDocPerFile", true, "Maximum frequency of document per file") ;
    if(!command.parse(args)) return ;
    command.printHelp() ;
    String db   = command.getOption("db", null) ;
    String exportDir = command.getOption("exportDir", null) ;
    int maxDocPerFile = command.getOption("maxDocPerFile", 5000);
    
    Configuration conf = ConfigurationFactory.getConfiguration();
    Database database = Database.getDatabase(db, conf) ;
    StatisticsSet reporter = new StatisticsSet();
    Database.Reader reader = database.getReader() ;

    JSONMultiFileWriter writer =  new JSONMultiFileWriter(exportDir);
    writer.setCompress(true) ;
    writer.setMaxDocumentPerFile(maxDocPerFile) ;


    TimeReporter timeReporter = new TimeReporter();
    TimeReporter.Time totalTime = timeReporter.getTime("Total");
    totalTime.start();

    TimeReporter.Time lastRoundTime = timeReporter.getTime("lastRound");
    lastRoundTime.start();

    int count = 0;

    Row row = null ;
    DocumentMapper mapper = new DocumentMapper() ;
    while((row = reader.next()) != null) {
      Document doc = mapper.fromRow(row) ;
      writer.write(doc) ;
      reporter.incr("Document", "All", 1) ;
      count++;
      if(count%20000 == 0) {
        lastRoundTime.stop();
        reporter.report(System.out) ;
        timeReporter.report(System.out);
        lastRoundTime.start();
      }
    }
    reader.close() ;
    writer.close() ;
    
    lastRoundTime.stop();
    totalTime.stop();
    reporter.report(System.out) ;
    timeReporter.report(System.out);
  }
}
