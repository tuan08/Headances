package org.headvances.ml.nlp.opinion;

import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

public class OpinionMerger {
  private Map<String, Opinion> maps = new HashMap<String, Opinion>();
 
  public OpinionMerger(){}
  
  public void merge(String targetFile, String withFile) throws Exception {
    OpinionExtractor oExtractor = new OpinionExtractor(new String[]{"product"});
    List<Opinion> ops1 = MLUtil.readSampleOpinion(oExtractor, targetFile);
    List<Opinion> ops2 = MLUtil.readSampleOpinion(oExtractor, withFile);
    
    for(Opinion op: ops1) {
      op.computeId(op.getDocumentId());
      maps.put(op.getId(), op);
    }
   
    int count = 0; 
    
    for(Opinion op: ops2){
      op.computeId(op.getDocumentId());
      if(maps.get(op.getId()) != null){
        count ++;
        continue;
      }
      maps.put(op.getId(), op);
    }
    System.out.println("Statistic of duplicated line: " + count);
  }
  
  public void dump(String outDir) throws Exception{
    OpinionWriter writer = new OpinionWriter(outDir);
 
    Iterator<Opinion> itr = maps.values().iterator();
    while(itr.hasNext()){
      writer.writeFile("good_merged", itr.next());
    }
  }
  
  public static void main(String[] args) throws Exception{
    OpinionMerger merger = new OpinionMerger();
    merger.merge(
        "file:src/data/opinion/123_good.txt",
        "file:src/data/opinion/review.-1.new.txt"
    );
    merger.dump("target/merged");
  }
}
