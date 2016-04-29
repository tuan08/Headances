package org.headvances.hadoop.batchdb.document;

import java.io.IOException;
import java.util.Iterator;
import java.util.Map;

import org.headvances.data.Document;
import org.headvances.data.Entity;
import org.headvances.hadoop.batchdb.Cell;
import org.headvances.hadoop.batchdb.Row;
import org.headvances.hadoop.batchdb.RowId;
import org.headvances.json.JSONSerializer;
/**
 * $Author: Tuan Nguyen$ 
 **/
public class DocumentMapper {
	final static public String META_COLUMN = "meta" ;
	final static public String DATA_COLUMN = "data" ;
	
	public Row toRow(Document doc) throws IOException {
		Row row = new Row() ;
		RowId rowId = new RowId(doc.getId(), doc.getCreatedDate(), doc.getModifiedDate(), RowId.STORE_STATE) ;
		row.setRowId(rowId) ;
		row.addCell(META_COLUMN, toMetaCell(doc)) ;
		row.addCell(DATA_COLUMN, toDataCell(doc)) ;
		
		Map<String, Entity> entities = doc.getEntities() ;
    Iterator<Map.Entry<String,Entity>> i = entities.entrySet().iterator();
    while(i.hasNext()){
      Map.Entry<String,Entity> entry = i.next() ;
      Cell cell = new Cell() ;
      cell.setKey(rowId) ;
      cell.setName(entry.getKey()) ;
      cell.addField("entity", JSONSerializer.JSON_SERIALIZER.toString(entry.getValue())) ;
      row.addCell(cell.getName(), cell) ;
    }
		return row ;
	}
	
	private Cell toMetaCell(Document doc) throws IOException {
		Cell cell = new Cell() ;
		cell.setName(META_COLUMN) ;
		cell.addField("id", doc.getId()) ;
		cell.addField("createDate", doc.getCreatedDate());
		cell.addField("createBy", doc.getCreatedBy());
		cell.addField("modifiedDate", doc.getModifiedDate());
    cell.addField("modifiedBy", doc.getModifiedBy());
    
    cell.addField("labels", doc.getLabels());
    cell.addField("tags", doc.getTags());
    cell.addField("contentType", doc.getContentType());
		return cell ;
	}
	
	private Cell toDataCell(Document doc) {
		Cell cell = new Cell() ;
		cell.setName(DATA_COLUMN) ;
		cell.addField("title", doc.getTitle()) ;
		cell.addField("description", doc.getDescription()) ;
		cell.addField("content", doc.getContent()) ;
		return cell ;
	}
	
	public Document fromRow(Row row) throws IOException {
		Document doc = new Document() ;
		Iterator<Cell> i = row.values().iterator();
		while(i.hasNext()) {
		  Cell cell = i.next() ;
		  if(META_COLUMN.equals(cell.getName())) {
		    mapMeta(doc, row.getCell(META_COLUMN));
		  } else if(DATA_COLUMN.equals(cell.getName())) {
		    mapData(doc, row.getCell(DATA_COLUMN));
		  } else {
		    mapEntities(doc, cell);
		  }
		}
		return doc ;
	}
	
	public void mapMeta(Document doc, Cell cell) throws IOException {
		if(cell == null) return ;
		doc.setId(cell.getFieldAsString("id")) ;
		doc.setCreatedDate(cell.getFieldAsLong("createDate"));
		doc.setCreatedBy(cell.getFieldAsString("createBy"));
		doc.setModifiedDate(cell.getFieldAsLong("modifiedDate"));
		doc.setModifiedBy(cell.getFieldAsString("modifiedBy"));
		
		doc.setLabels(cell.getFieldAsStringArray("labels"));
		doc.setTags(cell.getFieldAsStringArray("tags"));
		doc.setContentType(cell.getFieldAsString("contentType"));
	}
	
  public void mapData(Document doc, Cell cell) {
		if(cell == null) return ;
	  doc.setTitle(cell.getFieldAsString("title"));
    doc.setDescription(cell.getFieldAsString("description"));
    doc.setContent(cell.getFieldAsString("content"));
	}
  
  public void mapEntities(Document doc, Cell cell) throws IOException {
		if(cell == null) return ;
		String json = cell.getFieldAsString("entity") ;
		Entity entity = JSONSerializer.JSON_SERIALIZER.fromString(json, Entity.class) ;
		doc.addEntity(cell.getName(), entity);
  }
}