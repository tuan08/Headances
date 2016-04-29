package org.headvances.ml.swingui.classify;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import org.headvances.data.Document;
import org.headvances.json.JSONReader;
import org.headvances.json.JSONWriter;
import org.headvances.util.FileUtil;

/**
 * $Author: Tuan Nguyen$ 
 **/
public class DocumentIO {
	private String   dir ;
	private String[] files ;
	private int      cfile   = 0;
	private FileIO   currentFileIO = null ;
	
	public DocumentIO(String dir) throws IOException {
		this.dir = dir ;
		List<String> holder = new ArrayList<String>() ;
		for(String sel : FileUtil.findFiles(dir, ".*\\.json.*")) {
		  System.out.println(sel);
			if(sel.indexOf("labeled") < 0) holder.add(sel) ;
		}
		this.files = holder.toArray(new String[holder.size()]) ;
	}
	
	public Document[] next() throws Exception {
		if(currentFileIO == null) {
			if(cfile >= files.length) return null ;
			currentFileIO = new FileIO(files[cfile++]) ;
		}
		Document[] doc = currentFileIO.next() ;
		if(doc == null) {
			currentFileIO.close() ;
			currentFileIO = null ;
			return next() ;
		}
		return doc; 
	}
	
	public void write(Document[] doc) throws Exception {
		currentFileIO.write(doc) ;
	}
	
	public void close() throws Exception {
		if(currentFileIO != null) currentFileIO.close() ;
	}
	
	static public class FileIO  {
		private String     file ;
		private JSONReader creader ;
		private JSONWriter cwriter ;
		private int read = 0, write = 0;
		private Document   current ;
		
		public FileIO(String file) throws Exception {
			this.file = file; 
			creader = new JSONReader(file) ;
			String labeledFile = file.replace(".json", "-labeled.json") ;
			cwriter = new JSONWriter(labeledFile) ;
		}
		
		public Document[] next() throws Exception {
			if(read != write) {
				throw new Exception("Need to commit the documents") ;
			}
			String currentPrefix = null ;
			List<Document> holder = new ArrayList<Document>() ;
			if(current == null) current = creader.read(Document.class) ;
			int count = 0 ;
			while(current != null && count < 100) {
				count++ ;
				String id = current.getId() ;
				String prefix = id.substring(0, id.indexOf(":")) ;
				if(currentPrefix == null) {
					currentPrefix = prefix ;
					holder.add(current) ;
					current = creader.read(Document.class) ;
				} else if(prefix.equals(currentPrefix)) {
					holder.add(current) ;
					current = creader.read(Document.class) ;
				} else {
					read += holder.size() ;
					return holder.toArray(new Document[holder.size()]) ;
				}
			}
			if(holder.size() > 0) {
				read += holder.size() ;
				return  holder.toArray(new Document[holder.size()]) ;
			}
			return null ;
		}
		
		public void write(Document[] doc) throws Exception {
			if(doc == null || doc.length == 0) return ;
			for(int i = 0; i < doc.length; i++) {
				cwriter.write(doc[i]) ;
			}
			write += doc.length ;
		}
		
		public void close() throws Exception {
			if(read != write) {
				throw new Exception("Need to commit the documents") ;
			}
			creader.close() ;
			cwriter.close() ;
			FileUtil.remove(new File(file), false) ;
		}
	}
	
	static public void main(String[] args) throws Exception {
		DocumentIO io  = new DocumentIO("export") ;
		Document[] doc = null ;
		int total = 0;
		while((doc = io.next()) != null) {
			io.write(doc) ;
			for(Document  sel : doc) {
				System.out.println("  " + sel.getId()) ;
			}
			total +=  doc.length ;
			System.out.println("Read: " + doc.length);
		}
		io.close() ;
		System.out.println("Total: " + total);
	}
}