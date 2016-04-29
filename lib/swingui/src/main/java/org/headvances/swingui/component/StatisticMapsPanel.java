package org.headvances.swingui.component;

import java.awt.EventQueue;

import javax.swing.JComponent;
import javax.swing.JFrame;
import javax.swing.JTabbedPane;
import javax.swing.WindowConstants;

import org.headvances.util.statistic.StatisticsSet;
/**
 * $Author: Tuan Nguyen$ 
 **/
public class StatisticMapsPanel extends UpdatableJPanel {
	private JTabbedPane   tabbedPane;

	public StatisticMapsPanel() throws Exception {
		update() ;
	}
	
	protected JComponent createBodyPane() {
	  tabbedPane = new JTabbedPane();
	  return tabbedPane ;
	}
	
	protected void update() throws Exception {
		populate(getStatisticMap()) ;
	}
	
	protected StatisticsSet[] getStatisticMap() throws Exception { 
		return new StatisticsSet[0] ; 
	}
	
	public void populate(StatisticsSet[] statisticMap) {
		tabbedPane.removeAll() ;
		for(String selCategory : statisticMap[0].getCategories()) {
			StatisticTab tab = new StatisticTab(statisticMap, selCategory) ;
			tabbedPane.add(tab.getName(), tab);
		}
	}
	
  static public class StatisticTab extends JTablePagination {
  	public StatisticTab(StatisticsSet[] map, String category) {
  		setName(category) ;
  		String[] header = map[0].getCategoryKeys(category) ;
  		Object[][] data = new Object[map.length][] ;
  		for(int i = 0; i < map.length; i++) {
  			data[i] = map[i].getCategoryData(category, header) ;
  		}
  		setData(header, data, 50) ;
  	}
  }
  
  public static void main(String[] args) {
		EventQueue.invokeLater(new Runnable() {
			public void run() {
				final StatisticsSet[] maps = new StatisticsSet[100] ;
				for(int i = 0; i < maps.length; i++) {
					StatisticsSet map = new StatisticsSet() ;
					map.increment("category1", "name1", 2) ;
					map.increment("category1", "name2", 1) ;

					map.incr("category2", "all", 3) ;
					map.incr("category2", "name1", "all", 2) ;
					map.incr("category2", "name2", "all", 1) ;
					map.incr("category2", "name2", "all", 1) ;
					maps[i] = map ;
				}

				JFrame frame = new JFrame();
				frame.setDefaultCloseOperation(WindowConstants.EXIT_ON_CLOSE);
        try {
        	StatisticMapsPanel table = new StatisticMapsPanel() {
        		protected StatisticsSet[] getStatisticMap() throws Exception { 
        			return maps ; 
        		}
        	};
	        frame.getContentPane().add(table);
        } catch (Exception e) {
	        e.printStackTrace();
        }
				frame.setSize(600, 400);
				frame.setLocationRelativeTo(null);
				frame.setVisible(true);
			}
		});
	}
}