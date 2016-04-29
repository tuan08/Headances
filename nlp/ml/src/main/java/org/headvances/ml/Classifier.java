package org.headvances.ml;

import org.headvances.ml.feature.Feature;
import org.headvances.util.statistic.StatisticsSet;
/**
 * $Author: Tuan Nguyen$
 **/
public interface Classifier<T> {
  public boolean   isTrusted(Predict predict) ;
  public Predict   predict(T tdoc) throws Exception ;
  public Predict[] classify(T tdoc) throws Exception ;

  public void report(StatisticsSet map, T doc) ;

  static public class Util {
    static public void report(StatisticsSet map, String category, Feature[] features){
      int num = features.length;
      if (num <= 100){
        int start = num/10 * 10 + 1;
        int end = (num/10 + 1) * 10;
        String name = start + " - " + end;
        map.incr(category, name, "all", 1);
      } else {
        map.incr(category, "> 100" , "all",1); 
      }
    }
  }
}