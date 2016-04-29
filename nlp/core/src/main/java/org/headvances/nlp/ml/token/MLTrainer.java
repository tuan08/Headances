package org.headvances.nlp.ml.token;

/**
 * $Author: Tuan Nguyen$ 
 **/
abstract public class MLTrainer {
	abstract public void train(String[] dataFile, String outputFile, int iteration) throws Exception ;
}