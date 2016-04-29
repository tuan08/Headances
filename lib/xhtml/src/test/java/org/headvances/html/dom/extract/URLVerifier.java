package org.headvances.html.dom.extract;

import junit.framework.Assert;

import org.headvances.data.Document;
import org.headvances.html.dom.TDocument;
import org.headvances.html.dom.extract.DocumentExtractor.Type;
import org.headvances.html.fetcher.Fetcher;

/**
 * $Author: Tuan Nguyen$ 
 **/
public class URLVerifier {
	private String   anchorText ;
	private String   url ;
	private DocumentExtractor.Type type ;
	private String[] expectTags ;
	
	public URLVerifier(String anchorText, String url, 
			               DocumentExtractor.Type type, String[] expectTag) {
		this.anchorText = anchorText ;
		this.url = url ;
		this.type = type ;
		this.expectTags = expectTag ;
	}

	public ExtractContent extract(Fetcher fetcher) throws Exception {
		Document hdoc = fetcher.fetch(url);
		DocumentExtractor extractor = new DocumentExtractor() ;
		TDocument tdoc = new TDocument(anchorText, url, hdoc.getContent()) ;
		//TNodePrinter visitor = new TNodePrinter(System.out) ;
		//visitor.process(tdoc.getRoot()) ;
		ExtractContent extractContent = extractor.extract(type, tdoc) ;
		return extractContent ;
	}
	
	public void verify(Fetcher fetcher, boolean dump) throws Exception {
		ExtractContent extractContent = extract(fetcher) ;
		if(dump) extractContent.dump(System.out) ;
		ExtractBlock mainBlock = extractContent.getExtractBlock("mainContent") ;
		Assert.assertNotNull(mainBlock) ;
		String[] tags = mainBlock.getTags() ;
		for(String expectTag : expectTags) {
			boolean foundTag = false ;
			for(String tag : tags) {
				if(tag.equals(expectTag)) {
					foundTag = true ;
					break ;
				}
			}
			if(!foundTag) Assert.fail("Expect tag " + expectTag) ;
		}
	}
}
