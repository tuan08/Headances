package org.headvances.crawler.member;

import java.util.Collection;
import java.util.Iterator;
import java.util.Set;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.FutureTask;

import org.headvances.cluster.task.ClusterTask;
import org.headvances.cluster.task.PingTask;
import org.springframework.beans.factory.xml.XmlBeanDefinitionReader;
import org.springframework.context.support.GenericApplicationContext;

import com.hazelcast.core.DistributedTask;
import com.hazelcast.core.HazelcastInstance;
import com.hazelcast.core.Member;
import com.hazelcast.core.MultiTask;
/**
 * $Author: Tuan Nguyen$ 
 **/
public class VirtualMember {
	private String config ;
	private GenericApplicationContext ctx ;
	
	public VirtualMember(String config) {
		this.config = config ;
	}
	
	public void start() {
		ctx = new GenericApplicationContext() ;
    XmlBeanDefinitionReader xmlReader = new XmlBeanDefinitionReader(ctx) ;
    xmlReader.loadBeanDefinitions(config);
    ctx.refresh() ;
	}
	
	public void stop() {
		ctx.stop() ;
	}
	
	public void echo(String input) throws Exception {
		HazelcastInstance instance = (HazelcastInstance) ctx.getBean("instance") ;
		ExecutorService executorService = instance.getExecutorService("executorService");

		FutureTask<String> task = new DistributedTask<String>(new PingTask(input));
		executorService.execute(task);
		String echoResult = task.get();
		System.out.println("DistributedTask Result: " + echoResult);
		
		Set<Member> members = instance.getCluster().getMembers() ;
		MultiTask<String> mtask = new MultiTask<String>(new PingTask(input), members);
    executorService.execute(mtask);
    Collection<String> results = mtask.get();
    Iterator<String> i = results.iterator() ;
    while(i.hasNext()) {
    	System.out.println("MultiTask Result: " + i.next()) ;
    }
	}

	public <T> Collection<T> execute(ClusterTask<T> task) throws Exception {
		HazelcastInstance instance = (HazelcastInstance) ctx.getBean("instance") ;
		ExecutorService executorService = instance.getExecutorService("executorService");

		Set<Member> members = instance.getCluster().getMembers() ;
		MultiTask<T> mtask = new MultiTask<T>(task, members);
    executorService.execute(mtask);
    return mtask.get();
	}
}