/**
 * Copyright (C) 2011 Headvances Inc.
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU Affero General Public License as
 * published by the Free Software Foundation, either version 3 of the
 * License, or (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU Affero General Public License for more details.

 * You should have received a copy of the GNU Affero General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/>.
 * 
 * This project aim to build a set of library/data to process 
 * the Vietnamese language and analyze the web data
 **/
package org.headvances.crawler.swui.site;

import java.util.List ;

import org.headvances.swingui.component.JPopupMenuExt ;
import org.headvances.xhtml.site.SiteConfig;
import org.headvances.xhtml.site.SiteContext;

/**
 * $Author: Tuan Nguyen$ 
 **/
public class SiteConfigTable extends AbstractSiteTable {
	public SiteConfigTable() {
		final JPopupMenuExt popup = new SiteContextPopupMenu(this);
		getJTable().addMouseListener(popup.createPopupTriggerListener());
	}
	
	 public void updateRow(int selectRow, SiteContext context) {
	    Object[] data = createSiteConfigRow(context);
	    updateRow(selectRow, data);
	 }

	public void populate(List<SiteContext> contexts) {
		if (contexts == null) return;
		String[] header = { "Site", "Inject URL", "Max Conn", "Crawl Deep", "Refresh Time", "Modify", "Status" };
		Object[][] data = new Object[contexts.size()][] ;
		for (int i = 0; i < contexts.size(); i++) {
			SiteContext context = contexts.get(i) ;
			data[i] = createSiteConfigRow(context) ;
		}
		setData(header, data, 50);
	}
	
	static public Object[] createSiteConfigRow(SiteContext context) {
    SiteConfig config = context.getSiteConfig();
    Object[] row = {
        config.getHostname(), 
        config.getInjectUrl()[0],
        config.getMaxConnection(), 
        config.getCrawlDeep(),
        config.getRefreshPeriod(), 
        context.getModify(),
        config.getStatus()
    } ;
    return row ;
  }
}