package org.headvances.ml.nlp.ent;

import org.headvances.nlp.token.tag.TokenTag;
/**
 * $Author: Tuan Nguyen$ 
 **/
public class EntityTag extends TokenTag {
	final static public String TYPE = "entity" ;
	
	private String entity ;
	private String tagValue ;
	
	public EntityTag(String entity, String tagValue) {
		this.entity = entity ;
		this.tagValue = tagValue ;
	}
	
	public String getEntity() { return this.entity ; }
	
	public String getTagValue() { return this.tagValue ; }
	
	public String getOType() { return TYPE ; }

  public boolean isTypeOf(String type) {
	  return TYPE.equals(type);
  }
  
  public String getInfo() {
		return getOType() + ": {" + entity + ", " + getTagValue() + "}" ;
	}
}