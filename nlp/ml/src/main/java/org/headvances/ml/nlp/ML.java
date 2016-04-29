package org.headvances.ml.nlp;

import org.headvances.util.TimeReporter;

/**
 * $Author: Tuan Nguyen$ 
 **/
public interface ML {
	public void train(String[] samples, String modelRes, int iteration) throws Exception ;
	public MLTestLog test(String[] file, boolean printToken) throws Exception ;
	public void crossValidation(String sampleDir, double splitRatio, int iteration) throws Exception ;
	public void setTimeReporter(TimeReporter timeReporter);
}
