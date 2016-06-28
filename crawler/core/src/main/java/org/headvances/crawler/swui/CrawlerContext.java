package org.headvances.crawler.swui;

import java.util.List ;

import org.headvances.cluster.ClusterClient;
import org.headvances.cluster.task.TaskResult;
import org.headvances.crawler.CrawlerFetcher ;
import org.headvances.crawler.cluster.task.ClusterServiceTask ;
import org.headvances.crawler.cluster.task.ClusterServiceTask.Command ;
import org.headvances.crawler.cluster.task.InjectURLTask ;
import org.headvances.crawler.cluster.task.ModifySiteContextTask ;
import org.headvances.crawler.master.CrawlerMaster ;
import org.headvances.swingui.SwingUtil ;

/**
 * $Author: Tuan Nguyen$ 
 **/
public class CrawlerContext {
  static CrawlerContext instance = new CrawlerContext();

  private ClusterClient crawlerClient = new ClusterClient();

  public ClusterClient getCrawlerClient() {
    return this.crawlerClient;
  }

  public void connectCrawlerClient(String groupName, String password,
      String address) throws Exception {
    crawlerClient.connect(groupName, password, address);
    CrawlerApplicationPlugin instance = CrawlerApplicationPlugin.getInstance();
    List<CrawlerClientListener> listeners = SwingUtil.findDescentdantsOfType(
        instance.getCrawlerPane(), CrawlerClientListener.class);
    for (CrawlerClientListener sel : listeners) {
      sel.onConnect(crawlerClient);
    }
  }

  public void startCrawlerServiceWithConfig(final boolean start, final String configFile) throws Exception {
    Thread thread = new Thread() {
      public void run() {
        try {
          CrawlerMaster.run();
          CrawlerFetcher.run();
          connectCrawlerClient("crawler", "crawler", "localhost:5700");
          if (configFile != null) {
            ModifySiteContextTask addConfigTask = new ModifySiteContextTask();
            addConfigTask.add(configFile);
            TaskResult.dump(crawlerClient.execute(addConfigTask));
          }
          
          if (configFile == null) {
            ModifySiteContextTask addConfigTask = new ModifySiteContextTask();
            addConfigTask.add("dantri.com.vn", "http://dantri.com.vn", 3, 2, "ok");
            addConfigTask.add("vnexpress.net", "http://vnexpress.net", 3, 2, "ok");
            //addConfigTask.add("edition.cnn.com", "http://edition.cnn.com/", 3, 2, "ok");
            //addConfigTask.add("careerbuilder.com", "http://careerbuilder.com/", 3, 2, "ok");
            TaskResult.dump(crawlerClient.execute(addConfigTask));
          }
          
          if (start) {
            TaskResult.dump(crawlerClient.execute(new InjectURLTask(), "master"));
            TaskResult.dump(crawlerClient.execute(new ClusterServiceTask(Command.START))) ;
          }
        } catch (Exception e) {
          e.printStackTrace();
        }
      }
    };
    thread.start();
  }

  static public CrawlerContext getInstance() {
    return instance;
  }
}