package org.headvances.crawler.swui.site;

import java.util.List;

import org.headvances.swingui.component.JPopupMenuExt;
import org.headvances.xhtml.site.SiteConfig;
import org.headvances.xhtml.site.SiteContext;
import org.headvances.xhtml.site.SiteScheduleStat;

public class SiteProcessTable extends AbstractSiteTable {
  public SiteProcessTable() {
    final JPopupMenuExt popup = new SiteContextPopupMenu(this);
    getJTable().addMouseListener(popup.createPopupTriggerListener());
  }

  public void populate(List<SiteContext> contexts) {
    if (contexts == null) return;
    String[] header = { "Site", "Schedule", "Process" };
    Object[][] data = new Object[contexts.size()][];
    for (int i = 0; i < contexts.size(); i++) {
      SiteContext context = contexts.get(i);
      SiteConfig config = context.getSiteConfig() ;
      SiteScheduleStat scheduleStat = context.getAttribute(SiteScheduleStat.class, false) ;
      data[i] = new Object[] {
          config.getHostname(),
          scheduleStat.getScheduleCount(),
          scheduleStat.getProcessCount()
      };
    }
    setData(header, data, 50);
  }
}
