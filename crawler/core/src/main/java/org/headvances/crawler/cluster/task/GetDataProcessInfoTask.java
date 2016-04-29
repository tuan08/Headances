package org.headvances.crawler.cluster.task;

import org.headvances.cluster.task.ClusterTask;
import org.headvances.crawler.process.DataProcessInfo;
import org.headvances.crawler.process.FetchDataProcessor;
import org.springframework.context.ApplicationContext;
/**
 * $Author: Tuan Nguyen$ 
 **/
public class GetDataProcessInfoTask  extends ClusterTask<DataProcessInfo>  {
	public DataProcessInfo call() {
		ApplicationContext ctx = getApplicationContext() ; ;
		if(ctx == null) return new DataProcessInfo() ;
		FetchDataProcessor scheduler = (FetchDataProcessor)ctx.getBean(FetchDataProcessor.class) ;
		if(scheduler == null) return new DataProcessInfo() ;
		return scheduler.getDataProcessInfo()  ;
	}
}