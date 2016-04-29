package org.headvances.crawler.tool;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Iterator;
import java.util.List;

import org.headvances.cluster.ClusterClient;
import org.headvances.cluster.task.TaskResult;
import org.headvances.crawler.CrawlerFetcher;
import org.headvances.crawler.cluster.task.ModifySiteContextTask;
import org.headvances.crawler.integration.DocumentConsumer;
import org.headvances.crawler.integration.FileDocumentConsumer;
import org.headvances.crawler.master.CrawlerMaster;
import org.headvances.hadoop.util.ConfigurationFactory;
import org.headvances.jms.EmbededActiveMQServer;
import org.headvances.json.JSONReader;
import org.headvances.util.CommandParser;
import org.headvances.util.FileUtil;
import org.headvances.util.IOUtil;
import org.headvances.xhtml.site.SiteConfig;
import org.headvances.xhtml.site.SiteContext;
import org.headvances.xhtml.site.SiteContext.Modify;
import org.springframework.context.ApplicationContext;
/**
 * $Author: Tuan Nguyen$ 
 **/
public class Crawler {
  private int deep    = 0 ;
  private int maxConn = 0 ;

  private CrawlerMaster  master;
  private CrawlerFetcher fetcher ;

  public Crawler() {
  }

  public Crawler(int maxConn, int deep) {
    this.maxConn = maxConn ;
    this.deep = deep ;
  }

  public void run(List<SiteContext> scontexts) throws Exception {
    System.err.println("\nLaunching ActiveMQ!!!!!!!!!!!!!!!!!!!!!!\n");
    EmbededActiveMQServer.run() ;
    System.err.println("\nLaunched ActiveMQ Successfully!!!!!!!!!!!!!!!!!!!!!!\n");

    System.err.println("\nLaunching Crawler Master!!!!!!!!!!!!!!!!!!!!!!\n");
    CrawlerMaster.run() ;
    ApplicationContext crawlerMasterContext = CrawlerMaster.getApplicationContext() ;
    master = crawlerMasterContext.getBean(CrawlerMaster.class) ;
    System.err.println("\nLaunched Crawler Master Successfully!!!!!!!!!!!!!!!!!!!!!!\n");

    System.err.println("\nLaunching Crawler Fetcher!!!!!!!!!!!!!!!!!!!!!!\n");
    CrawlerFetcher.run(); 
    ApplicationContext crawlerFetcherContext = CrawlerFetcher.getApplicationContext() ;
    fetcher  = crawlerFetcherContext.getBean(CrawlerFetcher.class) ;
    System.err.println("\nLaunched Crawler Fetcher Successfully!!!!!!!!!!!!!!!!!!!!!!\n");
    //CrawlerIntegrationTest.run() ;

    ClusterClient client = new ClusterClient("crawler", "crawler", "127.0.0.1:5700") ;

    ModifySiteContextTask task = new ModifySiteContextTask() ;
    task.add(scontexts) ;
    Collection<TaskResult> results = client.execute(task) ;
    Iterator<TaskResult> i = results.iterator() ;
    while(i.hasNext()) {
      TaskResult result = i.next() ;
      System.out.println(result.getMember() + ": " + result.getMember());
    }

    master.getURLDatumFetchScheduler().injectURL() ;
    master.start() ;
    fetcher.start() ;
  }

  public List<SiteContext> loadSiteConfig(String file) throws Exception {
    List<SiteContext> contexts = new ArrayList<SiteContext>() ;
    JSONReader reader = new JSONReader(IOUtil.loadRes(file)) ;
    SiteConfig config = null ;
    while((config = reader.read(SiteConfig.class)) != null) {
      if(maxConn > 0) config.setMaxConnection(maxConn) ;
      if(deep > 0) config.setCrawlDeep(deep) ;
      SiteContext context = new SiteContext(config) ;
      context.setModify(Modify.ADD) ;
      contexts.add(context) ;
    }
    return contexts ;
  }

  static private void run(String[] args) throws Exception{
    CommandParser command = new CommandParser("Crawler") ;
    command.addMandatoryOption("config", true, "The json site config file") ;
    command.addOption("store", true, "The storage implementation,  either file or db") ;
    command.addOption("deep",   true, "The crawl deep") ;
    command.addOption("maxconn",   true, "Max frequency connection per site") ;
    command.addOption("compression", true, "The compression codec default/gzip/lzo");
    command.addOption("clean", false, "Should remove the queue and the data") ;

    if(!command.parse(args)) {
      System.out.println(
          "Example: crawl -store file -config classpath:site-config.json -deep 2 -maxconn 2 -compression default -clean"
      );
      System.out.println("Available Config file: classpath:33-sites-config.json");
      return ;
    }
    
    command.printHelp() ;
    System.out.println(
        "Example: crawl -store file -config classpath:site-config.json -deep 2 -maxconn 2 -compression default -clean"
    );
    System.out.println("Available Config file: classpath:33-sites-config.json");

    if(command.hasOption("clean")) {
      String crawlerDataDir = System.getProperty("crawler.data.dir") ;
      String activeMQDataDir = System.getProperty("activemq.data.dir") ;
      if(crawlerDataDir != null) FileUtil.removeIfExist(crawlerDataDir) ;
      if(activeMQDataDir != null) FileUtil.removeIfExist(activeMQDataDir) ;
    }
    
    String compression = command.getOption("compression", null) ;
    if(compression != null) {
      ConfigurationFactory.setCompressionCodec(compression) ;
    }
    String store = command.getOption("store", null) ;
    if(store != null) {
      if("file".equals(store)) {
        System.setProperty("document.consumer", FileDocumentConsumer.class.getName()) ;
      }
    }

    String configFile = command.getOption("config", null) ;
    int deep = command.getOptionAsInt("deep", 2) ;
    int maxconn = command.getOptionAsInt("maxconn", 2) ;

    Crawler crawler = new Crawler(maxconn, deep) ;
    if(configFile.indexOf(":") < 0) configFile =  "file:" + configFile ;
    crawler.run(crawler.loadSiteConfig(configFile)) ;

    DocumentConsumer.run() ;
    Thread.currentThread().join() ;
  }

  static public void main(String[] args) throws Exception {
    if(args.length == 0) {
      args = new String[] {
          "-config", "33-sites-config.json",
          "-deep", "8",
          "-maxconn", "3"
      };
    }
    Crawler.run(args);
  }
}