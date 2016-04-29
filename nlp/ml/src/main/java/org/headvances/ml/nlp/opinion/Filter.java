package org.headvances.ml.nlp.opinion;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.headvances.util.IOUtil;
import org.headvances.util.text.StringMatcher;

public class Filter {
  public Filter() { }
  
  private String filter(String sentence) {
    return sentence.replaceAll("[\\p{M}\\p{P}]", "").toLowerCase().trim();
  }
  
  public void run(String inFile) throws Exception {
    Map<String,String> sens = new HashMap<String,String>();
    
    InputStreamReader inputStreamReader = new InputStreamReader(IOUtil.loadRes(inFile), "UTF-8");
    BufferedReader reader = new BufferedReader(inputStreamReader);
    String line = null;
    int count = 0; 
    while((line = reader.readLine()) != null){
      line = line.trim() ;
      int idx = line.indexOf("\t");
      String label = line.substring(0, idx);
      String sentence = line.substring(idx + 1, line.length()).trim();      
      if (!matches(sentence,"PRODUCT\\p{L}") && !matches(sentence,"\\p{L}PRODUCT") && label.equals("-1")) 
        sens.put(filter(sentence),line);
      /*if (key == null) {
        System.out.println(key);
        System.out.println(line);
      }*/      
      count++;
    }
    for (String value : sens.values()) {
      System.out.println(value);
    }
    System.out.println("Statistic of sentences: " + count + " filter: " + sens.size());
    reader.close() ;    
  }
  
  private boolean matches(String text, String rule) throws Exception {
    boolean foundMatch = false;    
    Pattern regex = Pattern.compile(rule);
    Matcher regexMatcher = regex.matcher(text);
    foundMatch = regexMatcher.find();
    return foundMatch;
  }
  
  public static void main(String[] args) throws Exception {
    String inFile  = "file:src/data/opinion/123_good.txt" ;
    new Filter().run(inFile);    
  }
}
