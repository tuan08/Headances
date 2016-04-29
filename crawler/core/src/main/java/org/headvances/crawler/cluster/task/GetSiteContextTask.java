package org.headvances.crawler.cluster.task;

import java.util.List;

import org.headvances.cluster.task.ClusterTask;
import org.headvances.xhtml.site.SiteContext;
import org.headvances.xhtml.site.SiteContextManager;
import org.springframework.context.ApplicationContext;
/**
 * $Author: Tuan Nguyen$ 
 **/
public class GetSiteContextTask  extends ClusterTask<List<SiteContext>>  {
	public List<SiteContext> call() {
		ApplicationContext ctx = getApplicationContext() ;
		if(ctx != null){
		SiteContextManager manager = ctx.getBean(SiteContextManager.class) ;
		if(manager != null) 
		    return manager.getSiteConfigContextList() ;
		}
		return null;
	}
}