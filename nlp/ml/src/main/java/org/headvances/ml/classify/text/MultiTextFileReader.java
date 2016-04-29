package org.headvances.ml.classify.text;

import java.io.File;

import org.headvances.util.FileUtil;
import org.headvances.util.text.StringUtil;

public class MultiTextFileReader {
  private TextReader reader;
  private String[]   files; 
  private int        currentFile = 0;
  private String dataDir;
  
  public MultiTextFileReader(String dataDir) throws Exception{
    this.dataDir = dataDir;
    process(new File(dataDir));
  }

  private void process(File folder) throws Exception{
    if(!folder.exists()) throw new Exception("Data directory is not existed!");
    File[] subFolders = folder.listFiles();
    for(File f: subFolders){
      if(f.isDirectory()){
        String[] fs = FileUtil.findFiles(f.getAbsolutePath(), ".*\\.txt");
        files = StringUtil.join(files, fs);
      } else {
        files = StringUtil.merge(files, f.getPath());
      }
    }
  }
  
  public TextDocument next() throws Exception{
    if(currentFile >= files.length) return null;
    String label = null;
    if(reader == null || currentFile < files.length){
      String file = files[currentFile++];
      reader = new TextReader(file);
      label = getLabel(file);
    }
    TextDocument doc = reader.read();
    if(doc == null){
      reader.close();
      reader = null;
      return next();
    }
    doc.setLabel(label);
    return doc;
  }
  
  private String getLabel(String file){
    File f = new File(file);
    String label = f.getParent();
    if(label.equals(dataDir)) return null;
    int idx = label.lastIndexOf("\\") + 1;
    label = label.substring(idx, label.length());
    return label;
  }
  
  public void close() throws Exception{
    if(reader != null) reader.close();
  }
}
