package org.headvances.hadoop.batchdb.document;

import java.util.Date;

import org.apache.hadoop.conf.Configuration;
import org.headvances.data.Document;
import org.headvances.hadoop.batchdb.Database;
import org.headvances.hadoop.batchdb.Reporter;
import org.headvances.hadoop.batchdb.Row;
import org.headvances.hadoop.util.HDFSUtil;
import org.headvances.util.FileUtil;
import org.headvances.util.RoundRobinAllocatorAgent;
import org.headvances.util.html.URLNormalizer;
import org.junit.Before;
import org.junit.Test;

public class DocumentDatabaseUnitTest {
	private RoundRobinAllocatorAgent<String> sites ;
	
	@Before
	public void setup() throws Exception {
		FileUtil.removeIfExist("target/htmldb") ;
		String[] siteNames = {
	    "vnexpress.net", "dantri.com.vn", "vietnamnet.vn"
	  } ;
		sites = new RoundRobinAllocatorAgent<String>(siteNames) ;
	}

	@Test
	public void test() throws Exception {
		Configuration conf = HDFSUtil.getDaultConfiguration() ;
		String[] entity = { "primitive", "location" } ;
		Database database = DocumentDBFactory.getDatabase(conf, "target/htmldb", entity, 1) ;
		DocumentMapper mapper = new DocumentMapper() ;
		Database.Writer writer = database.getWriter() ;
		Reporter.LocalReporter reporter = new Reporter.LocalReporter("Database Write") ;
		for(int i = 0; i < 10; i++) {
			Document doc = createDocument(i) ;
			Row row = mapper.toRow(doc) ;
			writer.write(row.getRowId(), row, reporter) ;
		}
		writer.close() ;
		reporter.getStatisticMap().report(System.out) ;
		
		Database.Reader reader = database.getReader() ;
		Row row = null ;
		while((row = reader.next()) != null) {
			Document doc = mapper.fromRow(row) ;
			System.out.println(doc.getId());
		}
		reader.close() ;
		System.out.println("---------------------------------------------------------");
		reader = database.getReader(new String[] { "meta" }) ;
		row = null ;
		while((row = reader.next()) != null) {
			Document doc = mapper.fromRow(row) ;
			System.out.println(doc.getId());
		}
		reader.close() ;
	}
	
	private Document createDocument(int id) {
		Document doc = Document.createSample(1, "test", new Date(), "test database") ;
		String url = sites.next() + "/" + id ;
		doc.setId(new URLNormalizer(url).getHostMD5Id()) ;
		return doc ;
	}
}