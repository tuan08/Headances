package org.headvances.search.analysis;

import java.io.Reader;

import org.apache.lucene.analysis.Analyzer;
import org.apache.lucene.analysis.TokenStream;
/**
 * $Author: Tuan Nguyen$ 
 **/
final public class VNAnalyzer extends Analyzer {
	public TokenStream tokenStream(String fieldName, Reader reader) {
		return new WordTokenStream(new String[] { "token1", "token2", "token3", "token4" }) ;
	}
}
