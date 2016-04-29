package org.headvances.cluster;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.FutureTask;

import org.headvances.cluster.task.ClusterTask;
import org.headvances.cluster.task.GetMemberInfoTask;

import com.hazelcast.client.ClientConfig;
import com.hazelcast.client.HazelcastClient;
import com.hazelcast.core.DistributedTask;
import com.hazelcast.core.Member;
import com.hazelcast.core.MultiTask;
/**
 * $Author: Tuan Nguyen$ 
 **/
public class ClusterClient {
	private HazelcastClient hazelcast ;
	private Map<String, Set<Member>> roleMap = new HashMap<String, Set<Member>>() ;
	private List<ClusterMember> members = new ArrayList<ClusterMember>() ;
	
	public ClusterClient() { }
	
	public ClusterClient(String username, String password, String ... connectUrl) throws Exception {
		connect(username, password, connectUrl) ;
	}
	
	public void connect(String groupname, String password, String ... connectUrl) throws Exception {
		ClientConfig config = new ClientConfig() ;
		config.getGroupConfig().setName(groupname).setPassword(password);
		config.addAddress(connectUrl);
		hazelcast = HazelcastClient.newHazelcastClient(config);
		updateMemberRoleMap() ;
	}
	
	public Set<Member> getMembers(String role) { return roleMap.get(role) ; }
	
	public List<ClusterMember> getClusterMembers() { return this.members ; }
	
	public Member getMember(String host, int port) {
		for(ClusterMember sel : members) {
			if(sel.getHost().equals(host) && port == sel.getPort()) return sel.getMember() ;
		}
		return null ;
	}
	
	public <T> T execute(ClusterTask<T> task, Member member) throws Exception {
		ExecutorService executorService = hazelcast.getExecutorService("executorService");
		FutureTask<T> dtask = new DistributedTask<T>(task, member);
		executorService.execute(dtask);
		T result = dtask.get();
		return result ;
	}
	
	public <T> Collection<T> execute(ClusterTask<T> task) throws Exception {
		ExecutorService executorService = hazelcast.getExecutorService("executorService");
		Set<Member> members = hazelcast.getCluster().getMembers() ;
		MultiTask<T> mtask = new MultiTask<T>(task, members);
    executorService.execute(mtask);
    return mtask.get();
	}
	
	public <T> Collection<T> execute(ClusterTask<T> task, String memberRole) throws Exception {
		Set<Member> members = roleMap.get(memberRole) ;
		ExecutorService executorService = hazelcast.getExecutorService("executorService");
		MultiTask<T> mtask = new MultiTask<T>(task, members);
    executorService.execute(mtask);
    return mtask.get();
	}
	
	public void updateMemberRoleMap() throws Exception {
		roleMap.clear() ;
		members.clear() ;
		GetMemberInfoTask task = new GetMemberInfoTask() ;
		Collection<MemberInfo> col = execute(task) ;
		for(MemberInfo mrole : col) {
			Set<String> role = mrole.getRole() ;
			for(String selRole : role) {
				ClusterMember cmember = new ClusterMember(mrole.getMember()) ;
				cmember.setRoles(mrole.getRole()) ;
				members.add(cmember) ;
				
				Set<Member> memberSet = roleMap.get(selRole) ;
				if(memberSet == null) {
					memberSet = new HashSet<Member>() ;
					roleMap.put(selRole, memberSet) ;
				}
				memberSet.add(mrole.getMember()) ;
			}
		}
	}
	
}