package org.jsoup.parser;

import java.io.BufferedInputStream;
import java.io.ByteArrayOutputStream;
import java.io.FileInputStream;
import java.io.IOException;

import org.jsoup.nodes.Document;
/**
 * $Author: Tuan Nguyen$ 
 **/
public class Debug {
	static public String getFileContent(String file) throws IOException {
    BufferedInputStream buffer = new BufferedInputStream(new FileInputStream(file));    
    ByteArrayOutputStream output = new ByteArrayOutputStream();
    byte[] data  = new byte[4912];      
    int available = -1;
    while((available = buffer.read(data)) > -1){
      output.write(data, 0, available);
    }   
    buffer.close() ;
    byte[] buf = output.toByteArray();
    return new String(buf, "UTF-8") ;
  }
	
	static public void main(String[] args) throws Exception {
		String xhtml = getFileContent("src/test/resources/problem.xhtml.orig") ;
		//xhtml = xhtml.replace('\0', '\r');
		TreeBuilder treeBuilder = new HtmlTreeBuilder();
		Document doc = treeBuilder.parse(xhtml, "");
		System.out.println("pass.............");
	}
}
