package org.headvances.nlp.ml.classify.text;

import java.io.BufferedReader;
import java.io.FileInputStream;
import java.io.InputStream;
import java.io.InputStreamReader;

public class TextReader {
  
  private BufferedReader reader;
  private InputStream in;

  public TextReader(String file) throws Exception {
    init(new FileInputStream(file));
  }
  
  public TextReader(InputStream in) throws Exception {
    init(in);
  }
  
  private void init(InputStream in) throws Exception {
    this.in = in;
    reader = new BufferedReader(new InputStreamReader(this.in, "UTF-8"));
  }
  
  public TextDocument read() throws Exception {
    TextDocument doc = new TextDocument();
    String content = "", line = "";
    while((line = reader.readLine()) != null){
      if(line.length() == 0) continue;
      if(line.startsWith("Title:")){
        String title = line.substring(7, line.length());
        doc.setTitle(title);
      }
      else content += line + "\r";
    }
    if(content.length() == 0) return null;
    doc.setContent(content);
    return doc; 
  }
  
  public void close() throws Exception{
    reader.close();
    in.close();
  }
}
