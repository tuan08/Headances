package org.headvances.ml.nlp.opinion;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

import org.headvances.util.CommandParser;
import org.headvances.util.FileUtil;

public class OpinionCrossValidator {
  private List<Opinion> trainData;
  private List<Opinion> testData;
  private String modelRes, dictRes;

  public OpinionCrossValidator() {
    trainData = new ArrayList<Opinion>();
    testData  = new ArrayList<Opinion>();
  }
  
  private void split(List<Opinion> ops, double ratio){
    Random randomGen = new Random() ;
    for(int i = 0; i < ops.size(); i++){
    	Opinion op = ops.get(i) ;
    	boolean test = i % 9 == 0;
      test = randomGen.nextFloat() < ratio ;
      if(test) testData.add(op);
      else trainData.add(op);
    }
  }

  public void doCross(String file) throws Exception{
  	String[] keyword = {"product"} ;
    OpinionMaxentTrainer trainer = new OpinionMaxentTrainer(keyword);
  	List<Opinion> opinions = MLUtil.readSampleOpinion(keyword, file) ;
    //this.trainData = opinions ;
    //this.testData  = opinions ;
  	split(opinions, 0.3);

    train(trainer);
    OpinionMaxentClassifier classifier = 
    	new OpinionMaxentClassifier(keyword, "file:"+ modelRes, "file:" + dictRes);
    classifier.predict("target/predict", testData) ;
  }
  
  private void train(OpinionMaxentTrainer trainer) throws Exception{
    trainer.train(trainData, modelRes, dictRes);
  }
  
  public void run(String[] args) throws Exception{
    CommandParser command = new CommandParser("cross-validator:") ;
    command.addMandatoryOption("sample", true, "The input labeled data") ;
    command.addOption("outdir",        true, "The output data directory") ;
    if(!command.parse(args)) return ;
    command.printHelp() ;
    
    String sample = command.getOption("sample", null) ;  
    String outDir = command.getOption("outdir", "target/opinion-val") ;
    
    FileUtil.removeIfExist(outDir) ;
    FileUtil.mkdirs(outDir) ;
    
    modelRes = outDir + "/opinion.maxent.model";
    dictRes = outDir + "/opinion.maxent.dict";
    
    doCross(sample);
  }
  
  public static void main(String[] args) throws Exception{
    if(args == null || args.length == 0) 
      args = new String[] {
        "-sample", "file:src/data/opinion/good-merged.txt",
      };
    OpinionCrossValidator validator = new OpinionCrossValidator();
    validator.run(args);
  }
}