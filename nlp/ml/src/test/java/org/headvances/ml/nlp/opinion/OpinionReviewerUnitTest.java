package org.headvances.ml.nlp.opinion;

import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.headvances.nlp.token.TextSegmenter;
import org.headvances.nlp.token.analyzer.CommonTokenAnalyzer;
import org.headvances.nlp.token.analyzer.PunctuationTokenAnalyzer;
import org.headvances.nlp.token.analyzer.TokenAnalyzer;
import org.headvances.nlp.token.analyzer.USDTokenAnalyzer;
import org.headvances.nlp.token.analyzer.VNDTokenAnalyzer;
import org.headvances.nlp.token.analyzer.VNMobileTokenAnalyzer;
import org.headvances.nlp.token.analyzer.VNPhoneTokenAnalyzer;
import org.headvances.util.statistic.StatisticsSet;
import org.junit.Test;

public class OpinionReviewerUnitTest {
  @Test
  public void multiTest() throws Exception{
    List<Opinion> ops = MLUtil.readSampleOpinion(new String[]{"product"}, "file:C:\\Users\\Admin\\Desktop\\all-keyword\\label\\i.txt");
    process(ops, "product");
  }

  private void process(List<Opinion> ops, String keyword) throws Exception{
    OpinionWriter writer = new OpinionWriter("target/review");
    StatisticsSet stats = new StatisticsSet();
    TextSegmenter textSegmenter = createTextSegmenter();
    
    OpinionReviewer filter = new OpinionReviewer(textSegmenter);
    Set<String> set = new HashSet<String>();

    for(Opinion op: ops){
      stats.incr("Opinion", "All", 1);

      if(set.contains(op.getOpinion())) continue;
      else set.add(op.getOpinion());
      
      Opinion rOp = filter.review(op.getRuleMatch().getTokenCollection());
      if(rOp != null){
        rOp.setLabel(op.getLabel());

        String query = rOp.getTag()[0];
        query = query.replace(':', '.');

        rOp.setTag(null);
        writer.writeFile(query, rOp);
        stats.incr("Opinion", query, "All", 1);
      } else {
        stats.incr("Opinion", "fail", "All", 1);
        writer.writeFile("fail", op);
      }
    }
    writer.closeWriter();
    stats.report(System.out);
  }
  
  public TextSegmenter createTextSegmenter(){
    TokenAnalyzer[] analyzer = new TokenAnalyzer[] {
        new PunctuationTokenAnalyzer(), 
        new CommonTokenAnalyzer(), 
        new VNMobileTokenAnalyzer() ,
        new VNPhoneTokenAnalyzer() ,
        new USDTokenAnalyzer(),
        new VNDTokenAnalyzer(),
    };
    return new TextSegmenter(analyzer) ;
  }
}
