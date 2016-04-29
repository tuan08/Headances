package org.headvances.nlp.ml.token;

import java.io.File;
import java.io.PrintStream;

import opennlp.maxent.GIS;
import opennlp.maxent.io.GISModelWriter;
import opennlp.maxent.io.SuffixSensitiveGISModelWriter;
import opennlp.model.AbstractModel;
import opennlp.model.EventStream;
import opennlp.model.OnePassRealValueDataIndexer;
import opennlp.model.TwoPassDataIndexer;

import org.headvances.util.TimeReporter;
/**
 * $Author: Tuan Nguyen$ 
 **/
public class OpenNLPTrainer extends MLTrainer {
  private TokenFeaturesGenerator generator ;

  private boolean     real;
  private double      SMOOTHING_OBSERVATION = 0.05;
  private boolean     USE_SMOOTHING = false;
  private TimeReporter timeReporter;

  public OpenNLPTrainer(TokenFeaturesGenerator generator) throws Exception{
    this.generator = generator ;
    this.timeReporter = new TimeReporter() ;
  }

  public void train(String[] samples, String modelFile, int iter) throws Exception {
    TimeReporter.Time trainTime = timeReporter.getTime("Train");
    trainTime.start();
    EventStream es = new OpenNLPEventStream(samples, generator);

    GIS.SMOOTHING_OBSERVATION = SMOOTHING_OBSERVATION;
    AbstractModel model = null;

    if (!real) { 
    	model = GIS.trainModel(iter, new TwoPassDataIndexer(es,0,true), true, USE_SMOOTHING, null, 0, 2); 
    } else { 
    	model = GIS.trainModel(iter, new OnePassRealValueDataIndexer(es,0,true), true, USE_SMOOTHING, null, 0, 2); 
    }
    
    GISModelWriter writer =  new SuffixSensitiveGISModelWriter(model, new File(modelFile));
    writer.persist();
    trainTime.stop();
  }

  public void setUsingRealWeight(boolean isReal){ this.real = isReal; }
  public void setSmoothingObservation(double smooth){ this.SMOOTHING_OBSERVATION = smooth; }

  public void report(PrintStream out){ timeReporter.report(out); }
}
