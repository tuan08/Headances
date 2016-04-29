package org.headvances.analysis;

import org.headvances.analysis.statistic.DocumentTagStatistic;
import org.headvances.data.Document;
import org.headvances.data.HtmlDocumentUtil;
import org.headvances.html.dom.TDocument;
import org.headvances.monitor.InvokeMonitor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.xml.XmlBeanDefinitionReader;
import org.springframework.context.support.GenericApplicationContext;
import org.springframework.jmx.export.annotation.ManagedAttribute;
import org.springframework.jmx.export.annotation.ManagedOperation;
import org.springframework.jmx.export.annotation.ManagedResource;

/**
 * @author Tuan Nguyen
 */
@ManagedResource(
	objectName="org.headvances.analysis:name=AnalysisService", 
	description="This service is responsible for classifying the webpage type, extract main content"
)
public class AnalysisService {
	private static final Logger logger = LoggerFactory.getLogger(AnalysisService.class);
	
	@Autowired 
	private DocumentTagStatistic hdStatistic;
	@Autowired
	private AnalysisHistory histories ;
	private Analyzer[]      analyzers  ;
	
	private InvokeMonitor invokeMonitor = new InvokeMonitor("Monitor the analysis call") ;
	
	public InvokeMonitor getInvokeMonitor() { return this.invokeMonitor ; }
	
	public void setAnalyzers(Analyzer[] analyzers) { this.analyzers = analyzers ; }
	
	@ManagedAttribute(
	  description="The frequency that the servervice has bean called or frequency of the document."
	)
	public int getInvokeCount() { return this.invokeMonitor.getCount() ; }
	
	@ManagedAttribute(
		description="The avg time to ananlyse a document."
	)
	public long getInvokeAvgTime() { return this.invokeMonitor.getAvgExecTime() ; }
	
	@ManagedOperation(description="Analysis Service invoke monitor")
  public String formatInvokeMonitor() { return invokeMonitor.toString() ;  }
	
	public Document analyze(Document hdoc) {
		long start = System.currentTimeMillis() ;
		invokeMonitor.incrCount() ;
		histories.add(hdoc) ;
		Throwable error = null;
		try {
			hdoc.setTags(null) ;
			String anchorText = HtmlDocumentUtil.getHtmlLink(hdoc).getString("anchorText");
			String url = HtmlDocumentUtil.getHtmlLink(hdoc).getString("url");
			TDocument tdoc = new TDocument(anchorText, url, hdoc.getContent()) ;
			for(int i = 0; i < analyzers.length; i++) {
				analyzers[i].analyze(hdoc, tdoc) ;
			}
		} catch (Exception ex) {
			invokeMonitor.incrErrorCount() ;
			invokeMonitor.addMessage(ex.getMessage()) ;
			logger.error("Cannot process " + HtmlDocumentUtil.getHtmlLink(hdoc).getString("url"), ex) ;
			error = ex;
		} finally{
		  hdStatistic.log(hdoc, error);
		  invokeMonitor.addExecTime(System.currentTimeMillis() - start) ;
		}
		return hdoc ;
	}
	
	public static void main(String[] args) throws Exception {
		final GenericApplicationContext context = new GenericApplicationContext() ;
		XmlBeanDefinitionReader xmlReader = new XmlBeanDefinitionReader(context) ;
		String[] res = {
			"classpath:/META-INF/analysis-server.xml",
		} ;
		xmlReader.loadBeanDefinitions(res) ;
		context.refresh() ;
		context.registerShutdownHook() ;
	}
}