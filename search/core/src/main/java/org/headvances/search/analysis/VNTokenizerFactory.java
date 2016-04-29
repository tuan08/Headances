package org.headvances.search.analysis;

import java.io.Reader;

import org.apache.lucene.analysis.Tokenizer;
import org.elasticsearch.common.inject.Inject;
import org.elasticsearch.common.inject.assistedinject.Assisted;
import org.elasticsearch.common.settings.Settings;
import org.elasticsearch.index.Index;
import org.elasticsearch.index.analysis.AbstractTokenizerFactory;
import org.elasticsearch.index.settings.IndexSettings;

public class VNTokenizerFactory extends AbstractTokenizerFactory {
	private NLPTokenizer nlpTokenizer ;
	
	@Inject 
	public VNTokenizerFactory(Index index, @IndexSettings Settings indexSettings,
			                      @Assisted String name, @Assisted Settings settings) throws Exception {
		super(index, indexSettings, name, settings);
	  nlpTokenizer = new NLPTokenizer() ;
	}

	@Override 
	public Tokenizer create(Reader input) {
		return new VNTokenizer(input, nlpTokenizer);
	}
}