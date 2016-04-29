package org.headvances.ml.feature;

public interface FeatureGenerator<T> {
	public void generate(FeatureHolder holder, T doc) ;
}