package org.headvances.data;

import java.util.Date;

import org.junit.Test;
/**
 * $Author: Tuan Nguyen$ 
 **/
public class IDocumentJSONSerializerUnitTest {
	
	@Test
	public void test() throws Exception {
		DocumentTest doc = DocumentTest.create(1, "test", new Date()) ;
		String json = Document.JSON_SERIALIZER.toString(doc) ;
		System.out.println(json);
		doc = Document.JSON_SERIALIZER.fromString(json, DocumentTest.class) ;
		System.out.println(Document.JSON_SERIALIZER.toString(doc));
	}
	
}