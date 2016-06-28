package org.headvances.hadoop.batchdb.document;

import junit.framework.Assert;

import org.headvances.data.Document;
import org.headvances.data.Entity;
import org.headvances.hadoop.batchdb.Row;
import org.junit.Test;

public class DocumentMapperUnitTest {
  @Test
  public void test() throws Exception {
    Document idoc1 = new Document() ;
    //Meta column
    idoc1.setId("id") ;
    idoc1.setCreatedDate(1l) ;
    idoc1.setCreatedBy("abc") ;
    idoc1.setModifiedDate(1l) ;
    idoc1.setModifiedBy("xyz") ;
    idoc1.setLabels(new String[] {"label1", "label2"});
    idoc1.setTags(new String[] {"tag1", "tag2"}) ;
    idoc1.setContentType("html");

    //Data column
    idoc1.setTitle("Title") ;
    idoc1.setDescription("Description") ;
    idoc1.setContent("Content") ;

    //Entity column

    Entity primitiveEntity1 = new Entity();
    primitiveEntity1.add("string", "string");
    primitiveEntity1.add("text", "this is a sample text");
    primitiveEntity1.add("array", new String[]{"element1", "element2", "tag:person"});
    primitiveEntity1.add("integer", 4);
    primitiveEntity1.add("float", 3.23);
    primitiveEntity1.add("long", 1l);
    primitiveEntity1.add("double", 3d);
    primitiveEntity1.add("boolean", true);
    idoc1.addEntity("PrimitiveEntity", primitiveEntity1);

    DocumentMapper mapper = new DocumentMapper() ;

    Row row = mapper.toRow(idoc1) ;

    Document idoc2 = mapper.fromRow(row) ;

    // Meta 
    Assert.assertEquals(idoc1.getId(), idoc2.getId()) ;
    Assert.assertEquals(idoc1.getCreatedDate(), idoc2.getCreatedDate()) ;
    Assert.assertEquals(idoc1.getCreatedBy(), idoc2.getCreatedBy()) ;
    Assert.assertEquals(idoc1.getModifiedDate(), idoc2.getModifiedDate()) ;
    Assert.assertEquals(idoc1.getModifiedBy(), idoc2.getModifiedBy()) ;
    Assert.assertEquals(idoc1.getContentType(), idoc2.getContentType());

    String[] labels1 = idoc1.getLabels();
    String[] labels2 = idoc2.getLabels();
    for(int i = 0; i < labels1.length; i++)
      Assert.assertEquals(labels1[i], labels2[i]);

    String[] tags1 = idoc1.getTags();
    String[] tags2 = idoc2.getTags();
    for(int i = 0; i < labels1.length; i++)
      Assert.assertEquals(tags1[i], tags2[i]);

    // Data
    Assert.assertEquals(idoc1.getTitle(), idoc2.getTitle()) ;
    Assert.assertEquals(idoc1.getDescription(), idoc2.getDescription()) ;
    Assert.assertEquals(idoc1.getContent(), idoc2.getContent()) ;
    //Entity
    Entity primitiveEntity2 = idoc2.getEntity("PrimitiveEntity");
    Assert.assertEquals(primitiveEntity1.getString("string"), primitiveEntity2.getString("string")) ;
    Assert.assertEquals(primitiveEntity1.getString("text"), primitiveEntity2.getString("text")) ;

    //Array assert !!!!!   
    String[] array1 = primitiveEntity1.getStringArray("array");
    String[] array2 = primitiveEntity2.getStringArray("array");
    for(int i = 0; i < array1.length; i++){
      Assert.assertEquals(array1[i], array2[i]);
    }

    Assert.assertEquals(primitiveEntity1.getInteger("integer"), primitiveEntity2.getInteger("integer")) ;
    Assert.assertEquals(primitiveEntity1.getFloat("float"), primitiveEntity2.getFloat("float")) ;
    Assert.assertEquals(primitiveEntity1.getLong("long"), primitiveEntity2.getLong("long")) ;
    Assert.assertEquals(primitiveEntity1.getDouble("double"), primitiveEntity2.getDouble("double")) ;
    Assert.assertEquals(primitiveEntity1.getBoolean("boolean"), primitiveEntity2.getBoolean("boolean")) ;
  }
}