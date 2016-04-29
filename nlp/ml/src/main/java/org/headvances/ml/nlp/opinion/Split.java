package org.headvances.ml.nlp.opinion;

import java.io.BufferedReader;
import java.io.FileOutputStream;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.io.Writer;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

import org.headvances.util.FileUtil;
import org.headvances.util.IOUtil;

public class Split {
	private Map<String, Writer> writers = new HashMap<String, Writer>() ;
	private String outDir  ;
	
	public Split(String outDir) {
		this.outDir = outDir ;
	}
	
	int countKeyword(String line, String keyword) {
		int start = 0;
		int idx = -1 ;
		int count = 0 ;
		while((idx = line.indexOf("PRODUCT", start)) >= 0) {
			start = idx + 1 ;
			count++ ;
		}
		return count ;
	}
	
	void write(String file, String label, String sentence) throws Exception {
		Writer writer = writers.get(file) ;
		if(writer == null) {
			writer = new OutputStreamWriter(new FileOutputStream(outDir + "/" + file + ".txt"), "UTF-8") ;
			writers.put(file, writer) ;
		}
		writer.append(label).append("\t").append(sentence).append("\n") ;
	}
	
	void closeWriters() throws Exception {
		Iterator<Writer> i = writers.values().iterator() ;
		while(i.hasNext()) i.next().close() ;
		writers.clear() ;
	}
	
	public void run(String inFile) throws Exception {
		closeWriters() ;
		FileUtil.mkdirs(outDir) ;
  	InputStreamReader inputStreamReader = new InputStreamReader(IOUtil.loadRes(inFile), "UTF-8");
    BufferedReader reader = new BufferedReader(inputStreamReader);
    String line = null;
    while((line = reader.readLine()) != null){
    	line = line.trim() ;
    	int idx = line.indexOf("\t");
    	String label = line.substring(0, idx);
    	String sentence = line.substring(idx + 1, line.length()).trim();
    	int count = countKeyword(line, "PRODUCT") ;
    	if(count != 1) {
    		write("rm", "rm", sentence) ;
    	} else {
    		write("good", label, sentence) ;
    	}
    }
    reader.close() ;
    closeWriters() ;
  }
	
  public static void main(String[] args) throws Exception {
  	String inFile  = "file:src/data/opinion/todo-1.txt" ;
  	String outDir  = "target/split" ;
  	new Split(outDir).run(inFile);
  }
}