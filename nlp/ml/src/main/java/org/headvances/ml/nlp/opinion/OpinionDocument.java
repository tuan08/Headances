package org.headvances.ml.nlp.opinion;

/**
 * $Author: Tuan Nguyen$ 
 **/
public class OpinionDocument {
	private String documentId ;
	private String text ;
	
	public OpinionDocument() {} 
	
	public OpinionDocument(String docId, String text) {
		this.documentId = docId ;
		this.text = text ;
	}
	
	public String getDocumentId() { return documentId; }
	public void setDocumentId(String documentId) { this.documentId = documentId; }
	
	public String getText() { return text; }
	public void setText(String text) { this.text = text; }
}
