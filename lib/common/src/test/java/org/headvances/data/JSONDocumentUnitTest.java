package org.headvances.data;

import org.codehaus.jackson.JsonNode;
import org.headvances.json.JSONReader;
import org.headvances.json.JSONWriter;
import org.junit.Test;
/**
 * $Author: Tuan Nguyen$ 
 **/
public class JSONDocumentUnitTest {
	@Test
	public void test() throws Exception {
		JSONReader reader = new JSONReader("src/test/resources/sample/sample1.json") ;
		JSONWriter writer = new JSONWriter(System.out) ;
		JsonNode node = null ;
		while((node = reader.read()) != null) {
			writer.write(node) ;
		}
		reader.close() ;
		reader = new JSONReader("src/test/resources/sample/sample1.json") ;
		DocumentTest doc = null ;
		while((doc = reader.read(DocumentTest.class)) != null) {
			writer.write(doc) ;
		}
	}
}