package org.headvances.ml.classify.text;

import java.io.BufferedWriter;
import java.io.FileOutputStream;
import java.io.OutputStream;
import java.io.OutputStreamWriter;

public class TextWriter {
  BufferedWriter writer;
  OutputStream out;
  
  public TextWriter(OutputStream out) throws Exception {
    init(out);
  }
  
  public TextWriter(String file) throws Exception {
    OutputStream out = new FileOutputStream(file, true);
    init(out);
  }

  private void init(OutputStream out) throws Exception {
    this.out = out;
    writer = new BufferedWriter(new OutputStreamWriter(this.out, "UTF-8"), 1024);
  }
  
  public void write(TextDocument doc) throws Exception {
    writer.append("Title: " + doc.getTitle() + "\r");
    writer.append(doc.getContent());
  }
  
  public void close() throws Exception {
    writer.close();
    out.close();
  }

}
