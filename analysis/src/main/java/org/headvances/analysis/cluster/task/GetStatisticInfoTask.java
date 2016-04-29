package org.headvances.analysis.cluster.task;

import org.headvances.analysis.statistic.StatisticInfo;
import org.headvances.cluster.task.ClusterTask;
import org.springframework.context.ApplicationContext;

public class GetStatisticInfoTask extends ClusterTask<StatisticInfo>{
  private String componentId ;
  
  public GetStatisticInfoTask(){}
  
  public GetStatisticInfoTask(String componentId){
    this.componentId = componentId;
  }
  
  public StatisticInfo call() throws Exception{
    StatisticInfo info = null;
    ApplicationContext ctx = getApplicationContext() ;
    if(ctx != null) {
      info = (StatisticInfo) ctx.getBean(componentId) ;
    }
    return info ;
    
  }
  

}
