package org.headvances.crawler.cluster.task;

import java.util.ArrayList;
import java.util.List;

import org.headvances.cluster.task.ClusterTask;
import org.headvances.crawler.fetch.URLDatumFetchScheduler;
import org.headvances.crawler.master.URLDatumCommitInfo;
import org.springframework.context.ApplicationContext;
/**
 * $Author: Tuan Nguyen$ 
 **/
public class GetURLDatumCommitInfoTask  extends ClusterTask<List<URLDatumCommitInfo>>  {
	public List<URLDatumCommitInfo> call() {
		ApplicationContext ctx = getApplicationContext() ; ;
		if(ctx == null) return new ArrayList<URLDatumCommitInfo>() ;
		URLDatumFetchScheduler scheduler = 
			(URLDatumFetchScheduler)ctx.getBean(URLDatumFetchScheduler.class) ;
		if(scheduler == null) return new ArrayList<URLDatumCommitInfo>() ;
		return scheduler.getURLDatumCommitInfos()  ;
	}
}