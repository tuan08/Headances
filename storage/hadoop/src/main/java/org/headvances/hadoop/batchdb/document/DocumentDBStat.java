package org.headvances.hadoop.batchdb.document;

import java.io.FileOutputStream;
import java.io.PrintStream;

import org.apache.hadoop.conf.Configuration;
import org.headvances.data.Document;
import org.headvances.hadoop.batchdb.Database;
import org.headvances.hadoop.batchdb.Row;
import org.headvances.hadoop.util.ConfigurationFactory;
import org.headvances.util.CommandParser;
import org.headvances.util.statistic.StatisticsSet;
/**
 * $Author: Tuan Nguyen$ 
 **/
public class DocumentDBStat {
	static public void main(String[] args) throws Exception {
		if(args.length == 0) {
			args = new String[] {
			  "-db",   "d:/webdb",
			};
		}
		
		CommandParser command = new CommandParser("stat:") ;
		command.addMandatoryOption("db", true, "The database location") ;
		command.addOption("incrReport", true, "Increment report") ;
		command.addOption("oreport", true, "output report file") ;
		if(!command.parse(args)) return ;
		command.printHelp() ;
		
		String db   = command.getOption("db", null) ;
		String oreport = command.getOption("oreport", null) ; 
		String incrReport = command.getOption("incrReport", null) ;
		int incrReportThreshold = 1000 ;
		if(incrReport != null) {
			incrReportThreshold = Integer.parseInt(incrReport) ;
		}
		Configuration conf = ConfigurationFactory.getConfiguration();
		Database database = Database.getDatabase(db, conf) ;
		StatisticsSet reporter = new StatisticsSet();
		StatisticsSet idPrefixReporter = new StatisticsSet();
		Database.Reader reader = database.getReader() ;
		Row row = null ;
		DocumentMapper mapper = new DocumentMapper() ;
		int count = 0;
		while((row = reader.next()) != null) {
			Document doc = mapper.fromRow(row) ;
			String id = doc.getId() ;
			String idPrefix = id.substring(0, id.indexOf(':')) ;
			reporter.incr("Document", "count", 1) ;
			reporter.incr("Tags", doc.getTags(), 1) ;
			reporter.incr("Labels", doc.getLabels(), 1) ;
			idPrefixReporter.incr("ID Prefix", idPrefix, 1) ;
			count++ ;
			if(count % incrReportThreshold == 0) {
				if(incrReport == null) System.out.print('.') ;
				else reporter.report(System.out) ;
			}
		}
		System.out.println();
		reader.close() ;
		reporter.report(System.out) ;
		idPrefixReporter.report(System.out) ;
		if(oreport != null) {
			PrintStream os = new PrintStream(new FileOutputStream(oreport)) ;
			reporter.report(os, "desc") ;
			idPrefixReporter.report(os, "desc") ;
			os.close() ;
		}
	}
}
