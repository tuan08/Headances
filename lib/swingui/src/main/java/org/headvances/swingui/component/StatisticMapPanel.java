package org.headvances.swingui.component;

import javax.swing.JComponent;
import javax.swing.JScrollPane;
import javax.swing.JTabbedPane;

import org.headvances.util.statistic.StatisticsSet;
/**
 * $Author: Tuan Nguyen$ 
 **/
public class StatisticMapPanel extends UpdatableJPanel {
	private JTabbedPane   tabbedPane;

	protected JComponent createBodyPane() {
	  tabbedPane = new JTabbedPane();
	  return tabbedPane ;
	}
	
	protected void update() throws Exception {
		populate(getStatisticMap()) ;
	}
	
	protected StatisticsSet getStatisticMap() throws Exception { return null ; }
	
	public void populate(StatisticsSet statisticMap) {
		tabbedPane.removeAll() ;
		for(String selCategory : statisticMap.getCategories()) {
			StatisticTab tab = new StatisticTab(statisticMap, selCategory) ;
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