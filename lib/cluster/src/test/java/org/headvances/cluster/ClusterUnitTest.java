package org.headvances.cluster;

import org.junit.Test;
import org.springframework.beans.factory.xml.XmlBeanDefinitionReader;
import org.springframework.context.support.GenericApplicationContext;
/**
 * Author : Tuan Nguyen
 *          tuan.nguyen@headvances.com
 * aug 21, 2012  
 */
public class ClusterUnitTest {

  @Test
  public void test() throws Exception { 
  	final GenericApplicationContext ctx = new GenericApplicationContext() ;
    XmlBeanDefinitionReader xmlReader = new XmlBeanDefinitionReader(ctx) ;
    String[] res = {
    	"classpath:/META-INF/cluster.xml",
    	"classpath:/META-INF/test.xml"
    } ;
    xmlReader.loadBeanDefinitions(res) ;
    ctx.refresh() ;
    
    ClusterNodeInfo info = ctx.getBean(ClusterNodeInfo.class) ;
    System.out.println("Description: " + info.getDescription());
    System.out.println("Role: " + info.getRole());
    ctx.registerShutdownHook() ;
  }
}