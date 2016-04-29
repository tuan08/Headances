package org.headvances.data;

import java.util.Date;
/**
 * $Author: Tuan Nguyen$ 
 **/
public class DocumentTest extends Document {
	final static public double LAT = 40.73, LON = -74.1 ;
	
	private GeoLocation   location ;
	private Nested nested ;
	
	public DocumentTest() {} 
	
	public GeoLocation getLocation() { return location; }
	public void        setLocation(GeoLocation location) { this.location = location; }
	
	public Nested getNested() { return nested; }
	public void setNested(Nested nested) { 
		this.nested = nested; 
	}
	
	static public class Nested {
		private String    string ;
		private String    text ;
		private String[]  tag ;
		private int       intValue ;
		private long      longValue ;
		private double    doubleValue ;
		private boolean   booleanValue ;
		
		public Nested() {}
		
		public Nested(boolean init) {
			this.string = "string" ;
			this.text  = "this is a text" ;
			this.tag = new String[] {"tag1", "tag2", "colon:colon"} ;
			
			this.intValue     = 1 ;
			this.longValue    = 1l ;
			this.doubleValue  = 1d ;
			this.booleanValue = true ;
		}
		
		public String  getString() { return this.string ; }
		public void    setString(String data) { this.string = data ; }

		public String  getText() { return this.text ; }
		public void    setText(String data) { this.text = data ; }

		
		public String[] getTag() { return this.tag ; }
		public void     setTag(String[] tag) { this.tag = tag ; }
		
		public int     getIntValue() { return this.intValue ; }
		public void    setIntValue(int value) { this.intValue = value ; }

		public long    getLongValue() { return this.longValue ; }
		public void    setLongValue(long value) { this.longValue = value ; }

		public double  getDoubleValue() { return this.doubleValue ; }
		public void    setDoubleValue(double value) { this.doubleValue = value ; }
		
		public boolean getBooleanValue() { return this.booleanValue ; }
		public void    setBooleanValue(boolean b) { this.booleanValue = b ; }
	}
	
	static public DocumentTest create(int id, String creator, Date time) {
		DocumentTest idoc = new DocumentTest() ;
		idoc.setId(Integer.toString(id)) ;
		idoc.createdDate(time) ;
		idoc.setCreatedBy(creator) ;
		idoc.modifiedDate(time) ;
		idoc.setModifiedBy(creator) ;
		idoc.setTitle("Document " + id + " title") ;
		idoc.setDescription("Document " + id + " description") ;
		idoc.setContent("Document " + id + " content") ;
		idoc.setTags(new String[] {"tag1", "tag2"}) ;
		Entity hello = new Entity() ;
		hello.add("hello", "hello") ;
		hello.add("integer", 1) ;
		hello.add("boolean", true) ;
		hello.add("array", new String[] {"value1", "value2"}) ;
		idoc.addEntity("hello", hello) ;
		idoc.setLocation(new GeoLocation(LAT, LON)) ;
		idoc.setNested(new Nested(true)) ;
		return idoc;
	}
}