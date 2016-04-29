package org.headvances.analysis.integration;

import org.headvances.data.Document;
import org.headvances.json.JSONMultiFileReader;
import org.headvances.data.HtmlDocumentUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.integration.Message;
import org.springframework.integration.MessageChannel;
import org.springframework.integration.message.GenericMessage;
/**
 * $Author: Tuan Nguyen$ 
 **/
public class FileInputAdapter {
	private int count = 0 ;
	
	@Autowired
	@Qualifier("AnalysisInputChannel")
	private MessageChannel  inputChannel ;
	
	public Message<Document> produce() {
		Document hdoc = new Document() ;
		HtmlDocumentUtil.setHtmlLink(hdoc, "No Anchor Text", "http://localhost", 1, null);
		hdoc.setContent("<html><body>This is a test " + count++ + "</body></html>") ;
		Message<Document> message = new GenericMessage<Document>(hdoc) ;
		return message ;
	}
	
	public void producce(String location) throws Exception {
		JSONMultiFileReader reader = new JSONMultiFileReader(location) ;
		Document doc = null ;
		while((doc = reader.next(Document.class)) != null) {
			Message<Document> message = new GenericMessage<Document>(doc) ;
			inputChannel.send(message) ;
		}
	}
}