package org.headvances.search.rest.webmonitor;

import java.util.LinkedHashMap;
import java.util.Map;
import javax.servlet.http.HttpSession;
/**
 * $Author: Tuan Nguyen$ 
 **/
public class WebMonitors {
  static private String[] DEMO_ENTITIES = {
  	"Iphone", "Iphone 3GS", "Iphone 4", "Samsung Galaxy", "Nokia Lumia", "HTC Desire", "XT720"
  }; 

  private Map<String, WebMonitor> webMonitor = new LinkedHashMap<String, WebMonitor>() ;

  public WebMonitor getWebMonitor(String entity) { return webMonitor.get(entity) ; }

  public void add(WebMonitor wmonitor) { webMonitor.put(wmonitor.getEntity(), wmonitor) ; }

  public WebMonitor[] getMonitor() {
    return webMonitor.values().toArray(new WebMonitor[webMonitor.size()]) ;
  }

  public void remove(String entity) { webMonitor.remove(entity) ; }

  static public WebMonitors getInstance(HttpSession session) {
    WebMonitors wmonitors = (WebMonitors)session.getAttribute("WebMonitors") ;
    if(wmonitors == null) {
      wmonitors = new WebMonitors() ;
      for(String entity: DEMO_ENTITIES){
        WebMonitor wmonitor = new WebMonitor() ;
        wmonitor.setEntity(entity) ;
        wmonitor.setDescription("Monitor the '" + entity +"' entity") ;
        wmonitors.add(wmonitor) ;
      }
      session.setAttribute("WebMonitors", wmonitors) ;
    }
    return wmonitors ;
  }
}