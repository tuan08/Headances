package org.headvances.analysis.xhtml;

import javax.annotation.PostConstruct;

import org.headvances.analysis.Analyzer;
import org.headvances.data.Document;
import org.headvances.data.Entity;
import org.headvances.data.HtmlDocumentUtil;
import org.headvances.html.dom.TDocument;
import org.springframework.jmx.export.annotation.ManagedResource;
/**
 * $Author: Tuan Nguyen$ 
 **/
@ManagedResource(
	objectName="org.headvances.analysis.xhtml:name=XHTMLErrorAnalyzer", 
	description="This component is responsible for finding the corrupted documents"
)
public class XHTMLErrorAnalyzer implements Analyzer {
	public XHTMLErrorAnalyzer() {
	}
	
	@PostConstruct
	public void onInit() throws Exception {
	}

	public void analyze(Document hDoc, TDocument tdoc) {
		Entity responseHeader = HtmlDocumentUtil.getHttpResponseHeader(hDoc) ;
		if(responseHeader != null) {
			String responseCode = responseHeader.getString("response-code") ;
			if(responseCode != null) {
				int rcode = Integer.parseInt(responseCode) ;
				if(rcode < 200) hDoc.addTag("error:response:100") ;
				else if(rcode >= 500) hDoc.addTag("error:response:500") ;
				else if(rcode >= 400) hDoc.addTag("error:response:400") ;
				else if(rcode >= 300) hDoc.addTag("error:response:300") ;
				if(rcode != 200) return ;
			}
		}
		String content = hDoc.getContent() ;
		if(content == null || content.length() < 1000) {
			hDoc.addTag("error:content:length") ;
		}
	}
	
}