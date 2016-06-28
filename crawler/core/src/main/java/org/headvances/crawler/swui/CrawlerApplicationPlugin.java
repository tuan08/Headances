package org.headvances.crawler.swui;

import javax.swing.JFrame;

import org.headvances.jms.swui.JMSApplicationPlugin;
import org.headvances.swingui.ApplicationFrame;
import org.headvances.swingui.ApplicationPlugin;
import org.headvances.swingui.SwingApplication;
import org.headvances.swingui.component.JMenuBarExt;
/**
 * $Author: Tuan Nguyen$ 
 **/
public class CrawlerApplicationPlugin implements ApplicationPlugin {
  static CrawlerApplicationPlugin instance ;

  private SwingApplication app ;
  private CrawlerPane      crawlerPane ;

  public void onInit(SwingApplication app) {
    this.app = app ;
    initJMenu(app) ;
    crawlerPane = new CrawlerPane() ;
    ApplicationFrame appFrame = new ApplicationFrame("Crawler", crawlerPane) ;
    appFrame.setClosable(false) ;
    app.addFrame(appFrame) ;
    instance = this ;
  }

  private void initJMenu(SwingApplication app) {
    JMenuBarExt menuBar = app.getJMenuBar() ;
  }

  public CrawlerPane getCrawlerPane() { return crawlerPane ; }

  public void onDestroy(SwingApplication app) {
  }

  public void startCrawler(boolean startCrawl) throws Exception {
    CrawlerContext.getInstance().startCrawlerServiceWithConfig(startCrawl, null) ;
  }

  static public CrawlerApplicationPlugin getInstance() { return instance ; }

  public static void main(String[] args) {
    javax.swing.SwingUtilities.invokeLater(new Runnable() {
      public void run() {
        //Make sure we have nice window decorations.
        JFrame.setDefaultLookAndFeelDecorated(true);
        //Create and set up the window.
        SwingApplication application = new SwingApplication();
        application.addPlugin(new JMSApplicationPlugin()) ;
        application.addPlugin(new CrawlerApplicationPlugin()) ;
        application.onInit() ;
        application.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        //Display the window.
        application.setVisible(true);
      }
    });
  }
}