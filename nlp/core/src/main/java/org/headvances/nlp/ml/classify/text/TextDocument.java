package org.headvances.nlp.ml.classify.text;

public class TextDocument {
  private String label;
  private String title;
  private String description; 
  private String content;
  
  public TextDocument(){}
  
  public TextDocument(String content){this.content = content; }
  
  public TextDocument(String title, String description, String content){
    this.title = title;
    this.description = description;
    this.content = content;
  }
  
  public String getLabel() { return label; }
  public void setLabel(String label) { this.label = label; }
 
  public String getTitle() { return title; }
  public void setTitle(String title) { this.title = title; }
  
  public String getDescription() { return description; }
  public void setDescription(String description) { this.description = description; }

  public String getContent() { return content; }
  public void setContent(String content) { this.content = content; }
}
