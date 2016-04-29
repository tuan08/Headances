package org.headvances.util.jvm;

import java.io.Serializable;
import java.lang.management.GarbageCollectorMXBean;
import java.lang.management.ManagementFactory;
import java.util.ArrayList;
import java.util.List;

public class JVMInfo implements Serializable{
  private MemoryInfo memoryInfo;
  private ArrayList<GarbageCollectorInfo> garbageCollectorInfo ;
  private AppInfo appInfo;
  
  public JVMInfo() {}
  
  public void populate(){
    memoryInfo = new MemoryInfo();
    
    List<GarbageCollectorMXBean> gcbeans = ManagementFactory.getGarbageCollectorMXBeans() ; 
    garbageCollectorInfo = new ArrayList<GarbageCollectorInfo>();
    for(int i = 0; i < gcbeans.size(); i++) {
      GarbageCollectorMXBean gcbean = gcbeans.get(i) ;
      garbageCollectorInfo.add(new GarbageCollectorInfo(gcbean));
    }
    
    appInfo = new AppInfo();
  }
  
  public MemoryInfo getMemoryInfo() { return memoryInfo;}

  public ArrayList<GarbageCollectorInfo> getGarbageCollectorInfo() { 
    return garbageCollectorInfo; 
  }

  public AppInfo getThreadInfo() { return appInfo; }
}