package org.headvances.analysis.cluster.task;

import java.util.ArrayList;
import java.util.List;

import org.headvances.analysis.integration.AnalysisInputGateway;
import org.headvances.cluster.task.ClusterTask;
import org.headvances.cluster.task.TaskResult;
import org.headvances.data.Document;
import org.springframework.context.ApplicationContext;
/**
 * $Author: Tuan Nguyen$ 
 **/
public class InputDocumentTask  extends ClusterTask<TaskResult>  {
	private List<Document> docs = new ArrayList<Document>() ;
	
	public InputDocumentTask() {} 
	
	public InputDocumentTask(List<Document> docs) {
		this.docs = docs ;
	}
	
	public void add(Document doc) { docs.add(doc) ; }
	
	public TaskResult call() {
		TaskResult result = new TaskResult(this) ;
		ApplicationContext ctx = getApplicationContext() ;
		if(ctx != null) {
			AnalysisInputGateway gw = ctx.getBean(AnalysisInputGateway.class) ;
			for(int i = 0; i < docs.size(); i++) {
				gw.send(docs.get(i)) ;
			}
			result.addMessage("Input " + docs.size() + " HtmlDocument!!!") ;
		} {
			result.addMessage("This is not an analysis server!!!") ;
		}
		return result ;
	}
}