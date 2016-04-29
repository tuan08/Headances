package org.headvances.analysis.cluster.task;

import org.headvances.analysis.AnalysisService;
import org.headvances.cluster.task.ClusterTask;
import org.headvances.monitor.InvokeMonitor;
import org.springframework.context.ApplicationContext;
/**
 * $Author: Tuan Nguyen$ 
 **/
public class GetAnalysisServiceMonitorTask extends ClusterTask<InvokeMonitor> {
  public GetAnalysisServiceMonitorTask() {} 
  
	public InvokeMonitor call() throws Exception {
  	ApplicationContext ctx = getApplicationContext() ;
  	AnalysisService service = ctx.getBean(AnalysisService.class);
  	return service.getInvokeMonitor() ;
  }
}