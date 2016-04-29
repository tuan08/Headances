package org.headvances.monitor;

import java.io.Serializable;
import java.util.LinkedList;
import java.util.List;
/**
 * $Author: Tuan Nguyen$ 
 **/
public class InvokeMonitor implements Serializable {
	private int    count ;
	private int    errorCount ;
	private long   totalExecTime ;
	
	private LinkedList<String> messages = new LinkedList<String>() ;
	private String description ;
	
	public InvokeMonitor() {} 
	
	public InvokeMonitor(String desc) {
		this.description = desc ;
	}
	
	public String getDescription() { return description; }
	public void setDescription(String description) {
  	this.description = description;
  }
	
	public int getCount() { return count; }
	public void setCount(int count) {
  	this.count = count;
  }
	public void incrCount() { count++ ; }
	
	public long getTotalExecTime() { return this.totalExecTime ; }
	public void setTotalExecTime(long time) { this.totalExecTime = time ; }
	public void addExecTime(long time) { this.totalExecTime += time ; }
	
	public long getAvgExecTime() { 
		if(count == 0) return  0 ;
		return totalExecTime/count ;
	}
	
	public int getErrorCount() { return errorCount; }
	public void setErrorCount(int errorCount) { this.errorCount = errorCount; }
	
	public void incrErrorCount() { errorCount++ ; }
	
	public List<String> getMessages() { return messages; }
	public void addMessage(String m) {
		messages.addFirst(m) ;
		if(messages.size() > 100) messages.removeLast() ;
	}
	
	public String toString() {
		StringBuilder b = new StringBuilder() ;
		b.append("Invoke Count:   ").append(count).append("\n") ;
		b.append("Invoke Error Count:   ").append(errorCount).append("\n") ;
		b.append("Total Exec Time:   ").append(totalExecTime).append("\n") ;
		b.append("Avg Exec Time:   ").append(getAvgExecTime()).append("\n") ;
		return b.toString() ;
	}
}