package org.headvances.crawler.integration;

import org.headvances.data.Document;
import org.springframework.beans.factory.xml.XmlBeanDefinitionReader;
import org.springframework.context.ApplicationContext;
import org.springframework.context.support.GenericApplicationContext;
/**
 * Author : Tuan Nguyen
 *          tuan.nguyen@headvances.com
 * Apr 21, 2010  
 */
public class DocumentConsumer  {
  private static ApplicationContext applicationContext ;

  private String storeDir ;

  public String getStoreDir() { return this.storeDir ; }
  public void   setStoreDir(String dir) { this.storeDir = dir ; }

  public void onInit() throws Exception {
  }

  public void onDestroy() throws Exception {
  }

  public void consume(Document doc) throws Exception {
    String tname = Thread.currentThread().getName() ;
    System.out.println("Consume[" + tname + "]: " + doc.getId());
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
        "classpath:/META-INF/crawler-document-consumer.xml"
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