package org.headvances.crawler.cluster.task;

import org.headvances.cluster.task.ClusterTask;
import org.headvances.jms.queue.QueueInfo;
import org.headvances.jms.queue.QueueMonitor;
import org.springframework.context.ApplicationContext;
/**
 * $Author: Tuan Nguyen$ 
 **/
public class GetQueueInfoTask extends ClusterTask<QueueInfo> {
  private String schedulerId ;
  private String listenerId ;
  
  public GetQueueInfoTask() {} 
  
  public GetQueueInfoTask(String schedulerId, String listenerId) {
  	this.schedulerId = schedulerId ;
  	this.listenerId = listenerId ;
  }
  
	public QueueInfo call() throws Exception {
  	QueueInfo info = new QueueInfo() ;
  	ApplicationContext ctx = getApplicationContext() ;
  	if(ctx.containsBean(schedulerId)) {
  		QueueMonitor monitor = (QueueMonitor) ctx.getBean(schedulerId) ;
  		info.setEnqueueInfo(monitor.getQueueCallInfo()) ;
  	}
  	if(ctx.containsBean(listenerId)) {
  		QueueMonitor monitor = (QueueMonitor) ctx.getBean(listenerId) ;
  		info.setDequeueInfo(monitor.getQueueCallInfo()) ;
  	}
  	return info ;
  }
}
