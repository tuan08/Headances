package org.headvances.nlp.ml.token;

/**
 * $Author: Tuan Nguyen$ 
 **/
abstract public class MLTester {
	abstract public MLTestLog test(String wtagFile) throws Exception ;
	
	static public class MLTestLog {
		int token = 0;
		int hit   = 0;
		int miss  = 0;

		public MLTestLog(int token, int hit, int miss) {
			this.token = token ;
			this.hit   = hit ;
			this.miss  = miss ;
		}

		public void merge(MLTestLog log) {
			token += log.token ;
			hit   += log.hit ;
			miss  += log.miss ;
		}
		
		public int getToken(){ return this.token; }
		public int getHit(){ return this.hit; }
		public int getMiss(){ return this.miss; }
	}
}