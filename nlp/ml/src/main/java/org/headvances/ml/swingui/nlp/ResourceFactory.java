package org.headvances.ml.swingui.nlp;

import org.headvances.ml.nlp.ent.EntitySuggestTokenAnalyzer;
import org.headvances.ml.nlp.ent.OpenNLPEntityTokenAnalyzer;
import org.headvances.ml.nlp.pos.OpenNLPPOSTokenAnalyzer;
import org.headvances.ml.nlp.pos.CRFPOSTokenAnalyzer;
import org.headvances.ml.nlp.ws.CRFWSTokenAnalyzer;
import org.headvances.ml.nlp.ws.OpenNLPWSTokenAnalyzer;
import org.headvances.nlp.NLPResource;
import org.headvances.nlp.dict.Dictionary;
import org.headvances.nlp.dict.DictionaryTaggingAnalyzer;
import org.headvances.nlp.dict.UnknownWordTokenSplitter;
import org.headvances.nlp.token.IToken;
import org.headvances.nlp.token.TextSegmenter;
import org.headvances.nlp.token.TokenException;
import org.headvances.nlp.token.analyzer.CommonTokenAnalyzer;
import org.headvances.nlp.token.analyzer.PunctuationTokenAnalyzer;
import org.headvances.nlp.token.analyzer.TokenAnalyzer;
import org.headvances.nlp.ws.NGramStatisticWSTokenAnalyzer;
import org.headvances.nlp.ws.WordTreeMatchingAnalyzer;
import org.headvances.nlp.wtag.WTagBoundaryTag;
import org.headvances.nlp.wtag.WTagDocumentReader;
import org.headvances.util.text.StringUtil;

public class ResourceFactory {
  static private WTagDocumentReader wtagReader ;
  
  static private TextSegmenter      opennlpTextSegmenter ;
  static private TextSegmenter      crfTextSegmenter ;
  static private TextSegmenter      statisticTextSegmenter ;
  static private TextSegmenter      longestMatchingTextSegmenter ;

  static {
    try {
      Dictionary dict = NLPResource.getInstance().getDictionary(Dictionary.DICT_RES);
      TokenAnalyzer dictTagger = new DictionaryTaggingAnalyzer(dict) ;
      TokenAnalyzer posAnalyzer = new CRFPOSTokenAnalyzer() ;

      TokenAnalyzer entitySuggestAnalyzer =  new EntitySuggestTokenAnalyzer(new OpenNLPEntityTokenAnalyzer());

      TokenAnalyzer[] analyzer = {
          new CommonTokenAnalyzer(), new PunctuationTokenAnalyzer(),
          dictTagger, 
          posAnalyzer,
      };

      wtagReader = new WTagDocumentReader() ;
      wtagReader.setTokenAnalyzer(analyzer) ;

      final TokenAnalyzer bTagger = new TokenAnalyzer() {
        public IToken[] analyze(IToken[] token) throws TokenException {
          for(IToken sel : token) sel.add(new WTagBoundaryTag(StringUtil.EMPTY_ARRAY)) ;
          return token;
        }
      };

      TokenAnalyzer[] opennlpTextSegmenterAnalyzer = {
          new PunctuationTokenAnalyzer(), new CommonTokenAnalyzer(),
          new OpenNLPWSTokenAnalyzer(), //new CRFWSTokenAnalyzer(),
          dictTagger,
          new UnknownWordTokenSplitter(dict),
          bTagger,
          new OpenNLPPOSTokenAnalyzer(),
          entitySuggestAnalyzer
      };

      TokenAnalyzer[] crfTextSegmenterAnalyzer = {
          new PunctuationTokenAnalyzer(), new CommonTokenAnalyzer(),
          new CRFWSTokenAnalyzer(),
          dictTagger,
          new UnknownWordTokenSplitter(dict),
          bTagger,
          posAnalyzer,
          entitySuggestAnalyzer
      };
      
      TokenAnalyzer[] statisticTextSegmenterAnalyzer = {
          new PunctuationTokenAnalyzer(), new CommonTokenAnalyzer(),
          new NGramStatisticWSTokenAnalyzer(),
          dictTagger,
          new UnknownWordTokenSplitter(dict),
          bTagger,
          posAnalyzer,
          entitySuggestAnalyzer
      };

      TokenAnalyzer[] longestMatchingTextSegmenterAnalyzer = {
          new PunctuationTokenAnalyzer(), new CommonTokenAnalyzer(),
          new WordTreeMatchingAnalyzer(dict),
          dictTagger,
          new UnknownWordTokenSplitter(dict),
          bTagger,
          posAnalyzer,
          entitySuggestAnalyzer
      };

      opennlpTextSegmenter = new TextSegmenter(opennlpTextSegmenterAnalyzer) ;
      crfTextSegmenter = new TextSegmenter(crfTextSegmenterAnalyzer) ;
      statisticTextSegmenter = new TextSegmenter(statisticTextSegmenterAnalyzer) ;
      longestMatchingTextSegmenter = new TextSegmenter(longestMatchingTextSegmenterAnalyzer) ;
    } catch(Exception ex) {
      ex.printStackTrace() ;
    }
  }

  static public WTagDocumentReader getWTagDocumentReader() { return wtagReader ; }

  static public TextSegmenter getOpennlpTextSegmenter() { return opennlpTextSegmenter ; }

  static public TextSegmenter getCRFTextSegmenter() { return crfTextSegmenter ; }

  static public TextSegmenter getStatisticTextSegmenter() { return statisticTextSegmenter ; }
  
  static public TextSegmenter getLongestMatchingTextSegmenter() { return longestMatchingTextSegmenter ; }

}