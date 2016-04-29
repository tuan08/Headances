package org.headvances.crawler.cluster.task;

import org.headvances.cluster.task.ClusterTask;
import org.headvances.cluster.task.TaskResult;
import org.headvances.crawler.master.CrawlerMaster;
import org.springframework.context.ApplicationContext;
/**
 * $Author: Tuan Nguyen$ 
 **/
public class InjectURLTask  extends ClusterTask<TaskResult>  {
	public TaskResult call() {
		TaskResult result = new TaskResult(this) ;
		ApplicationContext ctx = CrawlerMaster.getApplicationContext() ;
		if(ctx != null) {
			try {
				CrawlerMaster master = ctx.getBean(CrawlerMaster.class) ;
				master.getURLDatumFetchScheduler().injectURL() ;
				result.addMessage("Inject urls into the URLDatumDB") ;
			} catch(Exception ex) {
				ex.printStackTrace() ;
				result.addMessage("Inject urls into the URLDatumDB fail !!!!!!!!!!!!") ;
			}
		}
		return result ;
	}
}