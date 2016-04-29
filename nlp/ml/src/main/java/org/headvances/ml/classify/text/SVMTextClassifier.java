package org.headvances.ml.classify.text;

import java.io.BufferedReader;
import java.io.InputStreamReader;

import libsvm.svm;
import libsvm.svm_model;

import org.headvances.ml.Classifier;
import org.headvances.ml.Predict;
import org.headvances.ml.feature.Feature;
import org.headvances.ml.feature.FeatureDictionary;
import org.headvances.ml.feature.FeatureExtractor;
import org.headvances.ml.svm.SVMClassifier;
import org.headvances.ml.svm.SVMNodeBuilder;
import org.headvances.util.IOUtil;
import org.headvances.util.statistic.StatisticsSet;

public class SVMTextClassifier implements Classifier<TextDocument> {
  private SVMClassifier classifier ;
  private FeatureDictionary dictionary ;
  private TextFeatureGenerator generator ;
  private double trustThreshold = 0.85;
  
  public SVMTextClassifier() throws Exception {
    this("classpath:xhtml/svm/ContentType.dict.json", "classpath:xhtml/svm/ContentType.model") ;
  }

  public SVMTextClassifier(String modelRes, String dictRes) throws Exception {
    BufferedReader mbReader = 
      new BufferedReader(new InputStreamReader(IOUtil.loadRes(modelRes))) ;
    svm_model model = svm.svm_load_model(mbReader);
    mbReader.close() ;
    init(model, FeatureDictionary.load(IOUtil.loadRes(dictRes)));
  }
  
  public SVMTextClassifier(svm_model model, String dictRes) throws Exception {
    init(model, FeatureDictionary.load(IOUtil.loadRes(dictRes)));
  }
  
  public SVMTextClassifier(svm_model model, FeatureDictionary dict) throws Exception {
    init(model, dict);
  }
  
  private void init(svm_model model, FeatureDictionary dict) throws Exception {
    this.dictionary = dict;
    this.classifier = new SVMClassifier(model, 1, dict.getLabels().getLabels());
    this.generator = new TextFeatureGenerator();
  }

  public boolean isTrusted(Predict predict) {
    return predict.probability > trustThreshold;
  }

  public Predict predict(TextDocument doc) throws Exception {
    Predict[] predicts = classify(doc);
    Predict best = Predict.getBestPredict(predicts);
    return best;
  }

  public Predict[] classify(TextDocument doc) throws Exception {
    FeatureExtractor<TextDocument> fextractor = new FeatureExtractor<TextDocument>(generator) ;
    SVMNodeBuilder svmNodeBuilder = new SVMNodeBuilder(dictionary) ;
    Feature[] features = fextractor.extract(doc);
    return classifier.classify(svmNodeBuilder.getSVMVector(features)) ;
  }

  public void report(StatisticsSet map, TextDocument tdoc) {
  }
}
