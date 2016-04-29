package org.headvances.ml.nlp.opinion;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.List;

import org.headvances.util.IOUtil;

public class MLUtil {
  static public List<Opinion> readSampleOpinion(String[] keyword, String sample) throws Exception{
  	OpinionExtractor oExtractor = new OpinionExtractor(keyword) ;
  	return readSampleOpinion(oExtractor, sample) ;
  }
  
  static public List<Opinion> readSampleOpinion(OpinionExtractor oExtractor, String sample) throws Exception{
  	List<Opinion> opinions = new ArrayList<Opinion>();
    InputStreamReader inputStreamReader = new InputStreamReader(IOUtil.loadRes(sample), "utf8");
    BufferedReader reader = new BufferedReader(inputStreamReader);

    String line = null;
    int count = 0 ;
    while((line = reader.readLine()) != null){
    	count++ ;
    	line = line.trim() ;
//    	System.out.println(count + ". " + line) ;
      int idx = line.indexOf("\t");
      String rank = line.substring(0, idx);
      if("-2".equals(rank)) rank = "-1" ;
      else if("2".equals(rank)) rank = "1" ;
      else if("-3".equals(rank)) rank = "0" ;
      else if("3".equals(rank)) rank = "0" ;
      //else if("i".equals(rank)) rank = "0" ;
      //if("-1".equals(rank)) rank = "1" ;
      
      String sentence = line.substring(idx + 1, line.length()).trim();
      List<Opinion> holder = oExtractor.loadTrainingDoc(new OpinionDocument("sentence", sentence)) ;
      for(Opinion op : holder) {
      	op.setOpinion(sentence) ;
      	op.setLabel(rank);
      	opinions.add(op);
      }
    }
    System.out.println("Read " + count + " line");
    return opinions;
  }
}