package org.headvances.analysis.statistic;

import java.io.Serializable;

import org.headvances.data.Document;
import org.headvances.util.statistic.StatisticsSet;

/**
 * $Author: Tuan Nguyen$ 
 **/
public class DocumentTagStatistic implements StatisticInfo, Serializable {
  final static public String SUMMARY_CATEGORY = "Total summary" ;
  final static public String LABEL_CATEGORY = "Label summary" ;
  
	private StatisticsSet statistic = new StatisticsSet() ;
	
	public StatisticsSet getStatisticMap(){ return statistic ; }
	
	public void log(Document doc, Throwable error) {
	  statistic.incr(SUMMARY_CATEGORY, "All", 1);
		if(error != null) statistic.incr(SUMMARY_CATEGORY, "Error", "All", 1) ;
		else  statistic.incr(SUMMARY_CATEGORY, "Pass", "All", 1);

		statistic.incr(LABEL_CATEGORY, "All", 1);
		for(String selTag : doc.getTags()) {
		  statistic.incr(LABEL_CATEGORY, selTag, "All", 1);
		}
	}
	
}
