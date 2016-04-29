package org.headvances.analysis.xhtml;

import org.headvances.data.Document;
import org.headvances.html.dom.TDocument;
import org.headvances.html.dom.extract.DocumentXPathExtractor;
import org.headvances.html.dom.extract.ExtractContent;
import org.headvances.html.dom.extract.XpathConfig;
import org.headvances.xhtml.site.SiteConfig;
import org.headvances.xhtml.site.SiteContext;
import org.headvances.xhtml.site.SiteContextManager;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jmx.export.annotation.ManagedAttribute;
import org.springframework.jmx.export.annotation.ManagedResource;

@ManagedResource(
		objectName="org.headvances.analysis.xhtml:name=XHTMLContentXPathExtractor", 
		description="This component is responsible for extracting the main content via the xpath config"
	)
public class XHTMLContentXPathExtractor {
	@Autowired
	private SiteContextManager ctxManager ;
	private DocumentXPathExtractor extractor = new DocumentXPathExtractor() ;
	
	private int count ;
	private int extractCount ;
	
	@ManagedAttribute(description="Count all the processed document")
	public int getCount() { return count ; }

	@ManagedAttribute(description="Count all the processed document with the extract content")
	public int getExtractCount() { return extractCount ; }

	
	public ExtractContent extract(Document hDoc, TDocument tdoc, String hint) {
		count++ ;
		String url = tdoc.getUrl() ;
		SiteContext context = ctxManager.getSiteContext(url) ;
		if(context == null) return null ;
		SiteConfig config = context.getSiteConfig() ;
		XpathConfig[] xpathConfig = config.getXpathConfig() ;
		if(xpathConfig == null || xpathConfig.length == 0) return null ;
		ExtractContent extractContent = extractor.extract(null, tdoc, xpathConfig) ;
		if(extractContent != null) extractCount++ ;
		return extractContent ;
	}
}
