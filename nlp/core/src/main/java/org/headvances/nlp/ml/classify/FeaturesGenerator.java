package org.headvances.nlp.ml.classify;

/**
 * $Author: Tuan Nguyen$ 
 **/
public class FeaturesGenerator<T> {
	protected FeatureGenerator<T>[] fgenerator ;

	public void add(FeatureGenerator<T> fgenerator) {
		if(this.fgenerator == null) {
			this.fgenerator = new FeatureGenerator[] { fgenerator } ;
		} else {
			FeatureGenerator<T>[] temp = new FeatureGenerator[this.fgenerator.length + 1] ;
			System.arraycopy(this.fgenerator, 0, temp, 0, this.fgenerator.length) ;
			temp[this.fgenerator.length] = fgenerator ;
			this.fgenerator = temp ;
		}
	}

	public FeatureHolder generate(T doc) {
		FeatureHolder holder = new FeatureHolder(1) ;
		for(int j = 0; j < fgenerator.length; j++) {
			fgenerator[j].generate(holder, doc) ;
		}
		return holder ;
	}
}