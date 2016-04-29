package org.headvances.nlp.dict;

import java.io.BufferedReader;
import java.io.InputStreamReader;

import org.headvances.json.JSONWriter;
import org.headvances.util.IOUtil;

/**
 * $Author: Tuan Nguyen$ 
 **/
public class BuildLexicon {
  static public void main(String[] args) throws Exception {
    String file = "file:d:/words.txt" ;
    InputStreamReader inputStreamReader = new InputStreamReader(IOUtil.loadRes(file), "utf8");
    BufferedReader reader = new BufferedReader(inputStreamReader);
    JSONWriter writer = new JSONWriter("vn.lexicon2.json") ;
    String line = null;
    while((line = reader.readLine()) != null){
      Meaning meaning = new Meaning() ;
      meaning.setName(line) ;
      meaning.setOType("lexicon");
      meaning.setLang("vi");
      meaning.put("postag", new String[] {"pos:X"}) ;
      writer.write(meaning) ;
    }
    writer.close() ;
    reader.close() ;
  }
}
