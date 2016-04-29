package org.headvances.crawler.integration;

import java.util.HashMap;
import java.util.LinkedList;
import java.util.Map;

import org.headvances.data.Document;
import org.headvances.data.HtmlDocumentUtil;
import org.springframework.beans.factory.xml.XmlBeanDefinitionReader;
import org.springframework.context.ApplicationContext;
import org.springframework.context.support.GenericApplicationContext;
/**
 * $Author: Tuan Nguyen$ 
 **/
public class DocumentConsumerLogger {
  private static ApplicationContext applicationContext ;
  
  private int maxLogPerSite =  3000 ;
  private Map<String, LinkedList<DocumentLog>> logHolder = new HashMap<String, LinkedList<DocumentLog>>() ;
  
  public void consume(Document doc) throws Exception {
    String id  = doc.getId() ;
    String site = id.substring(0, id.indexOf(":")) ;
    LinkedList<DocumentLog> siteLogHolder = logHolder.get(site) ;
    if(siteLogHolder == null) {
      siteLogHolder = new LinkedList<DocumentLog>() ;
      logHolder.put(site, siteLogHolder) ;
    }
    siteLogHolder.add(new DocumentLog(doc)) ;
    if(siteLogHolder.size() > maxLogPerSite) {
      siteLogHolder.removeFirst() ;
    }
  }
  
  public Map<String, LinkedList<DocumentLog>> getLogs() {
    return this.logHolder ; 
  }

  static public class DocumentLog {
    private String url ;
    private int    responseCode ;
    private long   contentLength ;
    
    public String getUrl() { return url ; }
    
    public long getContentLength()  { return contentLength ; }
    
    public int getResponseCode() { return responseCode ; }
    
    public DocumentLog(Document doc) {
      url = HtmlDocumentUtil.getHtmlLink(doc).get("url") ;
      responseCode = doc.getEntity(HtmlDocumentUtil.HTTP_RESPONSE_HEADER).getInteger("response-code") ;
      contentLength = (long)doc.getContent().length();
    }
  }
  
  static public ApplicationContext getApplicationContext() { return applicationContext ; }
  static public void setApplicationContext(ApplicationContext context) {
    applicationContext = context ;
  }

  static public void run() throws Exception {
    final GenericApplicationContext ctx = new GenericApplicationContext() ;
    XmlBeanDefinitionReader xmlReader = new XmlBeanDefinitionReader(ctx) ;
    String[] res = {
        "classpath:/META-INF/connection-factory-activemq.xml",
        "classpath:/META-INF/crawler-integration-logger.xml"
    } ;
    xmlReader.loadBeanDefinitions(res) ;
    ctx.refresh() ;
    ctx.registerShutdownHook() ;
    setApplicationContext(ctx) ;
  }

  static public void main(String[] args) throws Exception {
    run() ;
    Thread.currentThread().join() ;
  }
}