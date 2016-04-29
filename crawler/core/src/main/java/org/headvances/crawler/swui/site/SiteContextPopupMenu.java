package org.headvances.crawler.swui.site;

import java.awt.event.ActionEvent;
import java.util.List;

import javax.swing.AbstractAction;
import javax.swing.JFrame;

import org.headvances.crawler.swui.CrawlerApplicationPlugin;
import org.headvances.crawler.swui.WorkingWorkspace;
import org.headvances.swingui.SwingUtil;
import org.headvances.swingui.component.JPopupMenuExt;
import org.headvances.xhtml.site.SiteConfig;
import org.headvances.xhtml.site.SiteContext;
import org.headvances.xhtml.site.SiteContext.Modify;
/**
 * $Author: Tuan Nguyen$ 
 **/
public class SiteContextPopupMenu extends JPopupMenuExt {
	private AbstractSiteTable table  ;
	
	public SiteContextPopupMenu(AbstractSiteTable tbl) {
		this.table = tbl ;
		for(final Modify selModify : Modify.values()) {
			registerMenuAction("Change Modify", new AbstractAction(selModify.toString()) {
				public void actionPerformed(ActionEvent e) {
					int modelSelectRow = table.getModelSelectedRow();
					SiteContextPanel scp = SwingUtil.findAncestorOfType(table, SiteContextPanel.class) ;
					List<SiteContext> contexts = 
						scp.getSiteContextFilterSet().getFilteredSiteContexts() ;
					SiteContext selectContext = contexts.get(modelSelectRow);
					selectContext.setModify(selModify);
					scp.updateRow(modelSelectRow, selectContext);
					scp.getStatusBar().setStatus("Change Modify", "Change " + selectContext.getSiteConfig().getHostname() 
					                                                        + "'s Modify to "+selModify.name() + " Done!");
				}
			}) ;
		}
		for(final String selStatus : SiteConfig.STATUS_ALL) {
      registerMenuAction("Change Status", new AbstractAction(selStatus.toString()) {
        public void actionPerformed(ActionEvent e) {
          int modelSelectRow = table.getModelSelectedRow();
          SiteContextPanel scp = SwingUtil.findAncestorOfType(table, SiteContextPanel.class) ;
          List<SiteContext> contexts = 
            scp.getSiteContextFilterSet().getFilteredSiteContexts() ;
          SiteContext selectContext = contexts.get(modelSelectRow);
          selectContext.getSiteConfig().setStatus(selStatus);
          selectContext.setModify(Modify.MODIFIED);
          scp.updateRow(modelSelectRow, selectContext);
          scp.getStatusBar().setStatus("Change Status", "Change " + selectContext.getSiteConfig().getHostname() 
                                                                  + "'s Status to "+selStatus + " Done!");
        }
      }) ;
    }
		add(new AbstractAction("Edit") {
			public void actionPerformed(ActionEvent e) {
				int modelSelectRow = table.getModelSelectedRow();
				SiteContextPanel scp = 
					SwingUtil.findAncestorOfType(table, SiteContextPanel.class) ;
				List<SiteContext> contexts = 
					scp.getSiteContextFilterSet().getFilteredSiteContexts() ;
				SiteContext selectContext = contexts.get(modelSelectRow);
				SiteContextEditor contextEditor = 
					new SiteContextEditor(new JFrame(), scp, modelSelectRow, selectContext);
			}
		}) ;
		add(new AbstractAction("New") {
			public void actionPerformed(ActionEvent e) {
				SiteContextPanel scp = 
					SwingUtil.findAncestorOfType(table, SiteContextPanel.class) ;
				SiteContextEditor contextEditor = 
					new SiteContextEditor(new JFrame(), scp, -1, new SiteContext(new SiteConfig()));
			}
		}) ;
		add(new AbstractAction("Delete") {
			public void actionPerformed(ActionEvent e) {
				int modelSelectRow = table.getModelSelectedRow();
				SiteContextPanel scp = 
					SwingUtil.findAncestorOfType(table, SiteContextPanel.class) ;
				List<SiteContext> contexts = 
					scp.getSiteContextFilterSet().getFilteredSiteContexts() ;
				SiteContext selectContext = contexts.get(modelSelectRow);
				selectContext.setModify(Modify.DELETE);
				scp.updateRow(modelSelectRow, selectContext);
        scp.getStatusBar().setStatus("Delete", selectContext.getSiteConfig().getHostname() + " is deleted!");
			}
		}) ;
		add(new AbstractAction("Site Info Fetcher") {
      public void actionPerformed(ActionEvent e) {
        WorkingWorkspace ws = 
          CrawlerApplicationPlugin.getInstance().getCrawlerPane().getWorkingWorkspace() ;
        int[] selectedRow = table.getJTable().getSelectedRows() ;
        for(int i = 0; i < selectedRow.length; i++) {
          selectedRow[i] = table.getJTable().convertRowIndexToModel(selectedRow[i]) ;
        }
        SiteContext[] selectedContext = new SiteContext[selectedRow.length] ;
        
        SiteContextPanel scp = 
          SwingUtil.findAncestorOfType(table, SiteContextPanel.class) ;
        List<SiteContext> contexts = 
          scp.getSiteContextFilterSet().getFilteredSiteContexts() ;
        for(int i = 0; i < selectedRow.length; i++) {
          selectedContext[i] = contexts.get(selectedRow[i]);
        }
        SiteFetchInfoPanel view;
        try {
          view = new SiteFetchInfoPanel(selectedRow, selectedContext);
          ws.addTabView("Site Info Fetcher", view) ;
          scp.getStatusBar().setStatus("Viewing " + selectedRow.length + " Site Contexts", "");
        } catch (Exception exp) {
          exp.printStackTrace();
        }
      }
    }) ;
		table.getJTable().addMouseListener(createPopupTriggerListener());
	}
}
