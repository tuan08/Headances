package org.headvances.cluster.task;


/**
 * $Author: Tuan Nguyen$ 
 **/
public class ExitTask  extends ClusterTask<TaskResult>  {
	public TaskResult call() {
		TaskResult result = new TaskResult(this) ;
		return result ;
	}
}