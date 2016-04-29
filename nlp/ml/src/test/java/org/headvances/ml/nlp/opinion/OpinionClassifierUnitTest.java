package org.headvances.ml.nlp.opinion;

import java.util.List;

import org.headvances.ml.Predict;
import org.headvances.util.statistic.StatisticsSet;
import org.junit.Test;

public class OpinionClassifierUnitTest {
  @Test
  public void test() throws Exception{
    String[] keyword = {"iphone", "samsung galaxy", "htc desire", "nokia lumia", "xt720"};
    String[] files = {
        "C:\\Users\\Admin\\Desktop\\all-keyword\\label-2\\i.txt",
        "C:\\Users\\Admin\\Desktop\\all-keyword\\label-2\\1.txt",
        "C:\\Users\\Admin\\Desktop\\all-keyword\\label-2\\-1.txt",
    };
    OpinionMaxentClassifier classifier = new OpinionMaxentClassifier(keyword);
    OpinionExtractor oExtractor = new OpinionExtractor(keyword);
    OpinionWriter writer = new OpinionWriter("target/classify");
    
    StatisticsSet stat = new StatisticsSet();

    for(String file: files){
      System.out.println("Classifying " + file);
      List<Opinion> opinions = MLUtil.readSampleOpinion(oExtractor, "file:" + file);
      for(Opinion op: opinions) {   
        stat.incr("Opinion", "All", 1);
        Predict predict = classifier.predict(op);
        if(!predict.label.equals(op.getLabel())){
          String label = predict.label + "." + op.getLabel();
          op.setLabel(label);
          writer.writeByLabel(op);
          stat.incr("Opinion", label, "All", 1);
        }
      }
    }
    stat.report(System.out);
    writer.closeWriter();
  }
}
