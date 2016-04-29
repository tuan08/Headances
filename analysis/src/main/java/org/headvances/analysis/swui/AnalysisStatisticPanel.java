package org.headvances.analysis.swui;

import org.headvances.analysis.cluster.task.GetStatisticInfoTask;
import org.headvances.cluster.ClusterClient;
import org.headvances.swingui.component.StatisticMapPanel;
import org.headvances.util.statistic.StatisticsSet;
/**
 * $Author: Tuan Nguyen$ 
 **/
public class AnalysisStatisticPanel extends StatisticMapPanel {
	private ComponentNode componentNode ;

	public AnalysisStatisticPanel(ComponentNode componentNode) throws Exception {
		this.componentNode = componentNode ;
		update();
	}

	protected StatisticsSet getStatisticMap() throws Exception { 
		GetStatisticInfoTask task = new GetStatisticInfoTask(componentNode.getComponentId()) ;
		ClusterClient client = AnalysisContext.getInstance().getClient() ;
		return client.execute(task, componentNode.getMember()).getStatisticMap() ;
	}
}