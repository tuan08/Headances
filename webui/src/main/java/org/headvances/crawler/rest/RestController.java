package org.headvances.crawler.rest;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import org.headvances.cluster.ClusterClient;
import org.headvances.cluster.ClusterMember;
import org.headvances.cluster.task.GetJVMInfoTask;
import org.headvances.cluster.task.TaskResult;
import org.headvances.crawler.cluster.task.GetDataProcessInfoTask;
import org.headvances.crawler.cluster.task.GetFetcherInfoTask;
import org.headvances.crawler.cluster.task.GetSiteContextTask;
import org.headvances.crawler.cluster.task.GetURLDatumCommitInfoTask;
import org.headvances.crawler.cluster.task.GetURLDatumScheduleInfoTask;
import org.headvances.crawler.cluster.task.ModifySiteContextTask;
import org.headvances.crawler.fetch.http.FetcherInfo;
import org.headvances.crawler.master.URLDatumCommitInfo;
import org.headvances.crawler.master.URLDatumScheduleInfo;
import org.headvances.crawler.process.DataProcessInfo;
import org.headvances.data.Document;
import org.headvances.html.dom.TDocument;
import org.headvances.html.dom.extract.DocumentXPathExtractor;
import org.headvances.html.dom.extract.ExtractBlock;
import org.headvances.html.dom.extract.ExtractContent;
import org.headvances.html.fetcher.Fetcher;
import org.headvances.html.fetcher.HttpClientFetcher;
import org.headvances.json.JSONSerializer;
import org.headvances.util.html.URLNormalizer;
import org.headvances.util.jvm.JVMInfo;
import org.headvances.xhtml.site.SiteConfig;
import org.headvances.xhtml.site.SiteContext;
import org.headvances.xhtml.site.SiteContext.Modify;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import com.hazelcast.core.Member;
@Controller
@RequestMapping("/")
public class RestController {
	private ClusterClient client = new ClusterClient();
	
	@ResponseBody 
	@RequestMapping(value="connect", method = RequestMethod.GET)
	public List<ClusterMember> connect(@RequestParam(required = true) String url,
			                               @RequestParam(required = true) String username, 
			                               @RequestParam(required = true) String password) throws Exception {
		ClusterClient client = getCrawlerClient() ;
		client.connect(username, password, url) ;
		List<ClusterMember> members = client.getClusterMembers() ;
		return members;
	}
	
	@ResponseBody 
	@RequestMapping(value="getSiteInfos", method = RequestMethod.GET)
	public List<SiteInfoModel> getSiteInfos(@RequestParam(required = true) String host, 
			                               @RequestParam(required = true) int port) throws Exception {
		ClusterClient client = getCrawlerClient() ;
		GetSiteContextTask task = new GetSiteContextTask();
    Member member = client.getMember(host, port) ;
    List<SiteContext> contexts  = client.execute(task, member);
    SiteInfoModelManager manager = new SiteInfoModelManager() ;
    manager.setSiteContext(contexts) ;
    return manager.getSiteInfos() ;
	}
	
	@ResponseBody
	@RequestMapping(value="getSiteConfig", method = RequestMethod.GET)
	public SiteConfig getSiteConfig(@RequestParam(required = true) String host, 
			                            @RequestParam(required = true) int port,
			                            @RequestParam(required = true) String hostname) throws Exception {
		ClusterClient client = getCrawlerClient() ;
		GetSiteContextTask task = new GetSiteContextTask();
    Member member = client.getMember(host, port) ;
    List<SiteContext> contexts  = client.execute(task, member);
    SiteInfoModelManager manager = new SiteInfoModelManager() ;
    manager.setSiteContext(contexts) ;
    URLNormalizer urlnorm = new URLNormalizer(hostname) ;
    SiteConfig config = manager.getSiteConfig(urlnorm.getNormalizeHostName()) ;
    System.out.println(JSONSerializer.JSON_SERIALIZER.toString(config)) ;
    return config ;
	}

	@ResponseBody
	@RequestMapping(value={"saveSiteConfig"}, 
	                method = {RequestMethod.POST, RequestMethod.PUT}, 
	                headers ={"Content-Type=application/json", "Accept=application/json" })
	public SiteConfig saveSiteConfig(@RequestParam(required = true) String host, 
                                   @RequestParam(required = true) int port,
                                   @RequestBody SiteConfig config) throws Exception {
		System.out.println("call saveSiteConfig.........................") ;
		ClusterClient client = getCrawlerClient() ;
		GetSiteContextTask task = new GetSiteContextTask();
    Member member = client.getMember(host, port) ;
    List<SiteContext> contexts  = client.execute(task, member);
    SiteInfoModelManager manager = new SiteInfoModelManager() ;
    manager.setSiteContext(contexts) ;
    SiteContext context = manager.getSiteContext(config.getHostname()) ;
    if(context == null) {
    	context = new SiteContext(config) ;
    	context.setModify(Modify.ADD) ;
    	manager.addSiteContext(context) ;
    } else {
    	context.setModify(Modify.MODIFIED) ;
    	context.setSiteConfig(config) ;
    }
    
    ModifySiteContextTask modifiedTask = new ModifySiteContextTask();
    List<SiteContext> modifiedContexts = new ArrayList<SiteContext>() ;
    modifiedContexts.add(context) ;
    modifiedTask.add(modifiedContexts);
    Collection<TaskResult> results = client.execute(modifiedTask) ;
    Iterator<TaskResult> i = results.iterator() ;
    while(i.hasNext()) {
      TaskResult result = i.next() ;
      System.out.println(result.getMember() + ": " + result.getMember());
    }
    System.out.println(JSONSerializer.JSON_SERIALIZER.toString(config)) ;
    return config ;
	}
	
	@ResponseBody
	@RequestMapping(value={"testExtractXpathConfig"}, method = RequestMethod.POST)
	public Map<String, Map<String, String>> testExtractXpathConfig(@RequestParam(required = true) String url,
                                                                 @RequestBody SiteConfig config) throws Exception {
		System.out.println("call testExtractXpathConfig.........................") ;
    Map<String, Map<String, String>> holder = new HashMap<String, Map<String, String>>() ;
    DocumentXPathExtractor extractor = new DocumentXPathExtractor() ;
    Fetcher fetcher = new HttpClientFetcher();
    Document hdoc = fetcher.fetch(url);
		TDocument tdoc = new TDocument(null, url, hdoc.getContent()) ;
		ExtractContent extractContent = extractor.extract(null, tdoc, config.getXpathConfig()) ;
		if(extractContent == null) return holder ;
		
		List<ExtractBlock> blocks = extractContent.getExtractBlocks() ;
		for(int i = 0; i < blocks.size(); i++) {
			ExtractBlock block = blocks.get(i) ;
			Map<String, String> map = new HashMap<String, String>() ;
			Map<String, ExtractBlock.ExtractNode> extractNodes = block.getExtractNodes() ;
			Iterator<Map.Entry<String, ExtractBlock.ExtractNode>> itr = extractNodes.entrySet().iterator() ;
			while(itr.hasNext()) {
				Map.Entry<String, ExtractBlock.ExtractNode> entry = itr.next() ;
				map.put(entry.getKey(), entry.getValue().getTextContent()) ;
			}
			holder.put(block.getBlockName(), map) ;
		}
		return holder ;
	}
	
	@ResponseBody 
	@RequestMapping(value="getDataProcessInfo", method = RequestMethod.GET)
	public DataProcessInfo getDataProcessInfo(@RequestParam(required = true) String host, 
			                                      @RequestParam(required = true) int port) throws Exception {
		ClusterClient client = getCrawlerClient() ;
		Member member = client.getMember(host, port) ;
    DataProcessInfo info  = client.execute(new GetDataProcessInfoTask(), member);
    return info ;
	}

	@ResponseBody 
	@RequestMapping(value="getURLDatumScheduleInfo", method = RequestMethod.GET)
	public List<URLDatumScheduleInfo> getURLDatumScheduleInfo(@RequestParam(required = true) String host, 
	                                                          @RequestParam(required = true) int port) throws Exception {
		ClusterClient client = getCrawlerClient() ;
		Member member = client.getMember(host, port) ;
    List<URLDatumScheduleInfo> info  = client.execute(new GetURLDatumScheduleInfoTask(), member);
    return info ;
	}
	
	@ResponseBody 
	@RequestMapping(value="getURLDatumCommitInfo", method = RequestMethod.GET)
	public List<URLDatumCommitInfo> getURLDatumCommitInfo(@RequestParam(required = true) String host, 
		                                                    @RequestParam(required = true) int port) throws Exception {
		ClusterClient client = getCrawlerClient() ;
		Member member = client.getMember(host, port) ;
    List<URLDatumCommitInfo> info  = client.execute(new GetURLDatumCommitInfoTask(), member);
    return info ;
	}
	
	@ResponseBody 
	@RequestMapping(value="getFetcherInfos", method = RequestMethod.GET)
	public List<Map<String, Object>> getFetcherInfos(@RequestParam(required = true) String host, 
			                                             @RequestParam(required = true) int port) throws Exception {
    Member member = client.getMember(host, port) ;
		GetFetcherInfoTask task = new GetFetcherInfoTask() ;
		ClusterClient client = getCrawlerClient() ;
		List<FetcherInfo> fetcherInfos = client.execute(task, member) ;
    List<Map<String, Object>> holder = new ArrayList<Map<String, Object>>() ;
		for(int i = 0; i < fetcherInfos.size(); i++) {
			FetcherInfo info = fetcherInfos.get(i) ;
			Map<String, Object> model = new HashMap<String, Object>() ;
			model.put("Fetch", info.getFetchCount()) ;
			model.put("RC 100", info.getResponseCodeGroup100()) ;
			model.put("RC 200", info.getResponseCodeGroup200()) ;
			model.put("RC 300", info.getResponseCodeGroup300()) ;
			model.put("RC 400", info.getResponseCodeGroup400()) ;
			model.put("RC 500", info.getResponseCodeGroup500()) ;
			model.put("RC Other", info.getResponseCodeGroup10000()) ;
			model.put("Recent Fetch Urls", info.recentFetchUrl()) ;
			holder.add(model) ;
		}
		System.out.println("getFetcherInfos return : " + holder.size()) ;
    return holder;
	}
	
	@ResponseBody 
	@RequestMapping(value="getJVMInfo", method = RequestMethod.GET)
	public JVMInfo getJVMInfo(@RequestParam(required = true) String host, 
			                      @RequestParam(required = true) int port) throws Exception {
		ClusterClient client = getCrawlerClient() ;
		Member member = client.getMember(host, port) ;
    JVMInfo jvmInfo  = client.execute(new GetJVMInfoTask(), member);
    return jvmInfo ;
	}
	
	private ClusterClient getCrawlerClient() { return this.client ; }
}