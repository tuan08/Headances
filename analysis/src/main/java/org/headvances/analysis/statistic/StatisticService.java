package org.headvances.analysis.statistic;

import org.headvances.data.Document;

/**
 * $Author: Tuan Nguyen$ 
 * 
 **/
public class StatisticService {
	private DocumentTagStatistic hdStatistic = new DocumentTagStatistic() ;
	
	public StatisticService() {
//	  System.out.println("Create StatisticService!!!!!!!!!!!!!!!!!!!!!");
	}
	
	public void log(Document doc, Throwable error) {
		hdStatistic.log(doc, error) ;
	}
	
	public DocumentTagStatistic getHdStatistic(){
	  return this.hdStatistic;
	}
}
