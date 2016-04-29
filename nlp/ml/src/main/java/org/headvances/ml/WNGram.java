package org.headvances.ml;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.regex.Pattern;

public class WNGram {
	static Pattern LINE_SPLITER = Pattern.compile("[\\p{P}]") ;
	static Pattern WORD_SPLITER = Pattern.compile("[\\p{Z}]") ;
	
  static List<String> slide(String s, int len) {
    List<String> result = new ArrayList<String>();
    String[] sequences = LINE_SPLITER.split(s) ;
    for (String sequence : sequences){
      String[] parts = WORD_SPLITER.split(sequence) ;
      
      for (int i = 0; i < parts.length - len + 1; i++) {
        StringBuilder sb = new StringBuilder();
        for (int k = 0; k < len; k++) {
          if (k > 0) sb.append(" ");
          sb.append(parts[i + k]);
        }
        if (!sb.toString().trim().equals(""))
          result.add(sb.toString());
      }  
    }    
    return result;
  }

  static public Set<String> ngrams(String s, int begingrams, int endgrams) {
    Set<String> col = new HashSet<String>();
    for (int i = begingrams; i <= endgrams; i++) {
      List<String> tokens = slide(s, i);
      col.addAll(tokens);
    }
    return col;
  }
  
  public static void main(String[] args) throws Exception {
    String url = "tốc độ của cpu là 2,13Ghz/s";
    Set<String> tokens = ngrams(url, 1, 3);
    for (String token : tokens) System.out.println("token: " + token);
  }
}