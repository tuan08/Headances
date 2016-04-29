package org.headvances.search.rest.analyse;

import java.util.ArrayList;
import java.util.List;

import org.headvances.ml.nlp.opinion.Opinion;
import org.headvances.ml.nlp.opinion.OpinionDocument;
import org.headvances.ml.nlp.opinion.OpinionExtractor;
import org.headvances.nlp.ml.classify.Predict;
import org.headvances.nlp.ml.classify.text.MaxEntClassifier;
import org.headvances.nlp.ml.classify.text.TextDocument;
import org.headvances.util.JVMInfoUtil;
import org.headvances.util.text.StringUtil;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;

@Controller
@RequestMapping(value="/rest/analyse")
public class AnalyseController {
	private MaxEntClassifier classifier ;
	private WordAnalyzer wordAnalyzer ;
	
	public AnalyseController() throws Exception {
    classifier = new MaxEntClassifier();
    wordAnalyzer = new WordAnalyzer() ;
	}
	
	@RequestMapping(method=RequestMethod.POST)
	public @ResponseBody AnalyseResult analyse(Request request) {
		System.out.println(JVMInfoUtil.getMemoryUsageInfo()); 
		AnalyseResult result = new AnalyseResult() ;
		result.setRequest(request) ;
		try {
			analyseTextCategory(result, request.getText()) ;
			analyseWord( request, result) ;
			extractOpinions(result, request.getText()) ;
		} catch(Exception ex) {
			ex.printStackTrace() ;
		}
		return result ;
	}

	public void analyseTextCategory(AnalyseResult result, String text) throws Exception {
		long startTime = System.currentTimeMillis() ;
		TextDocument textdoc = new TextDocument("", "", text);
		Predict best = classifier.predict(textdoc);
		long time = System.currentTimeMillis() - startTime ;
		result.setTextClassify(best.label, best.probability, time) ;
	}
	
	private void analyseWord(Request request, AnalyseResult result) throws Exception {
		long startTime = System.currentTimeMillis() ;
		String[] line = wordAnalyzer.analyze(request.getText(), request.getAlgorithm(), request.getShow()) ;
		long time = System.currentTimeMillis() - startTime ;
		result.setTextAnalyse(line, time) ;
	}
	
	public void extractOpinions(AnalyseResult result, String text) throws Exception {
		String entities = result.getRequest().getEntities() ;
		String[] entity = StringUtil.toStringArray(entities) ;
		OpinionExtractor opinionExtractor = new OpinionExtractor(entity) ;
		String[] block = text.split("\n") ;
		long startTime = System.currentTimeMillis() ;
		List<Opinion> holder = new ArrayList<Opinion>() ;
		for(int i  = 0 ; i < block.length; i++) {
			List<Opinion> opinions = opinionExtractor.extract(new OpinionDocument("analyse", block[i])) ;
			for(int j = 0; j < opinions.size(); j++) {
				holder.add(opinions.get(j)) ;
			}
		}
		result.setExtractOpinions(holder) ;
		long time = System.currentTimeMillis() - startTime ;
	}
	
	public TextDocument createTextDocument(String text){
		TextDocument textdoc = new TextDocument("", "", text);
		return textdoc;
	}
}