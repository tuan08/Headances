package org.headvances.analysis.statistic;

import java.io.Serializable;

import org.headvances.data.Document;
import org.headvances.nlp.ml.classify.Predict;
import org.headvances.util.statistic.StatisticsSet;
/**
 * $Author: Tuan Nguyen$ 
 **/
public class ContentTypeStatistic implements StatisticInfo, Serializable {
  final static public String SUMMARY_CATEGORY = "Total Summary" ;
  final static public String LABEL_CATEGORY   = "Label Summary" ;
  
	private StatisticsSet statistic = new StatisticsSet() ;
	
	public StatisticsSet getStatisticMap(){ return statistic ; }
	
	public void log(Document doc, Predict predict) {
	  statistic.incr(SUMMARY_CATEGORY, "All", 1) ;
	  if(predict.probability > 0.85) {
	    statistic.incr(SUMMARY_CATEGORY, "> 0.85", "All", 1) ;
	  }
	  if(predict.probability > 0.70) {
	  	statistic.incr(SUMMARY_CATEGORY, "> 0.70", "All", 1) ;
	  } 
	  if(predict.probability > 0.55) {
	  	statistic.incr(SUMMARY_CATEGORY, "> 0.55", "All", 1) ;
	  } 
	  if(predict.probability > 0.40) {
	  	statistic.incr(SUMMARY_CATEGORY, "> 0.40", "All", 1) ;
	  } 
	  if(predict.probability > 0.25) {
	  	statistic.incr(SUMMARY_CATEGORY, "> 0.25", "All", 1) ;
	  }
	  
	  statistic.incr(LABEL_CATEGORY, "All", 1) ;
	  statistic.incr(LABEL_CATEGORY, predict.label, "All", 1) ;
	}
	
	public String toString() {
		StringBuilder b = new StringBuilder() ;
		statistic.report(b) ;
		return b.toString() ;
	}
}