package org.headvances.xhtml.url;

import org.headvances.util.html.URLNormalizer;
import org.headvances.xhtml.site.SiteContext;
/**
 * Author : Tuan Nguyen
 *          tuan.nguyen@headvances.com
 * May 4, 2010  
 */
public class URLContext {
  private URLNormalizer urlNormalizer ;
  private SiteContext   siteConfigContext ;

  public URLContext(URLNormalizer urlNormalizer, SiteContext siteConfigContext) {
    this.urlNormalizer = urlNormalizer ;
    this.siteConfigContext = siteConfigContext ;
  }

  public URLNormalizer getUrlNormalizer() { return urlNormalizer; }
  public void setUrlNormalizer(URLNormalizer urlNormalizer) { this.urlNormalizer = urlNormalizer; }

  public SiteContext getSiteContext() { return siteConfigContext ; }
  public void setSiteConfig(SiteContext siteConfigContext) { 
  	this.siteConfigContext = siteConfigContext; 
  }
}