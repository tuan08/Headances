package org.headvances.search.rest.webmonitor;

import java.util.List;

import javax.servlet.http.HttpSession;

import org.headvances.ml.nlp.opinion.Opinion;
import org.headvances.search.ESClientService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

@Controller
@RequestMapping(value="/rest/webmonitor")
public class WebMonitorController {
	@Autowired 
	private ESClientService service ;
	
	@RequestMapping(value="/list", method=RequestMethod.GET)
	public @ResponseBody WebMonitors list(HttpSession session) {
		WebMonitors wmonitors = WebMonitors.getInstance(session) ;
		return wmonitors ;
	}
	
	@RequestMapping(value="/opinions", method=RequestMethod.GET)
	public @ResponseBody Opinions opinions(HttpSession session, OpinionFilter filter, @RequestParam int page) {
		WebMonitors wmonitors = WebMonitors.getInstance(session) ;
		WebMonitor  wmonitor = wmonitors.getWebMonitor(filter.getEntity()) ;
		if(wmonitor == null) return new Opinions() ;
    try {
    	OpinionHolder holder = wmonitor.getOpinions() ;
    	List<Opinion> opinions = holder.getOpinions(filter) ;
    	System.out.println("Found Opinions: " + opinions.size());
    	return new Opinions(opinions, page, 100) ;
    } catch (Exception e) {
	    e.printStackTrace();
    }
    return new Opinions() ;
	}
	
	@RequestMapping(value="/entity", method=RequestMethod.GET)
	public @ResponseBody WebMonitor entity(HttpSession session, @RequestParam String entity) {
		WebMonitors wmonitors = WebMonitors.getInstance(session) ;
		WebMonitor wmonitor = wmonitors.getWebMonitor(entity) ;
		return wmonitor ;
	}
	
	@RequestMapping(value="/execute", method=RequestMethod.GET)
	public @ResponseBody WebMonitor execute(HttpSession session, @RequestParam String entity) {
		WebMonitors wmonitors = WebMonitors.getInstance(session) ;
		WebMonitor wmonitor = wmonitors.getWebMonitor(entity) ;
		if(wmonitor == null) return null ;
		try {
			wmonitor.update(service) ;
		} catch(Exception ex) {
			ex.printStackTrace(); 
		}
		return wmonitor ;
	}
	
	@RequestMapping(value="/delete", method=RequestMethod.GET)
	public @ResponseBody WebMonitors delete(HttpSession session, @RequestParam String entity) {
		WebMonitors wmonitors = WebMonitors.getInstance(session) ;
		wmonitors.remove(entity) ;
    return wmonitors ;
	}
}