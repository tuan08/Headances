package org.headvances.xhtml.site;

import java.io.Serializable;
import java.util.HashMap;
import java.util.Map;

import org.headvances.util.html.URLNormalizer;
/**
 * Author : Tuan Nguyen
 *          tuan.nguyen@headvances.com
 * Apr 14, 2010  
 */
public class SiteContext implements Serializable {
  static public enum Modify { NONE, ADD, DELETE, MODIFIED }

  private Modify modify = Modify.NONE ;
  private SiteConfig siteConfig ;
  private Map<String, Serializable> attributes = new HashMap<String, Serializable>() ;
  
  public SiteContext(SiteConfig config) {
    this.siteConfig = config ;
  }

  public Modify getModify() { return this.modify ; }
  public void setModify(Modify modify) { this.modify = modify ; }

  public SiteConfig getSiteConfig() { return this.siteConfig ; }
  public void setSiteConfig(SiteConfig config) { this.siteConfig = config ; }
  
  public <T extends Serializable> boolean hasAttribute(Class<T> clazz) {
  	return attributes.containsKey(clazz.getName())  ;
  }
  
  public <T extends Serializable> T getAttribute(Class<T> clazz) {
  	return getAttribute(clazz, true) ;
  }
  
  public <T extends Serializable> T getAttribute(Class<T> clazz, boolean create) {
  	T instance = (T) attributes.get(clazz.getName()) ;
  	if(instance == null && create) {
  		try {
	      instance = clazz.newInstance() ;
	      attributes.put(clazz.getName(), instance) ;
  		} catch (InstantiationException e) {
      	throw new RuntimeException(e) ;
      } catch (IllegalAccessException e) {
	      throw new RuntimeException(e) ;
      }
  	}
  	return instance ;
  }

  public int getMaxConnection() { 
    int max = siteConfig.getMaxConnection() ;
    if(max < 1) max = 1 ;
    return max ;
  }

  public boolean allowURL(URLNormalizer urlnorm) {
    String hostname = urlnorm.getNormalizeHostName() ;
    if(hostname.equals(siteConfig.getHostname())) return true ;
    if(siteConfig.getCrawlSubDomain()) {
      return hostname.endsWith(siteConfig.getHostname()) ;
    } 
    return false ;
  }

  public void update(SiteContext other) { this.siteConfig = other.siteConfig ; }
}