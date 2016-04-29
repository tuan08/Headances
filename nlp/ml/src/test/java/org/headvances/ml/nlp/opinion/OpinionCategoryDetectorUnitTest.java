package org.headvances.ml.nlp.opinion;

import java.util.ArrayList;
import java.util.List;

import org.headvances.nlp.query2.match.MatcherResourceFactory;
import org.headvances.util.statistic.StatisticsSet;
import org.headvances.util.text.StringUtil;
import org.junit.Test;

public class OpinionCategoryDetectorUnitTest {
  //@Test
  public void singleTest() throws Exception{
    String keyword = "iphone";
    String[] text = {
        "1:Chiếc iphone này màn hình rất tốt.",
        "1:Ở mức giá thấp - trung bình iphone rất ngon .",
        "1:Iphone khá ngon mà có giá mềm nữa.",
        "1:con iphone mà còn ngon thì giá kia cũng ổn"
    };
    OpinionExtractor oExtractor = new OpinionExtractor(new String[]{keyword}) ;

    List<Opinion> opinions = new ArrayList<Opinion>();
    for(String txt: text){
      int idx = txt.indexOf(":");
      String label = txt.substring(0, idx);
      String content = txt.substring(idx + 1, txt.length());

      List<Opinion> ops = oExtractor.loadTrainingDoc(new OpinionDocument("sentence", content));
      for(Opinion op: ops){
        op.setOpinion(content);
        op.setLabel(label);
        opinions.add(op);
      }
    }
    
    process(opinions);
    
  }
  
  @Test
  public void multiTest() throws Exception{
    List<Opinion> ops = MLUtil.readSampleOpinion(new String[]{"product"}, "file:src/data/opinion/123_good.txt");
    process(ops);
  }
  
  private void process(List<Opinion> ops) throws Exception {
    OpinionWriter writer = new OpinionWriter("target/category");
    StatisticsSet stats = new StatisticsSet();

    MatcherResourceFactory resFactory = new MatcherResourceFactory();
    OpinionCategoryDetector cDetector = new OpinionCategoryDetector(resFactory);
    
    for(Opinion op: ops){
      if(!isRegularLabel(op.getLabel())) continue;
      
//      System.out.println(op.getOpinion());
      
      cDetector.detect(op, op.getRuleMatch().getTokenCollection());
      stats.incr("Opinion", "All", 1);
      stats.incr("Opinion", op.getCategory().length + " cat", "All", 1);
      
      writer.writeFileWithCategory(op.getCategory().length + "-cat", op);
      
      for(String cat: op.getCategory()){
        cat = cat.replace(':', '.');
        writer.writeFile(cat, op);
        
        stats.incr("Category", "All", 1);
        stats.incr("Category", cat, "All", 1);
      }
      System.out.println(op.getOpinion() + " " + StringUtil.joinStringArray(op.getCategory()));
    }
    
    writer.closeWriter();
    stats.report(System.out);
    
  }
  
  private boolean isRegularLabel(String label){
    String[] labels = {"-3", "3", "i"};
    for(String l: labels) 
      if(label.equals(l)) return false;
    return true;
  }


}
