package org.headvances.crawler.rest;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.List;

import org.headvances.xhtml.site.SiteConfig;
import org.headvances.xhtml.site.SiteContext;

public class SiteInfoModelManager {
	private List<SiteInfoModel> siteInfos ;
	private LinkedHashMap<String, SiteContext> siteContexts = new LinkedHashMap<String, SiteContext>() ;
	
	public List<SiteInfoModel> getSiteInfos() {
		if(siteInfos == null) {
			siteInfos = new ArrayList<SiteInfoModel>() ;
			Iterator<SiteContext> i = siteContexts.values().iterator() ;
			while(i.hasNext()) {
				SiteContext context = i.next() ;
				siteInfos.add(new SiteInfoModel(context)) ;
			}
		}
		return siteInfos ;
	}
	
	public void addSiteContext(SiteContext context) {
		siteContexts.put(context.getSiteConfig().getHostname(), context) ;
		siteInfos = null ;
	}
	
	public void setSiteContext(List<SiteContext> list) {
		siteContexts.clear() ;
		for(int i = 0; i < list.size(); i++) {
			SiteContext context = list.get(i) ;
			siteContexts.put(context.getSiteConfig().getHostname(), context) ;
		}
		siteInfos = null ;
	}
	
	public SiteContext saveSiteConfig(SiteConfig config) {
		SiteContext context = siteContexts.get(config.getHostname()) ;
		if(context != null) {
			context = new SiteContext(config) ;
			siteContexts.put(config.getHostname(), context) ;
		}
		context.setSiteConfig(config) ;
		return context ;
	}
	
	public SiteContext getSiteContext(String hostname) {
		return siteContexts.get(hostname) ;
	}
	
	public SiteConfig getSiteConfig(String hostname) {
		SiteContext context = siteContexts.get(hostname) ;
		if(context != null) return context.getSiteConfig() ;
		return null ;
	}
}
