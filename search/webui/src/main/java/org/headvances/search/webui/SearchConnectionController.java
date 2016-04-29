package org.headvances.search.webui;

import javax.validation.Valid;

import org.headvances.search.ESClientService;
import org.headvances.search.ESJSONClient;
import org.headvances.util.text.StringUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

@Controller
public class SearchConnectionController {
	@Autowired 
	private ESClientService service ;
	
	@RequestMapping("/search/connection")
  public String connection(Model model) {
		model.addAttribute("connection", new Connection() ) ;
	  return "search/connection/Connection";
  }
	
	@RequestMapping(value="/search/connection", method=RequestMethod.POST)
	public String connection(@Valid Connection connection, BindingResult result) {
		if (result.hasErrors()) {
			return "/search/connection" ;
		}
		String[] url = StringUtil.toStringArray(connection.getUrls()) ;
		service.connect(url, connection.getIndex(), connection.getType()) ;
		return "redirect:/search/connection/" + connection.getIndex();
	}
	
	@RequestMapping(value="/search/connection/{index}", method=RequestMethod.GET)
	public String getView(@PathVariable String index, Model model) {
		ESJSONClient client = service.getClient(index) ;
		if (client == null) {
			throw new ResourceNotFoundException("Not found index " + index);
		}
		Connection connection = new Connection() ;
		connection.setIndex(client.getIndex()) ;
		connection.setType(client.getType()) ;
		connection.setUrls(StringUtil.joinStringArray(client.getAddress())) ;
		model.addAttribute("connection", connection);
		return "search/connection/Connection";
	}
}