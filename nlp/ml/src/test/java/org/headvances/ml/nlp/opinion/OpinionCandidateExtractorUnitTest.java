package org.headvances.ml.nlp.opinion;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.headvances.nlp.query2.match.MatcherResourceFactory;
import org.headvances.util.IOUtil;
import org.headvances.util.statistic.StatisticsSet;
import org.headvances.util.text.StringUtil;
import org.junit.Test;

public class OpinionCandidateExtractorUnitTest {

//  @Test
  public void singleTest() throws Exception{
    String[] keyword = {"samsung galaxy", "iphone", "htc desire"};
    String[] text = {
        "1:các dòng điện thoại samsung galaxy, iphone, htc desire rất tốt",
        "3:Chiếc htc desire này rất tốt còn bảo hành",
        "3:Cần thanh lý chiếc iphone rất tốt",
        "-1:nên cân nhắc , mấy con htc desire với HP nóng lắm bạn",
        "1:Samsung galaxy nhìn trông long lanh"
    };
    OpinionExtractor oExtractor = new OpinionExtractor(keyword) ;

    List<Opinion> opinions = new ArrayList<Opinion>();
    for(String txt: text){
      int idx = txt.indexOf(":");
      String label = txt.substring(0, idx);
      String content = txt.substring(idx + 1, txt.length());

      List<Opinion> ops = findOpinions(label, content, oExtractor);
      for(Opinion op: ops)opinions.add(op);
    }
    
    process(opinions, StringUtil.joinStringArray(keyword));
  }

  @Test
  public void multiTest() throws Exception{
    String[] keyword = {"samsung galaxy", "iphone", "htc desire"};
    OpinionExtractor oExtractor = new OpinionExtractor(keyword) ;
    
    String sample = "file:C:\\Users\\Admin\\Desktop\\all-keyword\\label\\i.txt";
    InputStreamReader inputStreamReader = new InputStreamReader(IOUtil.loadRes(sample), "utf8");
    BufferedReader reader = new BufferedReader(inputStreamReader);

    List<Opinion> opinions =  new ArrayList<Opinion>();
    Set<String> opinionIds = new HashSet<String>();
    
    String line = null;
    int count = 0 ;
    while((line = reader.readLine()) != null){
      count++ ;
      line = line.trim() ;
      int idx = line.indexOf("\t");

      String label = line.substring(0, idx);
      String content = line.substring(idx + 1, line.length()).trim();
      
      List<Opinion> ops = findOpinions(label, content, oExtractor);
      for(Opinion op:  ops) {
        if(opinionIds.contains(op.getId())) continue;
        op.computeId(count + "");
        opinions.add(op);
        opinionIds.add(op.getId());
      }
    }
    process(opinions, StringUtil.joinStringArray(keyword));
  }
  
  public List<Opinion> findOpinions(String label, String content,  OpinionExtractor oExtractor) throws Exception{
    List<Opinion> holder = new ArrayList<Opinion>();
    List<Opinion> ops = oExtractor.loadTrainingDoc(new OpinionDocument("sentence", content)) ;
    for(Opinion op : ops) {
      op.setOpinion(content) ;
      op.setLabel(label);
      holder.add(op);
    }
    return holder;
  }

  private void process(List<Opinion> ops, String keyword) throws Exception{
    OpinionWriter writer = new OpinionWriter("target/candidate");
    StatisticsSet stats = new StatisticsSet();

    MatcherResourceFactory resFactory = new MatcherResourceFactory();
    OpinionCandidateExtractor candidateExtractor = new OpinionCandidateExtractor(keyword , resFactory);

    for(Opinion op: ops){
//      if(!isRegularLabel(op.getLabel())) continue;
      
      stats.incr("Opinion", "All", 1);
      stats.incr("Regular label", "All", 1);
      stats.incr("Regular label", "" + op.getLabel(), "All", 1);
      
      Opinion rOp = candidateExtractor.extract(op.getRuleMatch().getTokenCollection());
      if(rOp != null){
        rOp.setLabel(op.getLabel());
        stats.incr("Opinion", "candidate", "All", 1);
        
        String query = rOp.getTag()[0];
        query = query.replace(':', '.');

        writer.writeFile(query, rOp);
        stats.incr("Opinion", query, "All", 1);
      } else {
        writer.writeFile("fail", op);
        stats.incr("Opinion", "fail", "All", 1);
      }
    }

    writer.closeWriter();

    stats.report(System.out);
  }
//  
//  private boolean isRegularLabel(String label){
//    String[] labels = {"-3", "3", "i"};
//    for(String l: labels) 
//      if(label.equals(l)) return false;
//    return true;
//  }
}
