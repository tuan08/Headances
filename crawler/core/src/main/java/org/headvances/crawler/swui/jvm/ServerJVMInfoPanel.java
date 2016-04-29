package org.headvances.crawler.swui.jvm;

import org.headvances.cluster.ClusterClient;
import org.headvances.cluster.task.GetJVMInfoTask;
import org.headvances.crawler.swui.CrawlerContext;
import org.headvances.crawler.swui.comp.ComponentNode;
import org.headvances.swingui.component.JVMInfoPanel;
import org.headvances.util.jvm.JVMInfo;
/**
 * $Author: Tuan Nguyen$
 **/
public class ServerJVMInfoPanel extends JVMInfoPanel {
  private ComponentNode compNode;

  public ServerJVMInfoPanel(ComponentNode compNode) throws Exception {
    this.compNode = compNode;
    update();
  }

  protected JVMInfo getJVMInfo() throws Exception { 
    GetJVMInfoTask task = new GetJVMInfoTask();
    ClusterClient client = CrawlerContext.getInstance().getCrawlerClient();
    return client.execute(task, compNode.getMember());
  }
}