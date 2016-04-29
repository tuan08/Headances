package org.headvances.cluster.task;

import java.io.Serializable;
import java.util.concurrent.Callable;

import org.springframework.beans.BeansException;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;

import com.hazelcast.core.Cluster;
import com.hazelcast.core.HazelcastInstance;
import com.hazelcast.core.HazelcastInstanceAwareObject;
import com.hazelcast.core.Member;
import com.hazelcast.spring.context.SpringAware;
/**
 * $Author: Tuan Nguyen$ 
 **/ 
@SpringAware 
abstract public class ClusterTask<T>  extends HazelcastInstanceAwareObject implements ApplicationContextAware, Serializable, Callable<T> {
	private transient ApplicationContext context;
	
	public ApplicationContext getApplicationContext() { return context ; }
	public void setApplicationContext(final ApplicationContext applicationContext) throws BeansException {
     context = applicationContext;
  }
	
	public boolean isMemberOf(ApplicationContext ctx) {
		if(ctx == null) return false ;
		HazelcastInstance instance = (HazelcastInstance) ctx.getBean(HazelcastInstance.class) ;
		if(instance == null) return  false ;
		Cluster cluster = getHazelcastInstance().getCluster() ;
		Member m1 = cluster.getLocalMember() ;
		Member m2 = instance.getCluster().getLocalMember() ;
		return m1.equals(m2) ;
	}
	
	protected Member getMember(ApplicationContext ctx) {
		HazelcastInstance instance = (HazelcastInstance) ctx.getBean(HazelcastInstance.class) ;
		if(instance == null) return  null ;
		Cluster cluster = getHazelcastInstance().getCluster() ;
		return cluster.getLocalMember() ;
	}
}