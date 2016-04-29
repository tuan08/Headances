package org.headvances.nlp.ml.token;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.List;

import opennlp.model.GenericModelReader;
import opennlp.model.MaxentModel;
import opennlp.model.PlainTextFileDataReader;
import opennlp.tools.util.BeamSearch;
import opennlp.tools.util.BeamSearchContextGenerator;
import opennlp.tools.util.Sequence;

import org.headvances.nlp.token.IToken;
import org.headvances.nlp.token.TokenException;
import org.headvances.nlp.token.analyzer.TokenAnalyzer;
import org.headvances.util.IOUtil;

abstract public class OpenNLPTokenAnalyzer implements TokenAnalyzer {
	public int DEFAULT_BEAM_SIZE = 3;
	private TokenFeaturesGenerator generator ;
	private MaxentModel model;

	public OpenNLPTokenAnalyzer(TokenFeaturesGenerator generator, String modelRes) throws Exception{
		this.generator = generator ;
		InputStream input = IOUtil.loadRes(modelRes);
		BufferedReader reader = new BufferedReader(new InputStreamReader(input, "UTF-8")) ;
		this.model = new GenericModelReader(new PlainTextFileDataReader(reader)).getModel();
	}

	public IToken[] analyze(IToken[] token) throws TokenException {
		if(token.length == 0) return token;
		TokenFeatures[] tfeatures = generator.generate(token, false) ; ;
		ChunkSearchGenerator generator = new ChunkSearchGenerator(tfeatures);    
		BeamSearch<IToken> beam = 
			new BeamSearch<IToken>(DEFAULT_BEAM_SIZE, generator, model, null, 0);
		Sequence bestSequence = beam.bestSequence(token, null);
		List<String> outcomes = bestSequence.getOutcomes();    

		if(outcomes.size() != token.length) {
			throw new TokenException("Failed to get outcomes");
		}

		int i = 0;
		while(i < token.length) {
			String target = outcomes.get(i);
			tag(token[i], target) ;
			i++ ;
		}
		return token;
	}
	
	abstract protected void tag(IToken token, String predict)  ;
	
	class ChunkSearchGenerator implements BeamSearchContextGenerator<IToken> {
		private TokenFeatures[] tfeatures;

		public ChunkSearchGenerator(TokenFeatures[] tfeatures) {
			this.tfeatures = tfeatures; 
		}    

		public String[] getContext(int index, IToken[] sequence, String[] priorDecisions, Object[] additionalContext) {
			return tfeatures[index].getFeatures();
		}
	}
}
