package org.headvances.cluster.task;

import org.headvances.util.jvm.JVMInfo;
import org.springframework.context.ApplicationContext;

/**
 * $Author: Tuan Nguyen$ 
 **/
public class GetJVMInfoTask  extends ClusterTask<JVMInfo>  {
	public JVMInfo call() {
	  ApplicationContext ctx = getApplicationContext() ; ;
	  if(ctx != null){
	    JVMInfo info = new JVMInfo();
	    info.populate() ;
	    return info ;
	  }
	  return null ;
	}
}