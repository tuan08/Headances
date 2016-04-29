package org.headvances.cluster.task;

import org.headvances.cluster.ClusterNodeInfo;
import org.headvances.cluster.MemberInfo;
import org.springframework.context.ApplicationContext;

import com.hazelcast.core.Cluster;
import com.hazelcast.core.Member;
/**
 * $Author: Tuan Nguyen$ 
 **/
public class GetMemberInfoTask extends ClusterTask<MemberInfo> {
  public MemberInfo call() throws Exception {
  	Cluster cluster = getHazelcastInstance().getCluster() ;
		Member member = cluster.getLocalMember() ;
		ApplicationContext context = getApplicationContext() ;
		ClusterNodeInfo info = context.getBean(ClusterNodeInfo.class) ;
		MemberInfo minfo = new MemberInfo(member, info) ;
		return minfo ;
  }
}
