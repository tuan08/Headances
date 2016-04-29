package org.headvances.analysis.integration;

import java.io.IOException;
import java.io.PrintStream;

import org.headvances.data.Document;
import org.headvances.data.Entity;
import org.headvances.data.HtmlDocumentUtil;
import org.headvances.util.ConsoleUtil;
import org.headvances.util.text.StringUtil;
/**
 * $Author: Tuan Nguyen$ 
 **/
public class ConsoleOutputAdapter  {
	private boolean enable = true;
	private PrintStream out ;
	
	public ConsoleOutputAdapter() throws IOException {
		out = ConsoleUtil.getUTF8SuportOutput() ;
	}
	
	public void setEnable(boolean b) { this.enable = b ; }
	
	public void write(Document hdoc) {
		if(!enable) return ;
		String anchorText = HtmlDocumentUtil.getHtmlLink(hdoc).getString("anchorText") ;
		String extractTitle = "" ;
		Entity icontent = hdoc.getEntity("icontent") ;
		if(icontent != null) {
			extractTitle = icontent.get("title") ;
		}
		String tname = Thread.currentThread().getName() ;
		out.println("ConsoleOutputAdapter[" + tname + "]: " + anchorText) ;
		out.println("  Extract Title: " + extractTitle) ;
		out.println("           Tags: " + StringUtil.joinStringArray(hdoc.getTags())) ;
		out.println("         Labels: " + StringUtil.joinStringArray(hdoc.getLabels())) ;
	}
}