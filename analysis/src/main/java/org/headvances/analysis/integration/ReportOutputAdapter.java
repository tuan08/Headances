package org.headvances.analysis.integration;

import org.headvances.data.Document;
import org.headvances.util.TimeReporter;
import org.headvances.util.statistic.StatisticsSet;
/**
 * $Author: Tuan Nguyen$ 
 **/
public class ReportOutputAdapter  {
	private boolean enable ;
	private StatisticsSet reporter ;
	private TimeReporter timeReporter ;
	private TimeReporter.Time totalTime ;
	private TimeReporter.Time lastRound ;
	private int count = 0;
	
	public ReportOutputAdapter() {
		reporter = new StatisticsSet();
    timeReporter = new TimeReporter();
    totalTime = timeReporter.getTime("Total time");
    totalTime.start();
    lastRound = timeReporter.getTime("Last Round");
    lastRound.start();
	}
	
	public void setEnable(boolean b ) {
		enable = b ;
	}
	
	synchronized public void write(Document doc) throws Exception {
		if(!enable) return ;
		reporter.incr("Document", "count", 1) ;
		String[] labels = doc.getLabels() ;
		if(labels != null) {
			reporter.incr("Document Labels", "all", 1) ;
			reporter.incr("Document Labels", labels, "all", 1) ;
		}
		String[] tags = doc.getTags() ;
		if(tags != null) {
			reporter.incr("Document Tags", "all", 1) ;
			reporter.incr("Document Tags", tags, "all", 1) ;
		}
		count++ ;
		if(count % 5000 == 0) {
			lastRound.stop();
      reporter.report(System.out) ;
      timeReporter.report(System.out);
      lastRound.start();
		}
	}
	
	public void onDestroy() {
		totalTime.stop();
		lastRound.stop();
		reporter.report(System.out) ;
		reporter.report(System.out) ;
		timeReporter.report(System.out);
	}
}