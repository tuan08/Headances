package org.headvances.nlp.ml.pos;

import org.headvances.nlp.ml.token.ML;
/**
 * $Author: Tuan Nguyen$ 
 **/
public class POSML {
	static public void main(String[] args) throws Exception {
		if(args == null || args.length == 0) { 
			args = new String[] {
					"-data",     "d:/ml-data/pos",
					"-model",    "target/pos.model",
					"-lib",      "opennlp",
					"-iteration", "1000"
					//,"-train"
			};
		}
		ML.run(args, new POSTokenFeaturesGenerator()) ;
	}
}