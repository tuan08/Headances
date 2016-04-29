package org.headvances.ml.nlp.opinion;

import java.io.BufferedWriter;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintStream;
import java.io.Writer;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

import org.headvances.util.FileUtil;
import org.headvances.util.statistic.StatisticsSet;
import org.headvances.util.text.StringUtil;

public class OpinionWriter {
  private Map<String, Writer> writers;
  private String outDir ;
  private StatisticsSet statistic;

  public OpinionWriter(String outDir) throws Exception {
    this.outDir = outDir;
    writers = new HashMap<String, Writer>();
    statistic = new StatisticsSet();
    FileUtil.removeIfExist(outDir);
    FileUtil.mkdirs(outDir);
  }

  public void writeByLabel(Opinion opinion) throws IOException{
    String label = opinion.getLabel() ;
    write("label", opinion) ;
    statistic.incr("Label", "all", 1);
    statistic.incr("Label", "label: " + label, "all", 1);
  }

  public void writeByTrust(Opinion opinion) throws IOException{
    statistic.incr("Trust", "all", 1);
    if(StringUtil.isIn("candidate:trust", opinion.getTag())) {
      statistic.incr("Trust", "trust", "all", 1);
      write("trust", opinion) ;
    }
  }

  public void writeByUnTrust(Opinion opinion) throws IOException{
    statistic.incr("Untrust", "all", 1);
    if(StringUtil.isIn("candidate:untrust", opinion.getTag())) {
      statistic.incr("Untrust", "untrust", "all", 1);
      write("untrust", opinion) ;
    }
  }

  public void report(PrintStream out){
    statistic.report(out);
  }

  public void write(String subdir, Opinion opinion) throws IOException {
    String label = opinion.getLabel() ;
    String dir = outDir + "/" + subdir ;
    String file = dir + "/" + label + ".txt" ;
    Writer writer = writers.get(file);
    if(writer == null) {
      FileUtil.mkdirs(dir) ;
      writer = new BufferedWriter(new FileWriter(file, false));
      writers.put(file, writer);
    }
    writer.append(opinion.getLabel()).append('\t').append(opinion.getOpinion()).append('\n') ;
  }

  public void writeFile(String file, Opinion opinion) throws IOException {
    file = outDir + "/" + file + ".txt" ;
    Writer writer = writers.get(file);
    if(writer == null) {
      writer = new BufferedWriter(new FileWriter(file, false));
      writers.put(file, writer);
    }
    if(opinion.getTag() == null){
      writer.append(opinion.getLabel()).append('\t').append(opinion.getOpinion()).append('\n') ;
    } else {
      writer.append(opinion.getLabel()).append('\t').append(opinion.getOpinion()).append('\n') ;
      
      writer.append(opinion.getRuleMatch().getRuleMatcher().getRuleExp()).append('\n');
      writer.append(opinion.getRuleMatch().getUnitMatchString()).append('\n');
      writer.append(opinion.getRuleMatch().getExtractString("    ")).append('\n');
      writer.append("\n**********************************************************\n");
    }
  }
  
  public void writeFileWithCategory(String file, Opinion opinion) throws IOException {
    file = outDir + "/" + file + ".txt" ;
    Writer writer = writers.get(file);
    if(writer == null) {
      writer = new BufferedWriter(new FileWriter(file, false));
      writers.put(file, writer);
    }
    if(opinion.getCategory().length > 0){
      writer.append(opinion.getLabel()).append('\t').append(opinion.getOpinion()).append('\n') ;
      writer.append(opinion.getRuleMatch().getUnitMatchString()).append('\n');
      writer.append(opinion.getRuleMatch().getExtractString("    ")).append('\n');
      writer.append("Category: ").append(StringUtil.joinStringArray(opinion.getCategory())).append('\n');
      writer.append("**********************************************************\n");
    }
  }
  
  public void writeText(String file, String text) throws IOException {
    statistic.incr("Review", "All", 1);
    statistic.incr("Review", file, "All", 1);
    
    String dir = outDir + "/review" ;
    file = dir + "/" + file + ".txt" ;
    Writer writer = writers.get(file);
    if(writer == null) {
      FileUtil.mkdirs(dir);
      writer = new BufferedWriter(new FileWriter(file, false));
      writers.put(file, writer);
    }
    writer.append(text).append('\n') ;
  }

  public void closeWriter() throws IOException{
    Iterator<Writer> itr = writers.values().iterator();
    while(itr.hasNext()) itr.next().close();
  }

  public void flush() throws IOException{
    Iterator<Writer> itr = writers.values().iterator();
    while(itr.hasNext()) itr.next().flush();
  }
}