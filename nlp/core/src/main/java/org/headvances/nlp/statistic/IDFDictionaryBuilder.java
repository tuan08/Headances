package org.headvances.nlp.statistic;

import java.util.Iterator;

import org.headvances.util.statistic.Statistic;
import org.headvances.util.statistic.Statistics;
import org.headvances.util.statistic.StatisticsSet;
/**
 * $Author: Tuan Nguyen$ 
 **/
public class IDFDictionaryBuilder {
	private StatisticsSet statisticsSet = new StatisticsSet() ;
	private int documentCount ;

	public void collect(Feature[] feature) {
		documentCount++ ;
		for(Feature sel : feature) {
			statisticsSet.incr("Document", sel.getFeature(), 1) ;
			statisticsSet.incr("Terms",    sel.getFeature(), sel.getFrequency()) ;
		}
	}

	public IDFDictionary getIDFDictionary() {
		IDFDictionary dict = new IDFDictionary() ;
		dict.setDocumentCount(documentCount) ;
		Statistics docStatistics = statisticsSet.getStatistics("Document") ;
		Iterator<Statistic> i = docStatistics.values().iterator() ;
		while(i.hasNext()) {
			Statistic docStats = i.next() ;
			dict.add(docStats.getName(), (int)docStats.getFrequency()) ;
		}
		return dict ;
	}
}
