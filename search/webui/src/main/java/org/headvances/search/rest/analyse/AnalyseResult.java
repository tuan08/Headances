package org.headvances.search.rest.analyse;

import java.util.List;

import org.headvances.ml.nlp.opinion.Opinion;
/**
 * $Author: Tuan Nguyen$ 
 **/
public class AnalyseResult {
	private Request request ;
	private TextClassify textClassify ;
	private TextAnalyse  textAnalyse ;
	private List<Opinion> extractOpinions ;
	
	public Request getRequest() { return request; }
	public void    setRequest(Request request) { this.request = request; }

	public TextClassify getTextClassify() { return this.textClassify ; }
	
	public void setTextClassify(String category, double probability, long time) {
		this.textClassify = new TextClassify(category, probability, time) ;
	}
	
	public TextAnalyse getTextAnalyse() { return this.textAnalyse ; }
	public void setTextAnalyse(String[] line, long time) {
		this.textAnalyse = new TextAnalyse(line, time) ;
	}
	
	public List<Opinion> getExtractOpinions() { return this.extractOpinions ; }
	public void setExtractOpinions(List<Opinion> opinions) { this.extractOpinions = opinions ; }
	
	static public class TextClassify {
		private String category ;
		private double probability ;
		private long   analyzedTime ;
		
		public TextClassify() {} 
		
		public TextClassify(String category, double probability, long analyzedTime) {
			this.category = category ;
			this.probability = probability ;
			this.analyzedTime = analyzedTime ;
		}
		
		public String getCategory() { return category; }
		public void setCategory(String category) { this.category = category; }
		
		public double getProbability() { return this.probability ; }
		public void setProbability(double probability) { this.probability = probability ; }
		
		public long getAnalyzedTime() { return analyzedTime; }
		public void setAnalyzedTime(long analyzedTime) { this.analyzedTime = analyzedTime; }
	}
	
	static public class TextAnalyse {
		private long     analyzedTime ;
		private String[] line ;
		
		public TextAnalyse() { }
		
		public TextAnalyse(String[] line, long analyzedTime) {
			this.line = line ;
			this.analyzedTime = analyzedTime ;
		}

		public long getAnalyzedTime() { return analyzedTime; }
		public void setAnalyzedTime(long analyzedTime) { this.analyzedTime = analyzedTime; }

		public String[] getLine() { return line; }
		public void setLine(String[] line) { this.line = line; }
	}
}
