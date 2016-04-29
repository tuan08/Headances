package org.headvances.analysis.swui;

import org.headvances.cluster.ClusterClient;
import org.headvances.cluster.task.GetJVMInfoTask;
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
    ClusterClient client = AnalysisContext.getInstance().getClient();
    return client.execute(task, compNode.getMember());
  }
}