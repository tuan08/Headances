package org.headvances.search.webui;

import javax.servlet.http.HttpSession;
/**
 * $Author: Tuan Nguyen$ 
 **/
public class SearchContext {
	static String QUERY_KEY  = "search.query" ;
	static String UICONFIG   = "search.uiconfig" ;

	private HttpSession session;  

	public SearchContext(HttpSession session) {
		this.session = session ;
	}

	public Query getCurrentQuery() { return (Query) session.getAttribute(QUERY_KEY) ; }
	public void  setCurrentQuery(Query q) {
		session.setAttribute(QUERY_KEY, q) ;
	}

	public UIConfig getUIConfig() { 
		UIConfig config = (UIConfig) session.getAttribute(UICONFIG) ; 
		if(config == null) {
			config = new UIConfig() ;
			session.setAttribute(UICONFIG, config) ;
		}
		return config ;
	}
	
	public void  setUIConfig(UIConfig uiconfig) {
		session.setAttribute(UICONFIG, uiconfig) ;
	}
}