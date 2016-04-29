package org.headvances.ml.nlp;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.PrintStream;
import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.List;

import opennlp.maxent.GIS;
import opennlp.maxent.io.GISModelWriter;
import opennlp.maxent.io.SuffixSensitiveGISModelWriter;
import opennlp.model.AbstractModel;
import opennlp.model.DataReader;
import opennlp.model.EventStream;
import opennlp.model.GenericModelReader;
import opennlp.model.MaxentModel;
import opennlp.model.OnePassDataIndexer;
import opennlp.model.OnePassRealValueDataIndexer;
import opennlp.model.PlainTextFileDataReader;
import opennlp.model.RealValueFileEventStream;
import opennlp.model.TwoPassDataIndexer;

import org.headvances.ml.nlp.feature.TokenFeaturesGenerator;
import org.headvances.nlp.wtag.WTagDocumentSet;
import org.headvances.util.ConsoleUtil;
import org.headvances.util.FileUtil;
import org.headvances.util.IOUtil;
import org.headvances.util.TimeReporter;

/**
 * $Author: Tuan Nguyen$ 
 **/
abstract public class OpenNLPML implements ML {
  MaxentModel model;
  private TokenFeaturesGenerator featuresGenerator;

  private boolean real;
  private double  SMOOTHING_OBSERVATION = 0.05;
  private boolean USE_SMOOTHING = false;

  private String outDir = "target/ml";

  private TimeReporter timeReporter;

  public OpenNLPML() throws Exception {}
  
  public OpenNLPML(String modelRes, boolean loadModel) throws Exception{
    init(modelRes, loadModel);
  }

  protected void init(String modelRes, boolean loadModel) throws Exception{
    featuresGenerator = createTokenFeaturesGenerator();
    if(loadModel){
      InputStream input = IOUtil.loadRes(modelRes);
      DataReader dataReader = new PlainTextFileDataReader(input);
      model = new GenericModelReader(dataReader).getModel();
    } else {
      FileUtil.removeIfExist(outDir);
      FileUtil.mkdirs(outDir);
    }
  }
  abstract protected TokenFeaturesGenerator createTokenFeaturesGenerator() throws Exception;

  public void train(String[] samples, String modelRes, int iteration) throws Exception {
    TimeReporter.Time trainTime = timeReporter.getTime("OpenNLP: Train");
    trainTime.start();

    EventStream es = new OpenNLPWTagEventStream(samples, featuresGenerator);

    GIS.SMOOTHING_OBSERVATION = this.SMOOTHING_OBSERVATION;
    AbstractModel model = null;

    if (!real) { model = GIS.trainModel(iteration, new TwoPassDataIndexer(es,0,true), true, USE_SMOOTHING, null, 0, 2); }
    else { model = GIS.trainModel(iteration, new OnePassRealValueDataIndexer(es,0,true), true, USE_SMOOTHING, null, 0, 2); }
    
    File outputFile = new File(modelRes);
    GISModelWriter writer =  new SuffixSensitiveGISModelWriter(model, outputFile);
    writer.persist();

    trainTime.stop();
  }

  public MLTestLog test(String[] files, boolean printToken) throws Exception {
    TimeReporter.Time testTime = timeReporter.getTime("OpenNLP: Test");
    testTime.start();

    PrintStream out = ConsoleUtil.getUTF8SuportOutput() ;
    MLTestLog log  = test(out, files, printToken) ;

    DecimalFormat formater = new DecimalFormat("#.00%") ;
    double ratio = (double)log.hit/log.token ;
    System.out.println("\nCorrect Ratio For All: " + formater.format(ratio) + ", " + log.hit + "/" + log.token);

    testTime.stop();
    return log;
  }

  private MLTestLog test(PrintStream out, String[] files, boolean printToken) throws IOException{
    EventStream es = new OpenNLPWTagEventStream(files, featuresGenerator);
    int total = 0, hit = 0;
    while (es.hasNext()) {
      opennlp.model.Event e = es.next();
      String target = e.getOutcome();
      String predict = getBestPredict(e.getContext(), real);

      if(predict.equals(target)) hit++;
      total++;
    }  
    return new MLTestLog(total, hit, total - hit);
  }

  private String getBestPredict(String[] contexts, boolean real){
    double[] ocs;
    if (!real) { ocs = model.eval(contexts); }
    else {
      float[] values = RealValueFileEventStream.parseContexts(contexts);
      ocs = model.eval(contexts,values);
    }
    return model.getBestOutcome(ocs);
  }

  public void crossValidation(String sample, double splitRatio, int iteration) throws Exception {
    TimeReporter.Time splitTime = timeReporter.getTime("OpenNLP: Split");
    splitTime.start();
    WTagDocumentSet set = new WTagDocumentSet(sample, ".*\\.(tagged|wtag)") ;
    List<String> trainFiles = new ArrayList<String>() ;
    List<String> testFiles = new ArrayList<String>() ;
    String[] files = set.getFiles() ;
    for(int i = 0; i < files.length; i++) {
      double random = Math.random() ;
      if(random < splitRatio) testFiles.add(files[i]) ;
      else trainFiles.add(files[i]) ;
    }

    splitTime.stop();

    String modelRes = outDir + "/opennlp.model";
    train(trainFiles.toArray(new String[trainFiles.size()]), modelRes, iteration);

    init("file:" + modelRes, true);

    test(testFiles.toArray(new String[testFiles.size()]), true);
  }

  public void setUsingRealWeight(boolean isReal){ this.real = isReal; }
  public void setSmoothingObservation(double smooth){ this.SMOOTHING_OBSERVATION = smooth; }

  public void setTimeReporter(TimeReporter time){ this.timeReporter = time; }
  public void report(PrintStream out){
    this.timeReporter.report(out);
  }
}
