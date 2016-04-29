package org.headvances.ml.nlp.chunk;

import org.headvances.nlp.token.tag.TokenTag;
/**
 * $Author: Tuan Nguyen$ 
 **/
public class ChunkTag extends TokenTag {
	final static public String TYPE = "chunk" ;
	
	private String chunk ;
	private String tagValue ;
	
	public ChunkTag(String chunk, String tagValue) {
		this.chunk = chunk ;
		this.tagValue = tagValue ;
	}
	
	public String getChunk() { return this.chunk ; }
	
	public String getTagValue() { return this.tagValue ; }
	
	public String getOType() { return TYPE ; }

  public boolean isTypeOf(String type) {
	  return TYPE.equals(type);
  }
  
  public String getInfo() {
		return getOType() + ": {" + chunk + ", " + getTagValue() + "}" ;
	}
}