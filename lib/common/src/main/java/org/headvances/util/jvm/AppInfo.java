package org.headvances.util.jvm;

import java.io.Serializable;
import java.lang.management.ManagementFactory;
import java.lang.management.ThreadInfo;
import java.lang.management.ThreadMXBean;
import java.util.ArrayList;

public class AppInfo implements Serializable {
  private String threadStartedCount;
  private String threadPeakCount;
  private String threadCount;
  private String threadDeamonCount;
  
  private ArrayList<AppThreadInfo> threadinfos;

  public AppInfo() {
    ThreadMXBean mbean = ManagementFactory.getThreadMXBean();
    threadinfos = new ArrayList<AppThreadInfo>();
    
    threadStartedCount = Long.toString(mbean.getTotalStartedThreadCount());
    threadPeakCount = Long.toString(mbean.getPeakThreadCount());
    threadCount = Long.toString(mbean.getThreadCount());
    threadDeamonCount = Long.toString(mbean.getDaemonThreadCount());
    
    long[] tid = mbean.getAllThreadIds() ;
    for(int i = 0; i < tid.length; i++) {
      ThreadInfo tInfo = mbean.getThreadInfo(tid[i], 10) ;
      if(tInfo == null) continue ;
      AppThreadInfo apptInfo = new AppThreadInfo(tInfo);
      apptInfo.setThreadCPUTime(Long.toString(mbean.getThreadCpuTime(tid[i])));
      apptInfo.setThreadUserTime(Long.toString(mbean.getThreadUserTime(tid[i])));
      threadinfos.add(apptInfo);
    }
  }
  
  public ArrayList<AppThreadInfo> getAllThreadInfo(){ return threadinfos; }
 
  public String getThreadStartedCount() { return threadStartedCount; }
  public void setThreadStartedCount(String threadStartedCount) { this.threadStartedCount = threadStartedCount; }
  public String getThreadPeakCount() { return threadPeakCount;}
  public void setThreadPeakCount(String threadPeakCount) { this.threadPeakCount = threadPeakCount;}

  public String getThreadCount() { return threadCount; }
  public void setThreadCount(String threadCount) { this.threadCount = threadCount;}
  
  public String getThreadDeamonCount() { return threadDeamonCount; }
  public void setThreadDeamonCount(String threadDeamonCount) { this.threadDeamonCount = threadDeamonCount; }
  
}
