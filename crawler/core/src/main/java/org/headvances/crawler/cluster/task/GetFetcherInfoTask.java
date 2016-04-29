package org.headvances.crawler.cluster.task;

import java.util.ArrayList;
import java.util.List;

import org.headvances.cluster.task.ClusterTask;
import org.headvances.crawler.fetch.http.FetcherInfo;
import org.headvances.crawler.fetch.http.HttpFetcherManager;
import org.springframework.context.ApplicationContext;
/**
 * $Author: Tuan Nguyen$ 
 **/
public class GetFetcherInfoTask  extends ClusterTask<List<FetcherInfo>>  {
	public List<FetcherInfo> call() {
		ApplicationContext ctx = getApplicationContext() ; ;
		if(ctx == null) return new ArrayList<FetcherInfo>() ;
		HttpFetcherManager manager = (HttpFetcherManager)ctx.getBean(HttpFetcherManager.class) ;
		if(manager == null) return new ArrayList<FetcherInfo>() ;
		return manager.getFetcherInfo()  ;
	}
}