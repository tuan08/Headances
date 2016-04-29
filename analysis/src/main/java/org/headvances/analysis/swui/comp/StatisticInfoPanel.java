package org.headvances.analysis.swui.comp;

import javax.swing.JComponent;
import javax.swing.JScrollPane;
import javax.swing.JTabbedPane;

import org.headvances.analysis.cluster.task.GetStatisticInfoTask;
import org.headvances.analysis.statistic.StatisticInfo;
import org.headvances.analysis.swui.AnalysisContext;
import org.headvances.analysis.swui.ComponentNode;
import org.headvances.analysis.swui.ComponentPanel;
import org.headvances.cluster.ClusterClient;
import org.headvances.swingui.component.TableView;
import org.headvances.util.statistic.StatisticsSet;

/**
 * $Author: Tuan Nguyen$ 
 **/
public class StatisticInfoPanel extends ComponentPanel {
  
	private ComponentNode componentNode ;
	private JTabbedPane   tabbedPane;

	private StatisticInfo info;

	public StatisticInfoPanel(ComponentNode componentNode) throws Exception {
		this.componentNode = componentNode ;
		update() ;
	}
	
	protected JComponent createBodyPane() {
	  tabbedPane = new JTabbedPane();
	  return tabbedPane ;
	}
	
	protected void update() throws Exception {
		GetStatisticInfoTask task = new GetStatisticInfoTask(componentNode.getComponentId()) ;
		ClusterClient client = AnalysisContext.getInstance().getClient() ;
		info = client.execute(task, componentNode.getMember()) ;
		populate(info) ;
	}
	
	public void populate(StatisticInfo info) {
    if(info == null) return ;
    StatisticsSet statisticMap = info.getStatisticMap();
    int idx = 0;
    for(String selCategory : statisticMap.getCategories()) {
    	StatisticTab tab = new StatisticTab(statisticMap, selCategory) ;
    	if(tabbedPane.getComponentCount() == statisticMap.getCategories().length)
    	  tabbedPane.setComponentAt(idx++, new JScrollPane(tab));
    	else
    	  tabbedPane.add(tab.getName(), new JScrollPane(tab));
    }
  }
	
  static public class StatisticTab extends TableView {
  	static String[] HEADER = { "Name", "Value", "Percentage" } ; 
  	
  	public StatisticTab(StatisticsSet map, String category) {
  		setName(category) ;
  		setData(HEADER, map.getStatisticData(category, "desc"));
  		setSortableColumn(new int[]{0, 1, 2});
  	}
  }
}