package org.headvances.analysis.cluster.task;

import org.headvances.analysis.AnalysisHistory;
import org.headvances.analysis.AnalysisServer;
import org.headvances.cluster.task.ClusterTask;
import org.springframework.context.ApplicationContext;

public class GetAnalysisHistoryTask extends ClusterTask<AnalysisHistory> {
  public AnalysisHistory call() {
    ApplicationContext ctx = AnalysisServer.getApplicationContext();
    AnalysisHistory history = (AnalysisHistory) ctx.getBean(AnalysisHistory.class);
    return history;
  }
}
