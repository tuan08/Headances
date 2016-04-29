package org.headvances.crawler.integration;

import javax.annotation.PostConstruct;
import javax.annotation.PreDestroy;

import org.apache.hadoop.conf.Configuration;
import org.headvances.data.Document;
import org.headvances.data.HtmlDocumentUtil;
import org.headvances.hadoop.batchdb.Database;
import org.headvances.hadoop.batchdb.Row;
import org.headvances.hadoop.batchdb.document.DocumentDBFactory;
import org.headvances.hadoop.batchdb.document.DocumentMapper;
import org.headvances.xhtml.url.HDFSUtil;
/**
 * $Author: Tuan Nguyen$ 
 **/
public class DocumentDBConsumer {
	private String          dblocation ;
	private Database        database ;
	private Database.Writer cwriter ;
	private long            lastCommit ;
	private DocumentMapper  mapper = new DocumentMapper() ;

	public void setDbLocation(String location) {
		this.dblocation = location ;
	}

	@PostConstruct
	public void onInit() throws Exception {
		Configuration conf = HDFSUtil.getDaultConfiguration() ;
		database = 
			DocumentDBFactory.getDatabase(conf, dblocation, HtmlDocumentUtil.ENTITIES, 16) ;
	}

	@PreDestroy
	public void onDestroy() throws Exception {
		if(cwriter != null) cwriter.close() ;
		System.out.println("destroy DocumentDBConsumer!!!!!!!!!!") ;
	}

	synchronized public void consume(Document doc) throws Exception {
		Row row = mapper.toRow(doc) ;
		getCurrentWriter().write(row, null) ;
	}

	private Database.Writer getCurrentWriter() throws Exception {
		long ctime = System.currentTimeMillis() ;
		if(cwriter == null || ctime > (lastCommit + (1 * 60 * 1000))) {
			if(cwriter != null) {
				cwriter.close() ;
				database.autoCompact(null) ;
			}
			cwriter = database.getWriter() ;
			lastCommit = System.currentTimeMillis() ;
		}
		return cwriter ;
	}
}