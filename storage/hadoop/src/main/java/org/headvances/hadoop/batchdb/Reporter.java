package org.headvances.hadoop.batchdb;

import org.headvances.util.statistic.StatisticsSet;

public interface Reporter {
	static enum BatchDB { WRITE, COMPACT }

	public void progress() ;
	public void increment(String name, int amount) ;

	static public class LocalReporter implements Reporter {
		private String       name ;
		private StatisticsSet map = new StatisticsSet() ;

		public LocalReporter(String name) {
			this.name = name ;
		}
		
		public void progress() { }

		public void increment(String name, int amount) {
			map.incr(this.name, name, amount) ;
		}

		public StatisticsSet getStatisticMap() { return map ; }
	}
}