package org.headvances.search.rest.analyse;

/**
 * $Author: Tuan Nguyen$ 
 **/
public class Request {
	private String mode ;
	private String entities ;
	private String algorithm ;
	private String show ;
	private String text ;
	
	public String getMode() { return mode; }
	public void setMode(String mode) { this.mode = mode; }
	
	public String getEntities() { return this.entities ; }
	public void   setEntities(String words) { this.entities = words ; }
	
	public String getAlgorithm() { return this.algorithm ; }
	public void   setAlgorithm(String algorithm) { this.algorithm = algorithm ; }
	
	public String getShow() { return this.show ; }
	public void   setShow(String show) { this.show  = show; }
	
	public String getText() { return text; }
	public void setText(String text) { this.text = text; }
}