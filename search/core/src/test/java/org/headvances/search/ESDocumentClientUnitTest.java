package org.headvances.search;

import static org.elasticsearch.index.query.QueryBuilders.filteredQuery;
import static org.elasticsearch.index.query.QueryBuilders.matchAllQuery;
import static org.elasticsearch.index.query.QueryBuilders.termQuery;
import static org.elasticsearch.node.NodeBuilder.nodeBuilder;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import junit.framework.Assert;

import org.elasticsearch.action.search.SearchResponse;
import org.elasticsearch.client.Client;
import org.elasticsearch.index.query.FilterBuilders;
import org.elasticsearch.index.query.FilteredQueryBuilder;
import org.elasticsearch.index.query.GeoDistanceFilterBuilder;
import org.elasticsearch.index.query.RangeFilterBuilder;
import org.elasticsearch.node.Node;
import org.elasticsearch.node.NodeBuilder;
import org.elasticsearch.search.SearchHit;
import org.headvances.data.Document;
import org.headvances.data.Entity;
import org.headvances.util.FileUtil;
import org.headvances.util.IOUtil;
import org.headvances.util.text.DateUtil;
import org.junit.Before;
import org.junit.Test;
/**
 * $Author: Tuan Nguyen$ 
 **/
public class ESDocumentClientUnitTest {
	@Before
	public void setup() throws Exception { 
		FileUtil.removeIfExist("target/data") ; 
	}
	
	@Test
	public void test() throws Exception {
		NodeBuilder nb = nodeBuilder() ;
		nb.getSettings().put("cluster.name", "headvances") ;
		nb.getSettings().put("path.data", "target/data") ;
		Node node1 = nb.node() ;
		Client client = node1.client();
		doTest(new ESDocumentClient<Document>(new String[] { "localhost:9300" }, "document", Document.class));
		client.close() ;
		node1.close()   ;
	}
	
	private void doTest(ESDocumentClient<Document> esclient) throws Exception {
		if(!esclient.hasIndex(esclient.getIndex())) {
			esclient.createIndex(
				esclient.getIndex(),
				IOUtil.getResourceAsString("Document.setting.json", "UTF8")
			) ;
			esclient.updateMapping(IOUtil.getResourceAsString("Document.mapping.json", "UTF8")) ;
		}

		esclient.getClusterState() ;
		Date currTime = new Date() ;
		Date pastTime = DateUtil.parseCompactDate("1/1/2011") ;
		for(int i = 0; i < 10; i++) {
			Date selTime = currTime ;
			if(i % 2 == 0) selTime = pastTime ;
			Document idoc = Document.createSample(i, "system", selTime, "sample data for elasticsearch") ;
			
			esclient.put(idoc) ;
		}
		
		List<Document> holder = new ArrayList<Document>() ;
		for(int i = 10; i < 20; i++) {
			Document idoc = Document.createSample(i, "system", currTime, "sample data for elasticsearch") ;
			holder.add(idoc) ;
		}
		esclient.put(holder) ;
		
		Thread.sleep(1000) ;
		
		Assert.assertEquals(20, esclient.count(termQuery("createdBy", "system"))) ;
		Assert.assertEquals(20, esclient.count(termQuery("content", "content"))) ;
		Assert.assertEquals(20, esclient.count(termQuery("vncontent", "content"))) ;
		
		SearchResponse sres  = esclient.search(termQuery("createdBy", "system"), 0, 10) ;
		Assert.assertEquals(20, sres.hits().getTotalHits()) ;
		Assert.assertEquals(10, sres.hits().hits().length) ;
		SearchHit[] hit = sres.hits().hits() ;
		for(int i = 0; i < 10; i++) {
			System.out.println(new String(hit[i].source()));
			Document idoc = esclient.getIDocument(hit[i]) ;
			Entity location = idoc.getEntities().get("location") ;
			Assert.assertEquals(40.73d, location.get("lat")) ;
			Assert.assertEquals(-74.1d, location.get("lon")) ;
			System.out.println("IDocument: " + idoc.getTitle());
		}
		
		Assert.assertEquals(20, esclient.count(termQuery("entities.primitive.string", "string"))) ;
		Assert.assertEquals(20, esclient.count(termQuery("entities.primitive.tag", "tag1"))) ;
		Assert.assertEquals(20, esclient.count(termQuery("entities.primitive.tag", "tag2"))) ;
		Assert.assertEquals(20, esclient.count(termQuery("entities.primitive.tag", "colon:colon"))) ;
		Assert.assertEquals(0,  esclient.count(termQuery("entities.primitive.tag", "colon"))) ;
		
		Assert.assertEquals(20, esclient.count(termQuery("entities.primitive.intValue", 1))) ;
		Assert.assertEquals(20, esclient.count(termQuery("entities.primitive.intValue", "1"))) ;
		
		Assert.assertEquals(20, esclient.count(termQuery("entities.primitive.longValue", 1l))) ;
		Assert.assertEquals(20, esclient.count(termQuery("entities.primitive.longValue", "1"))) ;
		
		Assert.assertEquals(20, esclient.count(termQuery("entities.primitive.doubleValue", 1d))) ;
		Assert.assertEquals(20, esclient.count(termQuery("entities.primitive.doubleValue", "1"))) ;
		
		Assert.assertEquals(20, esclient.count(termQuery("entities.primitive.booleanValue", true))) ;
		Assert.assertEquals(20, esclient.count(termQuery("entities.primitive.booleanValue", "true"))) ;
		
		GeoDistanceFilterBuilder geoFilter = FilterBuilders.geoDistanceFilter("entities.location") ;
		geoFilter.lat(40.73d).lon(-74.1d) ;
		geoFilter.distance("1km") ;
		FilteredQueryBuilder geoQuery = filteredQuery(matchAllQuery(), geoFilter);
		Assert.assertEquals(20, esclient.count(geoQuery)) ;

		RangeFilterBuilder  numFilter = FilterBuilders.rangeFilter("createdDate");
		numFilter.from(pastTime.getTime()).to(pastTime.getTime() + 1000*60*60*48) ;
		numFilter.includeLower(true).cache(false) ;
		FilteredQueryBuilder numQuery = filteredQuery(matchAllQuery(), numFilter);
		Assert.assertEquals(5, esclient.count(numQuery)) ;

		Assert.assertTrue(esclient.remove("0")) ;
		Thread.sleep(1000) ;
		Assert.assertEquals(19, esclient.count(termQuery("createdBy", "system"))) ;
		
		System.out.println(termQuery("vncontent", "content").toString()) ;
	}
}