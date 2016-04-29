package org.headvances.analysis.swui;

import java.util.ArrayList;
import java.util.List;

import org.headvances.analysis.cluster.task.InputDocumentTask;
import org.headvances.cluster.ClusterClient;
import org.headvances.data.Document;
import org.headvances.json.JSONMultiFileReader;
import org.headvances.swingui.SwingUtil;

import com.hazelcast.core.Member;
/**
 * $Author: Tuan Nguyen$ 
 **/
public class AnalysisContext {
	static AnalysisContext instance  = new AnalysisContext() ;
	
	private ClusterClient client = new ClusterClient() ;
	
	public ClusterClient getClient() { return this.client ; }

	public void connectClient(String groupName, String password, String address) throws Exception {
		client.connect(groupName, password, address) ;
		AnalysisApplicationPlugin instance = AnalysisApplicationPlugin.getInstance() ;
		List<ClientListener> listeners = 
			SwingUtil.findDescentdantsOfType(instance.getAnalysisPane(), ClientListener.class) ;
		for(ClientListener sel : listeners) {
			sel.onConnect(client) ;
		}
	}
	
	public void inputDocument(final Member member, final String location) {
		new Thread() {
			public void run() {
				try {
					JSONMultiFileReader reader = new JSONMultiFileReader(location) ;
					Document doc = null ;
					List<Document> docs = new ArrayList<Document>() ;
					while((doc = reader.next(Document.class)) != null) {
						docs.add(doc) ;
						if(docs.size() == 25) {
							client.execute(new InputDocumentTask(docs) , member) ;
							docs.clear() ;
						}
					}
					if(docs.size() > 0) {
						client.execute(new InputDocumentTask(docs) , member) ;
					}
				} catch (Exception e) {
					e.printStackTrace();
				}
			}
		}.start() ;
	}
	
	static public AnalysisContext getInstance() { return instance ; }
}