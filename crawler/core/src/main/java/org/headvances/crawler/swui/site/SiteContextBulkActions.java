package org.headvances.crawler.swui.site;

import java.awt.event.ActionEvent;
import java.util.List;

import javax.swing.AbstractAction;

import org.headvances.swingui.SwingUtil;
import org.headvances.swingui.component.DropDownButton;
import org.headvances.util.text.DateUtil;
import org.headvances.xhtml.site.SiteConfig;
import org.headvances.xhtml.site.SiteContext;
import org.headvances.xhtml.site.SiteContextFilterSet;
import org.headvances.xhtml.site.SiteContext.Modify;
/**
 * $Author: Tuan Nguyen$ 
 **/
public class SiteContextBulkActions extends DropDownButton {
	public SiteContextBulkActions() {
		super("Bulk");
		for(int i = 1; i <= 5; i++) {
			final int maxConn = i ;
			registerMenuAction("Max Connection", new AbstractAction(Integer.toString(maxConn)) {
				public void actionPerformed(ActionEvent e) {
					SiteContextPanel scp = 
							SwingUtil.findAncestorOfType(SiteContextBulkActions.this, SiteContextPanel.class) ;
          SiteContextFilterSet filter = scp.getSiteContextFilterSet();
          List<SiteContext> contexts = filter.getFilteredSiteContexts() ;
          for(SiteContext context : contexts) {
            SiteConfig config = context.getSiteConfig();
            config.setMaxConnection(maxConn);
            context.setModify(Modify.MODIFIED);
          }
          scp.onUpdateSiteContextFilterSet(filter);
				}
			}) ;
		}
		
		for(int i = 1; i <= 10; i++) {
      final int crwlDeep = i ;
			registerMenuAction("Crawl Deep", new AbstractAction(Integer.toString(crwlDeep)) {
        public void actionPerformed(ActionEvent e) {
          SiteContextPanel scp = SwingUtil.findAncestorOfType(SiteContextBulkActions.this, SiteContextPanel.class) ;
          SiteContextFilterSet filter = scp.getSiteContextFilterSet();
          List<SiteContext> contexts = filter.getFilteredSiteContexts() ;
          for(SiteContext context : contexts) {
            SiteConfig config = context.getSiteConfig();
            config.setCrawlDeep(crwlDeep);
            context.setModify(Modify.MODIFIED);
          }
          scp.onUpdateSiteContextFilterSet(filter);
        }
      }) ;
		}
		
		for(int i = 0; i < 3; i++) {
		  final int refreshPriod = 2*i + 1 ;
      registerMenuAction("Refresh Time Period", new AbstractAction(refreshPriod+" day") {
        public void actionPerformed(ActionEvent e) {
          SiteContextPanel scp = SwingUtil.findAncestorOfType(SiteContextBulkActions.this, SiteContextPanel.class) ;
          SiteContextFilterSet filter = scp.getSiteContextFilterSet() ;
          List<SiteContext> contexts = filter.getFilteredSiteContexts() ;
          for(SiteContext context : contexts) {
            SiteConfig config = context.getSiteConfig();
            config.setRefreshPeriod(refreshPriod * DateUtil.SECONDS_PER_DAY);
            context.setModify(Modify.MODIFIED);
          }
          scp.onUpdateSiteContextFilterSet(filter);
        }
      }) ;
		}
		
		for(Modify sel : Modify.values()) {
		  final Modify modidfy = sel ;
      registerMenuAction("Modify", new AbstractAction(modidfy.name()) {
        public void actionPerformed(ActionEvent e) {
          SiteContextPanel scp = SwingUtil.findAncestorOfType(SiteContextBulkActions.this, SiteContextPanel.class) ;
          SiteContextFilterSet filter = scp.getSiteContextFilterSet();
          List<SiteContext> contexts = filter.getFilteredSiteContexts();
          for(SiteContext context : contexts) {
            context.setModify(modidfy);
          }
          scp.onUpdateSiteContextFilterSet(filter);
        }
      }) ;
		}
		
		for(String sel : SiteConfig.STATUS_ALL) {
		  final String status = sel ;
      registerMenuAction("Status", new AbstractAction(status) {
        public void actionPerformed(ActionEvent e) {
          SiteContextPanel scp = SwingUtil.findAncestorOfType(SiteContextBulkActions.this, SiteContextPanel.class) ;
          SiteContextFilterSet filter = scp.getSiteContextFilterSet();
          List<SiteContext> contexts = filter.getFilteredSiteContexts() ;
          System.out.println(contexts.size());
          for(SiteContext context : contexts) {
            SiteConfig config = context.getSiteConfig();
            config.setStatus(status);
            context.setModify(Modify.MODIFIED);
          }
          scp.onUpdateSiteContextFilterSet(filter);
        }
      }) ;
		}
	}
}