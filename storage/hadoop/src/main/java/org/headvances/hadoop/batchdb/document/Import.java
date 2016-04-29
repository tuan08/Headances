package org.headvances.hadoop.batchdb.document;

import org.apache.hadoop.conf.Configuration;
import org.headvances.data.Document;
import org.headvances.data.Entity;
import org.headvances.data.HtmlDocumentUtil;
import org.headvances.hadoop.batchdb.Database;
import org.headvances.hadoop.batchdb.Reporter;
import org.headvances.hadoop.batchdb.Row;
import org.headvances.hadoop.util.ConfigurationFactory;
import org.headvances.json.JSONMultiFileReader;
import org.headvances.util.CommandParser;
import org.headvances.util.TimeReporter;
import org.headvances.util.html.URLNormalizer;
import org.headvances.util.text.StringUtil;
/**
 * $Author: Tuan Nguyen$ 
 **/
public class Import {
  static public void main(String[] args) throws Exception {
    if(args.length == 0) {
      args = new String[] {
          "-db",   "target/dbtest",
          "-file", "e:/testdata",
          "-compression", "lzo",
          "-partition", "16"
      };
    }
    CommandParser command = new CommandParser("import:") ;
    command.addMandatoryOption("db", true, "The database location") ;
    command.addMandatoryOption("file", true, "The input file location") ;
    command.addOption("entity", true, "The entities of the input documents") ;
    command.addOption("compression", true, "The compression codec default/gzip/lzo");
    command.addOption("partition", true, "The frequency of partitions") ;
    command.addOption("setId", false, "Should set ID or not");
    if(!command.parse(args)) return ;
    command.printHelp() ;

    String db   = command.getOption("db", null) ;
    String file = command.getOption("file", null) ;
    String entities = command.getOption("entity", "html") ;
    int partition = Integer.parseInt(command.getOption("partition", "16")) ;
    String[] entity = null ;
    if("html".equals(entities)) {
      entity = HtmlDocumentUtil.ENTITIES ;
    } else {
      entity = StringUtil.toStringArray(entities) ;
    }

    String compression = command.getOption("compression", null) ;
    if(compression != null) {
      ConfigurationFactory.setCompressionCodec(compression) ;
    }

    Configuration conf = ConfigurationFactory.getConfiguration();
    Database database = DocumentDBFactory.getDatabase(conf, db, entity, partition) ;
    Database.Writer writer = database.getWriter() ;
    Reporter.LocalReporter reporter = new Reporter.LocalReporter("Database Write");

    TimeReporter timeReporter = new TimeReporter();
    TimeReporter.Time totalTime = timeReporter.getTime("Total");
    totalTime.start();

    TimeReporter.Time lastRoundTime = timeReporter.getTime("lastRound");
    lastRoundTime.start();

    JSONMultiFileReader reader = new JSONMultiFileReader(file) ;
    Document doc = null ;
    int count = 0;
    DocumentMapper mapper = new DocumentMapper() ;
    while((doc = reader.next(Document.class)) != null) {
      if(command.hasOption("setId")){
        Entity htmlEntity = HtmlDocumentUtil.getHtmlLink(doc);
        if(htmlEntity == null) continue;
        String url = htmlEntity.get("url") ;
        URLNormalizer urlNorm = new URLNormalizer(url) ;
        doc.setId(urlNorm.getHostMD5Id()) ;
      }

      Row row = mapper.toRow(doc) ;
      writer.write(row.getRowId(), row, reporter) ;

      if(count%20000 == 0) {
        lastRoundTime.stop();

        reporter.getStatisticMap().report(System.out) ;
        timeReporter.report(System.out);

        lastRoundTime.start();
      }
      count++;
    }
    reader.close() ;
    writer.close() ;
    database.autoCompact(reporter) ;
    lastRoundTime.stop();
    totalTime.stop();
    reporter.getStatisticMap().report(System.out) ;
    timeReporter.report(System.out);
  }
}