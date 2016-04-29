package org.headvances.analysis;

import org.headvances.util.text.StringUtil;
import org.springframework.beans.factory.xml.XmlBeanDefinitionReader;
import org.springframework.context.ApplicationContext;
import org.springframework.context.support.GenericApplicationContext;

public class AnalysisServer {
	static AnalysisServer instance ;
	static ApplicationContext applicationContext ;
	
	private String[] role = { "analysis" } ;
	
	public void onInit() {
	}

	public String[] getRole() { return this.role ; }
	public void setRole(String[] role) { this.role = role ;}
	
	public void setRoles(String roles) {
		this.role = StringUtil.toStringArray(roles) ;
	}
	
	static public ApplicationContext getApplicationContext() { return applicationContext ; }
  static public void setApplicationContext(ApplicationContext context) {
  	applicationContext = context ;
  }
  
	static public AnalysisServer getInstance() { return instance ; }
	static public void setInstance(AnalysisServer ainstance) {
		instance = ainstance ;
	}
	
	static public void run() throws Exception {
		final GenericApplicationContext ctx = new GenericApplicationContext() ;
		XmlBeanDefinitionReader xmlReader = new XmlBeanDefinitionReader(ctx) ;
		String[] res = {
			"classpath:/META-INF/analysis-server.xml",
			"classpath:/META-INF/connection-factory-activemq.xml",
			"classpath:/META-INF/analysis-integration.xml",
			"classpath:/META-INF/cluster.xml"
		} ;
		xmlReader.loadBeanDefinitions(res) ;
		ctx.refresh() ;
		ctx.registerShutdownHook() ;
		
		setInstance(instance) ;
		setApplicationContext(ctx) ;
	}

	static public void main(String[] args) throws Exception {
		run() ;
		Thread.currentThread().join() ;
	}
}