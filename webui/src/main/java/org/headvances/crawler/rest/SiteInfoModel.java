package org.headvances.crawler.rest;

import java.util.HashMap;
import java.util.Map;

import org.headvances.xhtml.site.SiteConfig;
import org.headvances.xhtml.site.SiteContext;
import org.headvances.xhtml.site.SiteScheduleStat;
import org.headvances.xhtml.url.URLDatumStatisticMap;

public class SiteInfoModel {
	private String modify ;
	private SiteConfig siteConfig ;
	
	private int scheduleCount ;
	private int processCount ;

	private Map<Object, Object> fetchStatus = new HashMap<Object, Object>() ;
	private Map<Object, Object> responseCode = new HashMap<Object, Object>() ;
	private Map<Object, Object> fetchCount = new HashMap<Object, Object>() ;
	
	public SiteInfoModel() { }

	public SiteInfoModel(SiteContext context) {
		this.modify = context.getModify().toString() ;
		this.siteConfig = context.getSiteConfig() ;
		SiteScheduleStat scheduleStat = context.getAttribute(SiteScheduleStat.class) ;
		this.scheduleCount = scheduleStat.getScheduleCount() ;
		this.processCount  = scheduleStat.getProcessCount() ;
		URLDatumStatisticMap statMap = context.getAttribute(URLDatumStatisticMap.class) ;
		initMap(fetchStatus, statMap.getStatisticMap().getStatisticData("Fetch Status", null));
		initMap(responseCode, statMap.getStatisticMap().getStatisticData("Response Code", null));
		initMap(fetchCount, statMap.getStatisticMap().getStatisticData("Fetch Count", null));
	}

	public String getModify() { return this.modify ;}
	public void setModify(String s) { this.modify = s ; }
	
	public SiteConfig getSiteConfig() { return siteConfig; }
	public void setSiteConfig(SiteConfig siteConfig) { this.siteConfig = siteConfig; }

	public int getScheduleCount() { return scheduleCount; }
	public void setScheduleCount(int scheduleCount) { this.scheduleCount = scheduleCount; }

	public int getProcessCount() { return processCount; }
	public void setProcessCount(int processCount) { this.processCount = processCount; }
	
	public Map<Object, Object> getFetchStatus() { return this.fetchStatus ; }
	public void setFetchStatus(Map<Object, Object> fetchStatus) { this.fetchStatus = fetchStatus ; }
	
	public Map<Object, Object> getResponseCode() { return this.responseCode ; }
	
	public Map<Object, Object> getFetchCount() { return this.fetchCount ; }
	
	private void initMap(Map<Object, Object> map, Object[][] data) {
		for(int i = 0; i < data.length; i++) {
			map.put(data[i][0], data[i][1]) ;
		}
	}
}