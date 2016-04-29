package org.headvances.ml.classify.xhtml;

import java.io.BufferedReader;
import java.io.InputStreamReader;

import libsvm.svm;
import libsvm.svm_model;

import org.headvances.data.Document;
import org.headvances.html.dom.TDocument;
import org.headvances.ml.Classifier;
import org.headvances.ml.Predict;
import org.headvances.ml.feature.Feature;
import org.headvances.ml.feature.FeatureDictionary;
import org.headvances.ml.feature.FeatureExtractor;
import org.headvances.ml.svm.SVMClassifier;
import org.headvances.ml.svm.SVMNodeBuilder;
import org.headvances.util.IOUtil;
import org.headvances.util.statistic.StatisticsSet;
/**
 * $Author: Tuan Nguyen$
 **/
public class SVMDocumentClassifier implements Classifier<TDocument> {
  private SVMClassifier classifier ;
  private FeatureDictionary dictionary ;
  private DocumentFeatureGenerator featureGenerator ;
  private FeatureExtractor<TDocument> fextractor ;
  private SVMNodeBuilder svmNodeBuilder ;
  private double trustThreshold = 0.85;

  public SVMDocumentClassifier() throws Exception {
    this("classpath:xhtml/svm/ContentType.model", "classpath:xhtml/svm/ContentType.dict.json") ;
  }

  public SVMDocumentClassifier(String modelRes, String dictRes) throws Exception {
    BufferedReader mbReader = 
      new BufferedReader(new InputStreamReader(IOUtil.loadRes(modelRes))) ;
    svm_model model = svm.svm_load_model(mbReader);
    mbReader.close() ;
    init(model, FeatureDictionary.load(IOUtil.loadRes(dictRes)));
  }
  
  public SVMDocumentClassifier(svm_model model, String dictRes) throws Exception {
    init(model, FeatureDictionary.load(IOUtil.loadRes(dictRes)));
  }
  
  public SVMDocumentClassifier(svm_model model, FeatureDictionary dict) throws Exception {
    init(model, dict);
  }
  
  private void init(svm_model model, FeatureDictionary dict) throws Exception {
    this.dictionary = dict;
    this.classifier = new SVMClassifier(model, 1, dict.getLabels().getLabels());
    this.featureGenerator = new DocumentFeatureGenerator();
    this.fextractor = new FeatureExtractor<TDocument>(featureGenerator) ;
    this.svmNodeBuilder = new SVMNodeBuilder(dictionary) ;
  }

  public void setTrustThreshold(double val) { this.trustThreshold = val ; }

  public boolean isTrusted(Predict predict) {
    return predict.probability > trustThreshold ;
  }

  public Predict predict(Document doc) throws Exception {
    Predict[] predict = classify(doc) ;
    Predict best = Predict.getBestPredict(predict) ;
    return best ;
  }

  public Predict[] classify(Document doc) throws Exception {
    TDocument tdoc = TDocument.create(doc);
    FeatureExtractor<TDocument> fextractor = new FeatureExtractor<TDocument>(featureGenerator) ;
    SVMNodeBuilder svmNodeBuilder = new SVMNodeBuilder(dictionary) ;
    Feature[] features = fextractor.extract(tdoc);
    return classifier.classify(svmNodeBuilder.getSVMVector(features)) ;
  }

  public Predict predict(TDocument tdoc) throws Exception {
    Predict[] predict = classify(tdoc) ;
    Predict best = Predict.getBestPredict(predict) ;
    return best ;
  }

  public Predict[] classify(TDocument tdoc) throws Exception {
    SVMNodeBuilder svmNodeBuilder = new SVMNodeBuilder(dictionary) ;
    Feature[] features = fextractor.extract(tdoc);
    return classifier.classify(svmNodeBuilder.getSVMVector(features)) ;
  }
  
  public Feature[] getGenerateFeatures(TDocument tdoc){
    Feature[] features = fextractor.extract(tdoc);
    return features;
  }
  
  public Feature[] getValidFeatures(Feature[] features){
    Feature[] rFeatures = svmNodeBuilder.getValidFeatures(features);
    return rFeatures;
  }
  
  public void report(StatisticsSet map, TDocument tdoc) {
  	Feature[] features = fextractor.extract(tdoc);
  	map.incr("Extract Features", "all" , 1) ;
  	Util.report(map, "Extract Features", features);
  	
  	features = svmNodeBuilder.getValidFeatures(features) ;
  	map.incr("Used    Features", "all", 1) ;
  	Util.report(map, "Used    Features", features);
  }
}