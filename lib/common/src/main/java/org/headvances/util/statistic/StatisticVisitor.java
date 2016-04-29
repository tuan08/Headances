package org.headvances.util.statistic;

/**
 * $Author: Tuan Nguyen$ 
 **/
public interface StatisticVisitor {
	public void onVisit(Statistics statistics, Statistic statistic) ;
}
