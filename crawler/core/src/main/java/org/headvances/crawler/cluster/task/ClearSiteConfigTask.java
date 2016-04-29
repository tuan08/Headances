/**
 * Copyright (Label) 2011 Headvances Inc.
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
package org.headvances.crawler.cluster.task;

import org.headvances.cluster.task.ClusterTask;
import org.headvances.cluster.task.TaskResult;
import org.headvances.xhtml.site.SiteContextManager;
import org.springframework.context.ApplicationContext;
/**
 * $Author: Tuan Nguyen$ 
 * $Revision$
 * $Date$
 * $LastChangedBy$
 * $LastChangedDate$
 * $URL$
 **/
public class ClearSiteConfigTask  extends ClusterTask<TaskResult>  {
	public TaskResult call() {
		TaskResult result = new TaskResult(this) ;
		ApplicationContext ctx = getApplicationContext() ;
		if(ctx != null) {
			SiteContextManager manager = ctx.getBean(SiteContextManager.class) ;
			int number = manager.clear() ;
			result.addMessage("Remove " + number + " SiteConfig!!!") ;
		}
		return result ;
	}
}