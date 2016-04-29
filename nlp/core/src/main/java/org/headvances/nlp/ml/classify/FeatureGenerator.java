package org.headvances.nlp.ml.classify;

public interface FeatureGenerator<T> {
	public void generate(FeatureHolder holder, T doc) ;
}