package org.headvances.ml.swingui.classify;

/**
 * $Author: Tuan Nguyen$ 
 **/
public class Context {
	private static Context singleton = new Context() ;
	
	private DocumentIO documentIO ;
	
	public DocumentIO getDocumentIO() { return this.documentIO ; }
	
	public void newDocumentIO(String dir) throws Exception {
		this.documentIO = new DocumentIO(dir) ;
	}
	
	static public Context getInstance() { return singleton ; }
}
