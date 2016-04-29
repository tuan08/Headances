package org.headvances.nlp.ml.classify;

import org.headvances.util.statistic.StatisticsSet;
/**
 * $Author: Tuan Nguyen$ 
 **/
public class CrossValidationReporter {
	private StatisticsSet reporter = new StatisticsSet() ;
	
	public StatisticsSet getStatisticMap() { return this.reporter ; }
	
	public void reportValidFeatures(Feature[] features) {
  	reporter.incr("Used Features", "all", 1) ;
  	report("Used Features", features);
  }
	
	public void reportFeatures(Feature[] features) {
  	reporter.incr("Extract Features", "all" , 1) ;
  	report("Extract Features", features);
  }
	
	public void report(Predict predict, String expectLabel) {
		reporter.incr("Document", "all", 1) ;
		boolean goodPredict = expectLabel.equals(predict.label) ;
		if(goodPredict) {
			reporter.incr("Document",  "correct", "all", 1) ;
		} else {
			reporter.incr("Document",  "incorrect", "all", 1) ;

			reporter.incr("Expect Incorrect", "all",              1) ;
			reporter.incr("Expect Incorrect", expectLabel, "all", 1) ;

			reporter.incr("Predict Incorrect", "all",              1) ;
			reporter.incr("Predict Incorrect", predict.label, "all", 1) ;
		}

		if(goodPredict) reporter.incr("Good Predict", "all", 1) ;
		else            reporter.incr("Bad  Predict", "all", 1) ;
		reporter.incr("Predict Probablity",  "all", 1) ;
		if(predict.probability < 0.2) {
			reporter.incr("Predict Probablity",  "0.0 - 0.2", "all", 1) ;
			if(goodPredict) reporter.incr("Good Predict",  "0.0 - 0.2", "all", 1) ;
			else            reporter.incr("Bad  Predict",  "0.0 - 0.2", "all", 1) ;
		} else if(predict.probability < 0.4) {
			reporter.incr("Predict Probablity",  "0.2 - 0.4", "all", 1) ;
			if(goodPredict) reporter.incr("Good Predict",  "0.2 - 0.4", "all", 1) ;
			else            reporter.incr("Bad  Predict",  "0.2 - 0.4", "all", 1) ;
		} else if(predict.probability < 0.6) {
			reporter.incr("Predict Probablity",  "0.4 - 0.6", "all", 1) ;
			if(goodPredict) reporter.incr("Good Predict",  "0.4 - 0.6", "all", 1) ;
			else            reporter.incr("Bad  Predict",  "0.4 - 0.6", "all", 1) ;
		} else if(predict.probability < 0.8) {
			reporter.incr("Predict Probablity",  "0.6 - 0.8", "all", 1) ;
			if(goodPredict) reporter.incr("Good Predict",  "0.6 - 0.8", "all", 1) ;
			else            reporter.incr("Bad  Predict",  "0.6 - 0.8", "all", 1) ;
		} else {
			reporter.incr("Predict Probablity",  "0.8 - 1.0", "all", 1) ;
			if(goodPredict) reporter.incr("Good Predict",  "0.8 - 1.0", "all", 1) ;
			else            reporter.incr("Bad  Predict",  "0.8 - 1.0", "all", 1) ;
		}
	}
	
	void report(String category, Feature[] features){
    int num = features.length;
    if (num <= 100){
      int start = num/10 * 10 + 1;
      int end = (num/10 + 1) * 10;
      String name = start + " - " + end;
      reporter.incr(category, name, "all", 1);
    } else {
      reporter.incr(category, "> 100" , "all",1); 
    }
  }
}