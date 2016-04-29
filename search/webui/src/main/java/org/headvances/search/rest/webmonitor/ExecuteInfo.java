package org.headvances.search.rest.webmonitor;

import org.headvances.util.text.DateUtil;
import org.headvances.util.text.NumberFormatter;
/**
 * $Author: Tuan Nguyen$ 
 **/
public class ExecuteInfo {
	private long   startTime; 
	private long   finishTime ;
	private long   elapsedTime ;
	private int    processedRecord ;
	private int    totalRecord ;
	private String message ;
	
	public String getStartTime() { 
		return DateUtil.asCompactDateTime(this.startTime) ;
	}
	public void setStartTime(long startTime) { this.startTime = startTime; }
	
	public String getElapsedTime() { 
		return NumberFormatter.milliTimeAsHumanReadable(this.elapsedTime) ; 
	}
	public void setElapsedTime(long currentTime) {
		this.elapsedTime = currentTime - this.startTime ;
	}
	
	public String getFinishTime() { 
		if(finishTime < 1) return "" ;
		return DateUtil.asCompactDateTime(this.finishTime) ;
	}
	public void setFinishTime(long finishTime) { 
		setElapsedTime(finishTime) ;
		this.finishTime = finishTime; 
	}
	
	public int getProcessedRecord() { return processedRecord; }
	public void setProcessedRecord(int processedRecord) { 
		this.processedRecord = processedRecord; 
  }
	public void addProcessedRecord(int processedRecord) { 
		this.processedRecord += processedRecord; 
  }
	
	public int getTotalRecord() { return totalRecord; }
	public void setTotalRecord(int totalRecord) { this.totalRecord = totalRecord; }
}
