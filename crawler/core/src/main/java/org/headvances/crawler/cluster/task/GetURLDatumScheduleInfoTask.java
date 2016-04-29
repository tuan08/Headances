package org.headvances.crawler.cluster.task;

import java.util.ArrayList;
import java.util.List;

import org.headvances.cluster.task.ClusterTask;
import org.headvances.crawler.fetch.URLDatumFetchScheduler;
import org.headvances.crawler.master.URLDatumScheduleInfo;
import org.springframework.context.ApplicationContext;
/**
 * $Author: Tuan Nguyen$ 
 **/
public class GetURLDatumScheduleInfoTask  extends ClusterTask<List<URLDatumScheduleInfo>>  {
	public List<URLDatumScheduleInfo> call() {
		ApplicationContext ctx = getApplicationContext() ; ;
		if(ctx == null) return new ArrayList<URLDatumScheduleInfo>() ;
		URLDatumFetchScheduler scheduler = 
			(URLDatumFetchScheduler)ctx.getBean(URLDatumFetchScheduler.class) ;
		if(scheduler == null) return new ArrayList<URLDatumScheduleInfo>() ;
		return scheduler.getURLDatumScheduleInfos()  ;
	}
}