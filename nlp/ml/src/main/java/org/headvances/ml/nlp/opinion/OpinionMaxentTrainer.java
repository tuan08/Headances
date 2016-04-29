package org.headvances.ml.nlp.opinion;

import java.io.FileOutputStream;
import java.io.ObjectOutputStream;
import java.util.ArrayList;
import java.util.List;

import org.headvances.ml.feature.Feature;
import org.headvances.ml.feature.FeatureDictionary;
import org.headvances.ml.feature.FeatureExtractor;
import org.headvances.ml.feature.FeatureExtractorDictionary;
import org.headvances.ml.feature.FeatureGenerator;
import org.headvances.ml.maxent.FeatureInstanceFactory;
import org.headvances.nlp.NLPResource;
import org.headvances.nlp.dict.WordTreeDictionary;
import org.headvances.util.CommandParser;
import org.headvances.util.FileUtil;
import org.headvances.util.JVMInfoUtil;

import cc.mallet.classify.Classifier;
import cc.mallet.classify.MaxEntTrainer;
import cc.mallet.types.InstanceList;

public class OpinionMaxentTrainer {
  private OpinionExtractor oExtractor;

  public OpinionMaxentTrainer(String[] keyword) throws Exception {
  	this.oExtractor = new OpinionExtractor(keyword) ;
  }
  
  public OpinionExtractor getOpinionExtractor() { return this.oExtractor ; }
  
  public void train(String sample, String modelRes, String dictRes) throws Exception {
    List<Opinion> ops = MLUtil.readSampleOpinion(oExtractor, sample);
    train(ops, modelRes, dictRes);
  }

  public void train(List<Opinion> ops, String modelRes, String dictRes) throws Exception {
    OpinionFeatureDictionaryBuilder dictBuilder = 
    	new OpinionFeatureDictionaryBuilder(createFeatureGenerator(oExtractor.getKeyword()));
    dictBuilder.process(ops);
    FeatureDictionary dict = dictBuilder.getIDFDictionary();
    dict.save(dictRes);
    
    FeatureExtractor<Opinion> featureExtractor = createFeatureExtractor(dict, oExtractor.getKeyword());
    
    FeatureInstanceFactory factory = new FeatureInstanceFactory() ;
    InstanceList trainingData = new InstanceList(factory.getDataAlphabet(), factory.getTargetAlphabet());
    System.out.println("x" + trainingData.getTargetAlphabet());
    for(int i = 0; i < ops.size(); i++){
      Opinion op = ops.get(i);
      Feature[] feature = featureExtractor.extract(op);
      String target = op.getLabel();
      trainingData.add(factory.createTrainInstance(feature, target)) ;
      if(i % 100 == 0) {
        System.out.println("load " + i + " opinions");
      }
    }
    MaxEntTrainer trainer = new MaxEntTrainer();
    Classifier classifier = trainer.train(trainingData);

    ObjectOutputStream out = new ObjectOutputStream(new FileOutputStream (modelRes));
    out.writeObject (classifier);
    out.close();
  }

  public List<Opinion> doExtract(List<Opinion> ops) throws Exception {
  	List<Opinion> extractOpinions = new ArrayList<Opinion>() ;
  	for(int i = 0; i < ops.size(); i++) {
  		Opinion op = ops.get(i) ;
  		List<Opinion> list = oExtractor.extract(new OpinionDocument("opinion", op.getOpinion())) ;
  		for(int j = 0; j < list.size(); j++) {
  			Opinion sel = list.get(j) ;
  			sel.setLabel(op.getLabel()) ;
  			extractOpinions.add(sel) ;
  		}
  	}
  	return extractOpinions ;
  }

  static public FeatureExtractor<Opinion> createFeatureExtractor(FeatureDictionary dict, String[] keyword) throws Exception {
  	FeatureGenerator<Opinion> generator = createFeatureGenerator(keyword);
  	FeatureExtractor<Opinion> featureExtractor = new FeatureExtractorDictionary<Opinion>(generator, dict) ;
    featureExtractor.setUseMapFeatureHolder(true) ;
    return featureExtractor ; 
  }
  
  static public FeatureGenerator<Opinion> createFeatureGenerator(String[] keyword) throws Exception {
  	//return new OpinionFeatureGeneratorOld();
  	String[] dictRes = {
  		"classpath:nlp/vn.lexicon.json",	
  		//"classpath:nlp/opinion/opinions.json",
      //"classpath:nlp/opinion/nuance.json",
      //"classpath:nlp/opinion/taxonomy.json"
  	};
  	WordTreeDictionary dict = NLPResource.getInstance().getWordTreeDictionary(dictRes) ;
  	FeatureGenerator<Opinion> generator = new OpinionFeatureGenerator(keyword, dict) ;
  	return generator ;
  }

  public void run(String[] args) throws Exception{
    CommandParser command = new CommandParser("Trainer:") ;
    command.addMandatoryOption("sample", true, "The sample labeled data") ;
    command.addOption("outdir",        true, "The output data directory") ;
    if(!command.parse(args)) return ;
    command.printHelp() ;

    String dataDir = command.getOption("sample", null) ;  
    String outDir = command.getOption("outdir", "target/opinion") ;

    FileUtil.removeIfExist(outDir) ;
    FileUtil.mkdirs(outDir) ;

    String modelFile     = outDir  + "/opinion.maxent.model" ;
    String dictFile     = outDir  + "/opinion.maxent.dict" ;
    
    train(dataDir, modelFile, dictFile);
  }

  public static void main(String[] args) throws Exception{
    if(args.length == 0) {
      args = new String[] {
          "-sample",   "file:src/data/opinion/all-merged.txt",
          "-outdir", "target/opinion",
      };
    }
    System.out.println(JVMInfoUtil.getMemoryUsageInfo());
    OpinionMaxentTrainer trainer = new OpinionMaxentTrainer(new String[] {"product"});
    trainer.run(args);
  }
}