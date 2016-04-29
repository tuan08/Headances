package org.headvances.ml.nlp.opinion;

import java.util.ArrayList;
import java.util.List;

import org.headvances.ml.Predict;
import org.headvances.nlp.query2.match.MatcherResourceFactory;
import org.headvances.nlp.query2.match.RuleMatch;
import org.headvances.nlp.token.IToken;
import org.headvances.nlp.token.TextSegmenter;
import org.headvances.nlp.token.TokenCollection;
import org.headvances.nlp.token.analyzer.CommonTokenAnalyzer;
import org.headvances.nlp.token.analyzer.PunctuationTokenAnalyzer;
import org.headvances.nlp.token.analyzer.SentenceSplitterAnalyzer;
import org.headvances.nlp.token.analyzer.TokenAnalyzer;
import org.headvances.nlp.token.analyzer.USDTokenAnalyzer;
import org.headvances.nlp.token.analyzer.VNDTokenAnalyzer;
import org.headvances.nlp.token.analyzer.VNMobileTokenAnalyzer;
import org.headvances.nlp.token.analyzer.VNPhoneTokenAnalyzer;
import org.headvances.util.text.StringUtil;
import org.headvances.util.text.VietnameseUtil;

/**
 * $Author: Tuan Nguyen$ 
 **/
public class OpinionExtractor {
  private String[] keyword ;
  private TextSegmenter textSegmenter ;
  private OpinionCandidateExtractor candidateExtractor ;
  private OpinionCategoryDetector   categoryDetector ;
  private OpinionMaxentClassifier   classifier ;

  public OpinionExtractor(String[] keyword) throws Exception {
    this.keyword = keyword ;
    TokenAnalyzer[] analyzer = new TokenAnalyzer[] {
        new PunctuationTokenAnalyzer(), 
        new CommonTokenAnalyzer(), 
        new VNMobileTokenAnalyzer() ,
        new VNPhoneTokenAnalyzer() ,
        new USDTokenAnalyzer(),
        new VNDTokenAnalyzer(),
    };
    textSegmenter = new TextSegmenter(analyzer) ;
    MatcherResourceFactory resFactory = new MatcherResourceFactory();
    candidateExtractor = new OpinionCandidateExtractor(StringUtil.joinStringArray(keyword, ", "), resFactory);
    classifier = new OpinionMaxentClassifier(keyword) ;
    categoryDetector   = new OpinionCategoryDetector(resFactory);
  }

  public String[] getKeyword() { return this.keyword ; }
  public TextSegmenter getTextSegmenter() { return this.textSegmenter; }

  public List<Opinion> loadTrainingDoc(OpinionDocument doc) throws Exception {
    IToken[] tokens = textSegmenter.segment(doc.getText()) ;
    TokenCollection[] sentence = SentenceSplitterAnalyzer.INSTANCE.analyze(tokens);
    List<Opinion> holder = new ArrayList<Opinion>() ;
    for(int i = 0; i < sentence.length; i++) {
      Opinion opinion = new Opinion();
      RuleMatch rMatch = new RuleMatch(null, sentence[i]);
      opinion.setOpinion(rMatch.getTokenCollection().getNormalizeForm());
      opinion.setRuleMatch(rMatch);
      holder.add(opinion) ;
    }
    return holder ;
  }

  public List<Opinion> extract(OpinionDocument doc) throws Exception {
    IToken[] tokens = textSegmenter.segment(doc.getText()) ;
    TokenCollection[] sentence = SentenceSplitterAnalyzer.INSTANCE.analyze(tokens);

    List<Opinion> holder = new ArrayList<Opinion>() ;
    for(int i = 0; i < sentence.length; i++) {
      Opinion opinion = extract(sentence[i]) ;
      if(opinion != null) {
        opinion.computeId(doc.getDocumentId()) ;
        opinion.setRuleMatch(null);
        holder.add(opinion) ; 
      }
    }
    return holder ;
  }

  public Opinion extract(TokenCollection sentence) throws Exception {
    Opinion opinion = candidateExtractor.extract(sentence) ;
    if(opinion != null) {
      if(!isVNLanguage(opinion)) return null ;
      
      Predict predict = classifier.predict(opinion) ;
      String label = predict.label ;
      if("i".equals(label)) {
        if(StringUtil.isIn("candidate:comment", opinion.getTag())) {
          label = "0" ;
        }
      }
      opinion.setLabel(label) ;
      
      categoryDetector.detect(opinion, sentence) ;
      return opinion ;
    }
    return null ;
  }

  private boolean isVNLanguage(Opinion op) {
    RuleMatch rmatch = op.getRuleMatch() ;
    if(rmatch == null) return  false ;
    IToken[] tokens = rmatch.getTokenCollection().getTokens() ;
    int count = 0 ;
    for(IToken sel : tokens) {
      if(sel.getWord().length > 1) {
        if(VietnameseUtil.containVietnameseCharacter(sel.getNormalizeForm())) return true ;
      } else {
        if(VietnameseUtil.containVietnameseCharacter(sel.getNormalizeForm())) {
          count++ ;
          if(count > 1) return true; 
        }
      }
    }
    return false ;
  }
}
