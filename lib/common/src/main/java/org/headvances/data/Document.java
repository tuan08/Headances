package org.headvances.data;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.headvances.json.JSONSerializer;
import org.headvances.util.text.StringUtil;
/**
 * $Author: Tuan Nguyen$ 
 **/
public class Document implements Serializable {
	final static public JSONSerializer JSON_SERIALIZER = new JSONSerializer() ;
	
	private String   id ;
	private long     createdDate  ;
	private String   createdBy ;
	private long     modifiedDate ;
	private String   modifiedBy ;
	private String[] label ;
	private String[] tag ;
	private String   contentType ;
	
	private String   title ;
	private String   description ;
	private String   content ;
	
	private Map<String, Entity> entities ;
	
	public String getId() { return id ; }
	public void   setId(String id) { this.id = id; }
	
	public long getCreatedDate() { return createdDate; }
	public void setCreatedDate(long createdDate) { this.createdDate = createdDate; }
	public void createdDate(Date createdDate) { this.createdDate = createdDate.getTime(); }
	
	public String getCreatedBy() { return createdBy ; }
	public void   setCreatedBy(String s) { this.createdBy = s ; }
	
	public long getModifiedDate() { return modifiedDate; }
	public void setModifiedDate(long modifiedDate) { this.modifiedDate = modifiedDate; }
	public void modifiedDate(Date modifiedDate) { this.modifiedDate = modifiedDate.getTime(); }

	public String getModifiedBy() { return modifiedBy ; }
	public void   setModifiedBy(String s) { this.modifiedBy = s ; }
	
	public String getTitle() { return title; }
	public void   setTitle(String title) { this.title = title; }
	
	public String getDescription() { return description; }
	public void   setDescription(String description) { this.description = description; }
	
	public String getContentType() { return this.contentType ; }
	public void   setContentType(String type) { this.contentType = type ; }
	
	public String getContent() { return content; }
	public void   setContent(String content) { this.content = content; }
	
	public void addLabel(String label) {
  	this.label = StringUtil.merge(this.label, label) ;
  }
	
	public void addLabel(String[] label) {
  	this.label = StringUtil.merge(this.label, label) ;
  }
	
	public void addLabel(String prefix, String[] label) {
  	if(label == null) return ;
  	String[] newLabel = new String[label.length] ;
  	for(int i = 0; i < label.length; i++) newLabel[i] = prefix +  label[i] ;
		this.label = StringUtil.merge(this.label, newLabel) ;
  }
	
  public boolean hasLabel(String label) {
  	return StringUtil.isIn(label, this.label) ;
  }
  public String[] getLabels() { return label ; }
  public void     setLabels(String[] label) {
  	this.label = label ;
  }
	
	public void addTag(String tag) {
  	this.tag = StringUtil.merge(this.tag, tag) ;
  }
	
	public void addTag(String[] tag) {
  	this.tag = StringUtil.merge(this.tag, tag) ;
  }
	
	public void addTag(String prefix, String[] tag) {
  	if(tag == null) return ;
  	String[] newTag = new String[tag.length] ;
  	for(int i = 0; i < tag.length; i++) newTag[i] = prefix +  tag[i] ;
		this.tag = StringUtil.merge(this.tag, newTag) ;
  }
	
  public boolean hasTag(String tag) { return StringUtil.isIn(tag, this.tag) ; }
  
  public String[] getTags() { return tag ; }
  public void     setTags(String[] tag) { this.tag = tag ; }
  
  public String[] getTagWithPrefix(String prefix) { 
  	if(tag == null) return null ;
  	List<String> holder = null ;
  	for(String sel : tag) {
  		if(sel.startsWith(prefix)) {
  			if(holder == null) holder = new ArrayList<String>() ;
  			holder.add(sel) ;
  		}
  	}
  	if(holder == null) return StringUtil.EMPTY_ARRAY ;
  	return holder.toArray(new String[holder.size()]) ; 
  }
  
  public void addEntity(String key, Entity entity) {
  	if(entities == null) entities = new HashMap<String, Entity>(5) ;
  	entities.put(key, entity) ;
  }
  
  public void addEntity(Object keyObj, Entity entity) {
  	String key = keyObj.toString() ;
  	if(entities == null) entities = new HashMap<String, Entity>(5) ;
  	entities.put(key, entity) ;
  }
  
  public Entity getEntity(String key) {
  	if(entities == null) return null ;
  	return entities.get(key) ;
  }
  
  public Entity getEntity(Object key) {
  	if(entities == null) return null ;
  	return entities.get(key.toString()) ;
  }
  
  public Map<String, Entity> getEntities() { return this.entities ; }
  public void setEntities(Map<String, Entity> entities) { this.entities = entities ; }
  
	public void copy(Document other) {
		id = other.id;
		createdDate = other.createdDate  ;
		createdBy = other.createdBy ;
		modifiedDate = other.modifiedDate ;
		modifiedBy = other.modifiedBy ;
		
		title = other.title ;
		description = other.description ;
		contentType = other.contentType ;
		content = other.content ;
		tag = other.tag ;
		if(other.entities != null) {
			entities = new HashMap<String, Entity>(other.entities) ;
		} else {
			entities = null ;
		}
	}
	
	
	static public Document createSample(int id, String creator, Date time, String text) {
		Document idoc = new Document() ;
		idoc.setId(Integer.toString(id)) ;
		idoc.createdDate(time) ;
		idoc.setCreatedBy(creator) ;
		idoc.modifiedDate(time) ;
		idoc.setModifiedBy(creator) ;
		idoc.setTitle("Title: " + text + " " + id) ;
		idoc.setDescription("Description: " + text + " " + id) ;
		idoc.setContent("Content: " + text + " " + id) ;
		idoc.setTags(new String[] {"tag1", "tag2", "colon:colon"}) ;
		
		Entity primitive = new Entity() ;
		primitive.add("string",  "string") ;
		primitive.add("text",    "this is a text") ;
		primitive.add("tag",     new String[] {"tag1", "tag2", "colon:colon"}) ;
		primitive.add("intValue",     1) ;
		primitive.add("longValue",    1l) ;
		primitive.add("doubleValue",  1d) ;
		primitive.add("booleanValue", true) ;
		idoc.addEntity("primitive", primitive) ;

		Entity location = new Entity() ;
		location.add("lat",  40.73d) ;
		location.add("lon",  -74.1d) ;
		idoc.addEntity("location", location) ;
		return idoc;
	}
}