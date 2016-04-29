package org.headvances.xhtml.site;

import org.headvances.json.JSONReader;
import org.headvances.xhtml.site.SiteConfig;
import org.junit.Test;

/**
 * $Author: Tuan Nguyen$ 
 **/
public class SiteConfigUnitTest {
	@Test
	public void testSiteConfigFileReader() throws Exception {
		JSONReader reader = new JSONReader("src/test/resources/sitedb/sites.json") ;
		SiteConfig config = null ;
		while((config = reader.read(SiteConfig.class)) != null) {
			System.out.println("hostname: " + config.getHostname());
		}
	}
}
