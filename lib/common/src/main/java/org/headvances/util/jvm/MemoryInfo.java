package org.headvances.util.jvm;

import java.io.Serializable;
import java.lang.management.ManagementFactory;
import java.lang.management.MemoryMXBean;
import java.lang.management.MemoryUsage;

import org.headvances.util.text.NumberFormatter;

public class MemoryInfo implements Serializable{
  private String init ;
  private String use ;
  private String committed ;
  private String max ;
  
  public MemoryInfo() {
    MemoryMXBean mbean = ManagementFactory.getMemoryMXBean();
    MemoryUsage memory = mbean.getHeapMemoryUsage() ;
    
    init = NumberFormatter.byteFormatAsHumanReadable(memory.getInit()) ;
    max = NumberFormatter.byteFormatAsHumanReadable(memory.getMax()) ;
    use = NumberFormatter.byteFormatAsHumanReadable(memory.getUsed()) ;
    committed = NumberFormatter.byteFormatAsHumanReadable(memory.getCommitted()) ;
  }

  public String getInit() { return init; }
  public void setInit(String init) { this.init = init; }

  public String getUse() { return use ; }
  public void setUse(String use) { this.use = use; }

  public String getCommitted() { return committed ;}
  public void setCommitted(String committed) { this.committed = committed; }

  public String getMax() { return max; }
  public void setMax(String max) { this.max = max; }
}
