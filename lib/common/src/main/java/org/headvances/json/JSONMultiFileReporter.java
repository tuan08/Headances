package org.headvances.json;

import java.io.PrintStream;

import org.headvances.util.statistic.StatisticsSet;

/**
 * $Author: Tuan Nguyen$ 
 **/
public class JSONMultiFileReporter<T> {
	private String location ;
	private Class<T> type ;
	private StatisticsSet map ;
	
	public JSONMultiFileReporter(String loc, Class<T> type) {
		this.location = loc ;
		this.type = type ;
		this.map = new StatisticsSet() ;
	}
	
	public void process() throws Exception {
		map.clear() ;
		JSONMultiFileReader reader = new JSONMultiFileReader(location) ;
		T object  = null ;
		while((object = reader.next(type)) != null) {
			log(object) ;
		}
		reader.close() ;
	}
	
	protected void log(T object) {
		map.incr("Statistic", "all", 1) ;
	}
	
	public void report(PrintStream out) {
		map.report(out);
	}
}
