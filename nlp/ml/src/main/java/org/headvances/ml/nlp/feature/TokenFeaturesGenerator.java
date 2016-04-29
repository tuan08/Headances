package org.headvances.ml.nlp.feature;

import java.io.FileOutputStream;
import java.io.PrintStream;

import org.headvances.nlp.token.IToken;
import org.headvances.nlp.wtag.WTagDocumentReader;
import org.headvances.util.ConsoleUtil;
/**
 * $Author: Tuan Nguyen$ 
 **/
public class TokenFeaturesGenerator {
	protected FeatureGenerator[] fgenerator ;
	protected FeatureGenerator   targetFeatureGenrator ;
	private   WTagDocumentReader reader ;

	public TokenFeaturesGenerator(WTagDocumentReader reader) {
		this.reader = reader ;
	}

	public WTagDocumentReader getDocumentReader() { return this.reader ; }
	public void setDocumentReader(WTagDocumentReader reader) { this.reader = reader ; }

	public void add(FeatureGenerator fgenerator) {
		if(this.fgenerator == null) {
			this.fgenerator = new FeatureGenerator[] {fgenerator} ;
		} else {
			FeatureGenerator[] temp = new FeatureGenerator[this.fgenerator.length + 1] ;
			System.arraycopy(this.fgenerator, 0, temp, 0, this.fgenerator.length) ;
			temp[this.fgenerator.length] = fgenerator ;
			this.fgenerator = temp ;
		}
	}

	public void setTargetFeatureGenerator(FeatureGenerator fgenerator) {
		this.targetFeatureGenrator = fgenerator ;
	}

	public TokenFeatures[] generate(IToken[] token, boolean generateTarget) {
		TokenFeatures[] tokenFeatures = new TokenFeatures[token.length] ;
		TokenFeatureHolder featureHolder = new TokenFeatureHolder() ;
		for(int i = 0; i < token.length; i++) {
			if(ignoreToken(token[i])) {
				onIgnoreToken(token[i], featureHolder, generateTarget) ;
			} else {
				for(int j = 0; j < fgenerator.length; j++) {
					fgenerator[j].generate(token, i, featureHolder) ;
				}
				if(generateTarget && this.targetFeatureGenrator != null) {
					this.targetFeatureGenrator.generate(token, i, featureHolder) ;
				}
			}
			tokenFeatures[i] =
				new TokenFeatures(featureHolder.getFeatures(), featureHolder.getTargetFeature()) ;
			featureHolder.reset() ;
		}
		return tokenFeatures ;
	}

	public TokenFeatures[] createValueTarget(IToken[] token) {
		TokenFeatures[] tfeatures = new TokenFeatures[token.length] ;
		TokenFeatureHolder featureHolder = new TokenFeatureHolder() ;
		for(int i = 0; i < token.length; i++) {
			this.targetFeatureGenrator.generate(token, i, featureHolder) ;
			tfeatures[i] = new TokenFeatures(new String[]{token[i].getOriginalForm()}, featureHolder.getTargetFeature()) ;
			featureHolder.reset() ;
		}
		return tfeatures ;
	}

	protected void onIgnoreToken(IToken token, TokenFeatureHolder featureHolder,  boolean target) {
		featureHolder.addFeature("token:ignore") ;
		if(target && this.targetFeatureGenrator != null) {
			featureHolder.addFeature("O") ;
		}
	}
	
	protected boolean ignoreToken(IToken token) { return false ; }
	
	public void printFeatures(IToken[] token) throws Exception {
		PrintStream out = ConsoleUtil.getUTF8SuportOutput() ;
		printFeatures(out, token) ;
	}
	
	public void printFeatures(String file, IToken[] token) throws Exception {
		PrintStream out = new PrintStream(new FileOutputStream(file), true, "UTF-8") ;
		printFeatures(out, token) ;
		out.close() ;
	}

	public void printFeatures(PrintStream out, IToken[] token) throws Exception {
		TokenFeatures[] tokenFeatures = generate(token, false) ;
		for(int i = 0; i < tokenFeatures.length; i++) {
			String[] feature = tokenFeatures[i].getFeatures();
			for(int j = 0; j < feature.length; j++) {
				if(j > 0) out.append(' ') ;
				out.append(feature[j]) ;
			}
			out.append(' ').append(tokenFeatures[i].getTargetFeature()).println() ;
		}
	}
}
