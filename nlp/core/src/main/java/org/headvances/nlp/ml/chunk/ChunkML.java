package org.headvances.nlp.ml.chunk;

import org.headvances.nlp.ml.token.ML;
/**
 * $Author: Tuan Nguyen$ 
 **/
public class ChunkML {
	static public void main(String[] args) throws Exception {
		if(args == null || args.length == 0) { 
			args = new String[] {
					"-data",     "d:/ml-data/chunk",
					"-model",    "target/chunk.model",
					"-lib",      "crf",
					"-iteration", "300"
					//,"-train"
			};
		}
		ML.run(args, new ChunkTokenFeaturesGenerator()) ;
	}
}