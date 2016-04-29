package org.headvances.crawler.process;

import java.util.ArrayList;
import java.util.Map;

import org.headvances.crawler.channel.ChannelGateway;
import org.headvances.crawler.fetch.FetchData;
import org.headvances.data.HtmlDocumentUtil;
import org.headvances.html.dom.TDocument;
import org.headvances.xhtml.site.SiteContextManager;
import org.headvances.xhtml.url.URLContext;
import org.headvances.xhtml.url.URLDatum;
import org.headvances.xhtml.url.URLExtractor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
/**
 * $Author: Tuan Nguyen$ 
 **/
public class FetchDataProcessor {
	private static final Logger logger = LoggerFactory.getLogger(FetchDataProcessor.class);
	
	@Autowired
	private URLExtractor urlExtractor ;
	@Autowired
  private SiteContextManager siteConfigManager ;
	
	@Autowired
	@Qualifier("XHTMLDataGateway")
	private ChannelGateway xhtmlDataGateway ;
	 
	@Autowired
  @Qualifier("URLDatumFetchCommitGateway")
  private ChannelGateway urldatumFetchGateway ;
  
	private DataProcessInfo info = new DataProcessInfo() ;
	
	public DataProcessInfo getDataProcessInfo() { return this.info; }
	
	public void process(FetchData fdata) {
		info.incrProcessCount() ;
		final long start = System.currentTimeMillis() ;
		org.headvances.data.Document hdoc = fdata.getDocument() ;
		if(hdoc == null) {
			ArrayList<URLDatum> urlList = new ArrayList<URLDatum>() ;
			urlList.add(fdata.getURLDatum()) ;
			urldatumFetchGateway.send(urlList) ;
			return ;
		}
		try {
			URLDatum urldatum = fdata.getURLDatum() ;
			TDocument  tdoc = 
				new TDocument(urldatum.getAnchorTextAsString(), urldatum.getOriginalUrlAsString(), hdoc.getContent()) ;
			URLContext context = 
				siteConfigManager.getURLContext(fdata.getURLDatum().getFetchUrl()) ;
			
			Map<String, URLDatum> urls = urlExtractor.extract(fdata.getURLDatum(), context, tdoc) ;
			info.addSumHtmlProcessTime(System.currentTimeMillis() - start) ;
			
			ArrayList<URLDatum> urlList = new ArrayList<URLDatum>() ;
			urlList.add(fdata.getURLDatum()) ;
			urlList.addAll(urls.values()) ;
			
			urldatumFetchGateway.send(urlList) ;
			xhtmlDataGateway.send(fdata.getDocument()) ;
		} catch(Exception ex) {
			ex.printStackTrace() ;
			logger.error("Cannot process HtmlDocument: " + HtmlDocumentUtil.getHtmlLink(hdoc).getString("url")) ;
		}
		info.addSumProcessTime(System.currentTimeMillis() - start) ;
	}
}