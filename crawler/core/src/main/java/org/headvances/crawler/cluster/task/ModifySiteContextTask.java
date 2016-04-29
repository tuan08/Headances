package org.headvances.crawler.cluster.task;

import java.io.File;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;

import org.headvances.cluster.task.ClusterTask;
import org.headvances.cluster.task.TaskResult;
import org.headvances.json.JSONReader;
import org.headvances.xhtml.site.SiteConfig;
import org.headvances.xhtml.site.SiteContext;
import org.headvances.xhtml.site.SiteContextManager;
import org.headvances.xhtml.site.SiteContext.Modify;
import org.springframework.beans.factory.NoSuchBeanDefinitionException;
import org.springframework.context.ApplicationContext;
/**
 * $Author: Tuan Nguyen$ 
 **/
public class ModifySiteContextTask  extends ClusterTask<TaskResult>  {
	private List<SiteContext> contexts = new ArrayList<SiteContext>() ;
	
	public void add(String site, String injectUrl, int crawlDeep, int maxCon, String status) {
  	SiteConfig config = new SiteConfig() ;
  	config.setHostname(site) ;
  	config.setInjectUrl(new String[] {injectUrl}) ;
  	config.setCrawlDeep(crawlDeep) ;
  	config.setMaxConnection(maxCon) ;
  	config.setStatus(status) ;
  	config.setRefreshPeriod(60 * 60 * 24) ;
  	add(config) ;
	}
	
	public void add(SiteConfig config) { 
		SiteContext context = new SiteContext(config) ;
		context.setModify(Modify.ADD) ;
		contexts.add(context) ; 
  }

	public void add(SiteContext context) { 
		contexts.add(context) ; 
  }
	
	public void add(List<SiteContext> context) { 
	  for(SiteContext sel : context) contexts.add(sel) ; 
  }
	
	public void add(InputStream is) throws Exception {
		JSONReader reader = new JSONReader(is) ;
		List<SiteConfig> configs = reader.readAll(SiteConfig.class) ;
		for(int i = 0; i < configs.size(); i++) {
			add(configs.get(i)) ;
		}
	}
	
	public void add(String file) throws Exception {
		JSONReader reader = new JSONReader(file) ;
		List<SiteConfig> configs = reader.readAll(SiteConfig.class) ;
		for(int i = 0; i < configs.size(); i++) {
			add(configs.get(i)) ;
		}
	}
	
	public void add(File file) throws Exception {
		JSONReader reader = new JSONReader(file.getCanonicalPath()) ;
		List<SiteConfig> configs = reader.readAll(SiteConfig.class) ;
		for(int i = 0; i < configs.size(); i++) {
			add(configs.get(i)) ;
		}
	}
	
	public TaskResult call() {
		TaskResult result = new TaskResult(this) ;
		ApplicationContext ctx = getApplicationContext()  ;
		try {
			SiteContextManager manager = ctx.getBean(SiteContextManager.class) ;
			if(manager != null) {
				manager.modify(contexts) ;
				result.addMessage("Add " + contexts.size() + " SiteConfig for member " + getMember(ctx)) ;
			}
		} catch(NoSuchBeanDefinitionException ex) {
		}
		return result ;
	}
}