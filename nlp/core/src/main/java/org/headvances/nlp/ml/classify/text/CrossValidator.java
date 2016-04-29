package org.headvances.nlp.ml.classify.text;

import java.util.Random;

import org.headvances.nlp.ml.classify.CrossValidationReporter;
import org.headvances.nlp.ml.classify.Predict;
import org.headvances.util.CommandParser;
import org.headvances.util.FileUtil;
import org.headvances.util.JVMInfoUtil;
import org.headvances.util.MD5;
import org.headvances.util.TimeReporter;

public class CrossValidator {
  private String trainDataDir ;
  private String testDataDir  ;
  private String modelFile ;
  
  private TimeReporter timeReporter = new TimeReporter();
  
  public void run(String[] args) throws Exception{
    CommandParser command = new CommandParser("cross-validator:") ;
    command.addMandatoryOption("data", true, "The input labeled data") ;
    command.addOption("outdir",        true, "The output data directory") ;
    if(!command.parse(args)) return ;
    command.printHelp() ;

    String dataDir = command.getOption("data", null) ;  
    String outDir = command.getOption("outdir", "target/validation") ;
    
    trainDataDir = outDir + "/trainData/";
    testDataDir = outDir + "/testData/";
    
    FileUtil.removeIfExist(trainDataDir) ;
    FileUtil.mkdirs(trainDataDir) ;
    
    FileUtil.removeIfExist(testDataDir) ;
    FileUtil.mkdirs(testDataDir) ;
    
    modelFile     = outDir  + "/text.features.model" ;
    
    TimeReporter.Time totalTime = timeReporter.getTime("CrossValidator: Total");
    TimeReporter.Time splitTime = timeReporter.getTime("CrossValidator: Data spliting");
    TimeReporter.Time classifyTime;
    totalTime.start();
    
    splitTime.start();
    split(dataDir);
    splitTime.stop();
    
    train(new MaxentTextTrainer());

    classifyTime = timeReporter.getTime("MaxentTextClassifier: Classify");
    classifyTime.start();
    classify(new MaxEntClassifier("file:" + modelFile));
    classifyTime.stop();
    
    totalTime.stop();
    timeReporter.report(System.out);
  }
  
  void split(String dataDir) throws Exception {
    System.out.println("Start split data!!!");
    MultiTextFileReader reader = new MultiTextFileReader(dataDir);
    TextDocument doc ;
    Random randomGen = new Random() ;
    int count = 0 ;
    while ((doc = reader.next()) != null) {
      float random = randomGen.nextFloat() ;
      if(random < 0.2) writeData(doc, false);
      else writeData(doc, true) ;
      count++ ;
      if(count % 1000 == 0) System.out.print('.') ;
    }
    System.out.println("\nSplit data done!!!");
  }
  
  private void writeData(TextDocument doc, boolean isTrainData) throws Exception{
    TextWriter writer;
    String title = doc.getTitle(); 
    if(title == null || title.length() == 0) return;
    title = title.trim();
    
    String parentPath;
    if(isTrainData) parentPath = trainDataDir + doc.getLabel() + "/";
    else parentPath = testDataDir + doc.getLabel() + "/";
    
    MD5 md5 = MD5.digest(title.toLowerCase()) ;
    String fname = md5.toString() + ".txt";
    
    if(!FileUtil.exist(parentPath))FileUtil.mkdirs(parentPath);
    
    writer = new TextWriter(parentPath + fname) ;
    writer.write(doc) ;
    writer.close();
  }

  void train(MaxentTextTrainer trainer) throws Exception{
    System.out.println("Start train data!!!");
    trainer.train(trainDataDir, modelFile) ;
    System.out.println("Train data done!!!");
  }

  void classify(MaxEntClassifier classifier) throws Exception {
    System.out.println("Start test data!!!");
    MultiTextFileReader reader = new MultiTextFileReader(testDataDir);
    CrossValidationReporter reporter = new CrossValidationReporter();
    int count = 0;
    TextDocument doc;
    while ((doc = reader.next()) != null) {
      Predict predict = classifier.predict(doc) ;
      String  expectLabel = doc.getLabel();
      reporter.report(predict, expectLabel) ;
      count++ ;
      if(count > 0 && count % 100 == 0) System.out.print('.') ;
    }
    System.out.println() ;
    reporter.getStatisticMap().report(System.out) ;
  }

  static public void main(String[] args) throws Exception {
    if(args.length == 0) {
      args = new String[] {
        "-data",   "d:/ml-data/text",
        "-outdir", "target/text-validation"
      };
    }
    System.out.println(JVMInfoUtil.getMemoryUsageInfo());
    CrossValidator validator = new CrossValidator() ;
    validator.run(args) ;
  }
}