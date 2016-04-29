package org.headvances.ml.feature;

/**
 * $Author: Tuan Nguyen$ 
 **/
public class FeatureUtil {
	static public String normalize(String label) {
    if (label == null || label.length() == 0) return label;
    if(label.length() < 3 || label.length() > 20) return null;
    StringBuilder b = new StringBuilder();
    char[] buf = label.toCharArray();
    for (char c : buf) {
    	if(c == ' ') b.append(c);
    	else if(Character.isLetter(c)) b.append(Character.toLowerCase(c));
    }
    if(b.length() < 3 || b.length() > 20) return null;
    String result = b.toString();
    return result;
  }
}