package org.headvances.crawler.swui.site;

import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import java.util.List;

import org.headvances.swingui.DesktopUtil;
import org.headvances.swingui.SwingUtil;
import org.headvances.swingui.component.JTablePagination;
import org.headvances.xhtml.site.SiteContext;

/**
 * $Author: Tuan Nguyen$ 
 **/
abstract public class AbstractSiteTable extends JTablePagination {
	
	public AbstractSiteTable() {
		getJTable().addKeyListener(new KeyAdapter() {
			public void keyPressed(KeyEvent e) {
				if(e.isControlDown() && e.getKeyCode() == KeyEvent.VK_O) {
					int modelSelectRow = getModelSelectedRow();
					SiteContextPanel scp = 
						SwingUtil.findAncestorOfType(AbstractSiteTable.this, SiteContextPanel.class) ;
					List<SiteContext> contexts = 
						scp.getSiteContextFilterSet().getFilteredSiteContexts() ;
					SiteContext selectContext = contexts.get(modelSelectRow);
					String url = selectContext.getSiteConfig().getInjectUrl()[0] ;
					DesktopUtil.openBrowser(AbstractSiteTable.this, url) ;
				}
			}
		}) ;
	}
	
	abstract public void populate(List<SiteContext> contexts) ;
	
  public void updateRow(int selectRow, SiteContext context) {
    throw new RuntimeException("This method need to be implemented!!!");
  }
}
