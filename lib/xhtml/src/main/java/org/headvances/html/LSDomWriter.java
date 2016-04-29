package org.headvances.html;

import java.io.ByteArrayOutputStream;
import java.io.OutputStream;

import org.headvances.util.text.StringUtil;
import org.w3c.dom.Document;
import org.w3c.dom.Node;
import org.w3c.dom.bootstrap.DOMImplementationRegistry;
import org.w3c.dom.ls.DOMImplementationLS;
import org.w3c.dom.ls.LSOutput;
import org.w3c.dom.ls.LSSerializer;
import org.w3c.dom.ls.LSSerializerFilter;

/**
 * Author : Tuan Nguyen
 *          tuan.nguyen@headvances.com
 * Apr 27, 2010  
 */
public class LSDomWriter {
	private DOMImplementationLS domImpl ;
	private LSSerializerFilter filter ;
	
  public LSDomWriter() throws Exception {
    DOMImplementationRegistry registry = DOMImplementationRegistry.newInstance();
    this.domImpl = (DOMImplementationLS)registry.getDOMImplementation("LS");
  }
  
  public LSDomWriter(LSSerializerFilter filter) throws Exception {
  	this() ;
  	this.filter = filter ;
  }
  
  public void write(OutputStream os, Document doc) throws Exception {
    LSSerializer serializer = domImpl.createLSSerializer();
    if(filter != null) serializer.setFilter(filter) ;
    LSOutput lso = domImpl.createLSOutput();
    lso.setByteStream(os);
    lso.setEncoding("UTF-8");
    serializer.getDomConfig().setParameter("format-pretty-print", Boolean.TRUE) ;
    serializer.write(doc, lso);
  }
  
  public String toXMLString(Document doc) throws Exception {
    ByteArrayOutputStream bos = new ByteArrayOutputStream() ;
  	LSSerializer serializer = domImpl.createLSSerializer();
  	if(filter != null) serializer.setFilter(filter) ;
  	LSOutput lso = domImpl.createLSOutput();
    lso.setByteStream(bos);
    lso.setEncoding("UTF-8");
    serializer.getDomConfig().setParameter("format-pretty-print", Boolean.TRUE) ;
    serializer.write(doc, lso);
    return new String(bos.toByteArray(), "UTF-8") ;
  }
  
  public String toXMLString(Node node) throws Exception {
    ByteArrayOutputStream bos = new ByteArrayOutputStream() ;
  	LSSerializer serializer = domImpl.createLSSerializer();
  	if(filter != null) serializer.setFilter(filter) ;
  	LSOutput lso = domImpl.createLSOutput();
    lso.setByteStream(bos);
    lso.setEncoding("UTF-8");
    serializer.getDomConfig().setParameter("format-pretty-print", Boolean.TRUE) ;
    serializer.write(node, lso);
    return new String(bos.toByteArray(), "UTF-8") ;
  }
  
  static public class NodeNameFilter implements LSSerializerFilter {
  	private String[] skipNodeName ;
  	
  	public NodeNameFilter(String[] skipNodeName) {
  		this.skipNodeName = skipNodeName ;
  	}
  	
  	public int getWhatToShow() { return LSSerializerFilter.SHOW_ALL ; }
  	
    public short acceptNode(Node node) {
    	String name = node.getNodeName() ;
    	if(StringUtil.isIn(name, skipNodeName)) {
    		return LSSerializerFilter.FILTER_REJECT ;
    	}
	    return LSSerializerFilter.FILTER_ACCEPT ;
    }
  }
}
