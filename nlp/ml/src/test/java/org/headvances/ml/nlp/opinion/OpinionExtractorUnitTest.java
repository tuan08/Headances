package org.headvances.ml.nlp.opinion;

import java.util.List;

import org.junit.Test;

public class OpinionExtractorUnitTest {
  @Test
  public void test() throws Exception{   
    String[] keyword = { "iphone" };
    String [] text = {
        "không nên so sánh với iphone các bác à.",
        "Iphone 3gs không được đẹp",
        "Iphone 3gs ko to"
    };
    OpinionExtractor oExtractor = new OpinionExtractor(keyword);

    for (String txt: text) {
      List<Opinion> opinions = oExtractor.extract(new OpinionDocument("", txt));
      for(Opinion op: opinions) {
        System.out.println(op.getLabel() + " " + op.getTag()[0] + " " + op.getOpinion());
//        RuleMatch sel = op.getRuleMatch();
//        System.out.println("  " + sel.getRuleMatcher().getRuleExp()) ;
//        System.out.println("    " + sel.getTokenCollection().getOriginalForm()) ;
//        System.out.println("    " + sel.getUnitMatchString()) ;
//        System.out.println(         sel.getExtractString("    ")) ;
//        System.out.println("    Query = " + StringUtil.joinStringArray(op.getTag()));
        System.out.println();
      }
    }
  }
}
