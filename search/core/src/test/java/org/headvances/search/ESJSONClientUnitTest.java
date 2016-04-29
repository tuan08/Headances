package org.headvances.search;

import static org.elasticsearch.index.query.QueryBuilders.fieldQuery;
import static org.elasticsearch.index.query.QueryBuilders.filteredQuery;
import static org.elasticsearch.index.query.QueryBuilders.rangeQuery;
import static org.elasticsearch.index.query.QueryBuilders.termQuery;
import static org.elasticsearch.index.query.QueryBuilders.wildcardQuery;
import static org.elasticsearch.node.NodeBuilder.nodeBuilder;
import junit.framework.Assert;

import org.codehaus.jackson.JsonNode;
import org.elasticsearch.client.Client;
import org.elasticsearch.index.query.BaseQueryBuilder;
import org.elasticsearch.index.query.FilterBuilders;
import org.elasticsearch.index.query.FilteredQueryBuilder;
import org.elasticsearch.index.query.GeoDistanceFilterBuilder;
import org.elasticsearch.index.query.QueryBuilders;
import org.elasticsearch.index.query.RangeQueryBuilder;
import org.elasticsearch.node.Node;
import org.elasticsearch.node.NodeBuilder;
import org.headvances.json.JSONReader;
import org.headvances.json.JSONSerializer;
import org.headvances.util.FileUtil;
import org.headvances.util.IOUtil;
import org.junit.Before;
import org.junit.Test;
/**
 * $Author: Tuan Nguyen$ 
 **/
public class ESJSONClientUnitTest {
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
		doTest(new ESJSONClient(new String[] { "localhost:9300" }, "sample", "webpage"));
		client.close() ;
		node1.close()   ;
	}
	
	private void doTest(ESJSONClient esclient) throws Exception {
		esclient.createIndex(
		  IOUtil.getResourceAsString("webpage.setting.json", "UTF8"),
		  IOUtil.getResourceAsString("webpage.mapping.json", "UTF8")
		) ;
		
		JSONReader reader = new JSONReader("src/test/resources/sample/sample1.json") ;
		JsonNode node = null ;
		while((node = reader.read()) != null) {
			JsonNode idNode =node.get("id") ;
			String id = null ;
			if(idNode != null) id = idNode.getTextValue() ;
			String json = JSONSerializer.JSON_SERIALIZER.toString(node) ;
			esclient.put(json,id) ;
		}
		reader.close() ;
		
		Thread.sleep(1000) ;
		Assert.assertEquals(2, esclient.count(termQuery("createdBy", "system"))) ;
		
		Assert.assertEquals(1, esclient.count(fieldQuery("title",  "hà nội"))) ;
		Assert.assertEquals(1, esclient.count(fieldQuery("description",  "hà nội"))) ;
		Assert.assertEquals(0, esclient.count(fieldQuery("content",  "hà nội"))) ;
		Assert.assertEquals(0, esclient.count(termQuery("headers.test",  "test"))) ;
		
		Assert.assertEquals(1, esclient.count(termQuery("entities.htmlLink.deep", 1))) ;
		Assert.assertEquals(1, esclient.count(termQuery("entities.htmlLink.url", "vnexpress.net"))) ;

		Assert.assertEquals(1, esclient.count(termQuery("entities.mainContent.title.vn",  "hà.nội"))) ;
		Assert.assertEquals(1, esclient.count(fieldQuery("entities.mainContent.title.vn", "Hà.Nội"))) ;
		Assert.assertEquals(1, esclient.count(wildcardQuery("entities.mainContent.title.vn", "hà*"))) ;
		
		Assert.assertEquals(2, esclient.count(termQuery("entities.primitive.string", "string"))) ;
		Assert.assertEquals(2, esclient.count(termQuery("entities.primitive.tag", "tag1"))) ;
		Assert.assertEquals(2, esclient.count(termQuery("entities.primitive.tag", "tag2"))) ;
		Assert.assertEquals(2, esclient.count(termQuery("entities.primitive.tag", "colon:colon"))) ;
		Assert.assertEquals(0, esclient.count(termQuery("entities.primitive.tag", "colon"))) ;
		
		Assert.assertEquals(2, esclient.count(termQuery("entities.primitive.intValue", 1))) ;
		Assert.assertEquals(2, esclient.count(termQuery("entities.primitive.intValue", "1"))) ;
		
		Assert.assertEquals(2, esclient.count(termQuery("entities.primitive.longValue", 1l))) ;
		Assert.assertEquals(2, esclient.count(termQuery("entities.primitive.longValue", "1"))) ;
		
		Assert.assertEquals(2, esclient.count(termQuery("entities.primitive.doubleValue", 1d))) ;
		Assert.assertEquals(2, esclient.count(termQuery("entities.primitive.doubleValue", "1"))) ;
		
		Assert.assertEquals(2, esclient.count(termQuery("entities.primitive.booleanValue", true))) ;
		Assert.assertEquals(2, esclient.count(termQuery("entities.primitive.booleanValue", "true"))) ;

		Assert.assertEquals(1, esclient.count(termQuery("entities.contact.phone", "0433334444"))) ;
		Assert.assertEquals(1, esclient.count(fieldQuery("entities.contact.phone", "04-3333-4444"))) ;

		Assert.assertEquals(1, esclient.count(termQuery("entities.contact.phone", "0412345678"))) ;
		Assert.assertEquals(1, esclient.count(wildcardQuery("entities.contact.phone", "*12345678"))) ;
		Assert.assertEquals(1, esclient.count(fieldQuery("entities.contact.phone", "04-1234-5678"))) ;
		
		Assert.assertEquals(1, esclient.count(termQuery("entities.contact.addressNumber", 186))) ;
		BaseQueryBuilder addrRangeQuery = 
			rangeQuery("entities.contact.addressNumber").
			from(185).to(187) ;
		Assert.assertEquals(1, esclient.count(addrRangeQuery)) ;
	
		Assert.assertEquals(1, esclient.count(termQuery("entities.product.price", 20000000))) ;
		Assert.assertEquals(1, esclient.count(termQuery("entities.product.price", "20000000"))) ;
		Assert.assertEquals(1, esclient.count(termQuery("entities.product.priceUnit", "vnd"))) ;
		
		RangeQueryBuilder priceRangeQuery = 
			rangeQuery("entities.product.price").from(50000).to(21000000) ;
		Assert.assertEquals(1, esclient.count(priceRangeQuery)) ;
	
		priceRangeQuery = rangeQuery("entities.product.price").from(18000000).to(19000000) ;
		Assert.assertEquals(0, esclient.count(priceRangeQuery)) ;
	
		GeoDistanceFilterBuilder geoFilter = FilterBuilders.geoDistanceFilter("entities.location") ;
		geoFilter.lat(40.73d).lon(-74.1d) ;
		geoFilter.distance("1km") ;
		FilteredQueryBuilder geoQuery = filteredQuery(QueryBuilders.matchAllQuery(), geoFilter);
		Assert.assertEquals(2, esclient.count(geoQuery)) ;
	}
}