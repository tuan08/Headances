package org.headvances.search.rest.analyse;

import java.util.List;

import org.headvances.ml.nlp.chunk.ChunkTag;
import org.headvances.nlp.dict.Dictionary;
import org.headvances.nlp.token.IToken;
import org.headvances.nlp.token.TextSegmenter;
import org.headvances.nlp.token.TokenCollection;
import org.headvances.nlp.token.analyzer.CommonTokenAnalyzer;
import org.headvances.nlp.token.analyzer.PunctuationTokenAnalyzer;
import org.headvances.nlp.token.analyzer.TokenAnalyzer;
import org.headvances.nlp.token.tag.CurrencyTag;
import org.headvances.nlp.token.tag.DateTag;
import org.headvances.nlp.token.tag.DigitTag;
import org.headvances.nlp.token.tag.EntityTag;
import org.headvances.nlp.token.tag.MeaningTag;
import org.headvances.nlp.token.tag.NumberTag;
import org.headvances.nlp.token.tag.PhoneTag;
import org.headvances.nlp.token.tag.PosTag;
import org.headvances.nlp.token.tag.TimeTag;
import org.headvances.nlp.token.tag.TokenTag;
import org.headvances.nlp.ws.WordTreeMatchingAnalyzer;
import org.headvances.util.text.StringUtil;
/**
 * $Author: Tuan Nguyen$ 
 **/
public class TokenCollectionRenderer {
	static String TD_VALUE  = "<td style='border: 1px solid gray; white-space: nowrap; text-align: center; padding: 1px'>" ;
	static String TD_HEADER = "<td style='border: 1px solid gray; white-space: nowrap; padding: 1px 10px'>" ;
	
	private TextSegmenter textSegmenter ;
	
	public TokenCollectionRenderer(Dictionary dict) throws Exception {
		TokenAnalyzer[] analyzer = new TokenAnalyzer[] {
			new PunctuationTokenAnalyzer(), new CommonTokenAnalyzer(),
			new WordTreeMatchingAnalyzer(dict)
		};
		textSegmenter = new TextSegmenter(analyzer) ;
	}
	
	public String render(TokenCollection wsCollection, TokenCollection chunkCollection, 
			                 TokenCollection entityCollection) throws Exception  {
		IToken[] wtokens = wsCollection.getSingleTokens() ;
		StringBuilder b = new StringBuilder() ;
		b.append("<pre>") ;
		b.append("<table style='border-collapse: collapse; width: auto'>");
		b.append("<tr>"); 
		b.append(TD_HEADER).append("Sentence").append("</td>") ;
		b.append("<td style='border: 1px solid gray; padding: 2px' colspan='").append(wtokens.length).append("'>");
		b.append(wsCollection.getOriginalForm()); 
		b.append("</td>") ;
		b.append("</tr>") ;
		
		IToken[] splitByDict = textSegmenter.segment(wsCollection.getOriginalForm()) ;
		b.append("<tr>"); 
		b.append(TD_HEADER).append("Dictionary").append("</td>") ;
		b.append("<td style='border: 1px solid gray; padding: 2px' colspan='").append(wtokens.length).append("'>");
		for(int i = 0; i < splitByDict.length; i++) {
			String word = splitByDict[i].getOriginalForm() ;
			word = word.replace(' ', '_') ;
			if(i > 0) b.append(' ') ;
			b.append(word) ;
		}
		b.append("</td>") ;
		b.append("</tr>") ;
		renderWS(b, wtokens) ;
		renderTokenType(b, wtokens) ;
		renderChunk(b, chunkCollection) ;
		renderEntity(b, entityCollection.getTokens()) ;
		
		b.append("</table>");
		b.append("</pre>") ;
		return b.toString() ;
	}
	
	private void renderWS(StringBuilder b, IToken[]  token) {
		b.append("<tr>");
		b.append(TD_HEADER).append("Tokenize").append("</td>") ;
		for(int j = 0; j < token.length; j++) {
			String ws = token[j].getOriginalForm() ;
			b.append(TD_VALUE).append(ws).append("</td>") ;
		}
		b.append("</tr>");
	}
	
	private void renderTokenType(StringBuilder b, IToken[]  token) {
		b.append("<tr>");
		b.append(TD_HEADER).append("Type").append("</td>") ;
		for(int i = 0; i < token.length; i++) {
			List<TokenTag> tags = token[i].getTag() ;
			String type = null ;
			if(tags != null) {
				for(int j = tags.size() - 1; j >= 0; j--) {
					TokenTag tag = tags.get(j) ;
					if(tag instanceof DigitTag)         type = "digit" ;
					else if(tag instanceof NumberTag)   type = "num" ;
//					else if(tag instanceof CurrencyTag) type = "curency" ;
					else if(tag instanceof MeaningTag)  type = "dict" ;
					else if(tag instanceof PhoneTag)    type = "phone" ;
					else if(tag instanceof DateTag)     type = "date" ;
					else if(tag instanceof TimeTag)     type = "time" ;
					if(type != null) break ;
				}
			}
			if(type == null) type = "" ;
			b.append(TD_VALUE).append(type).append("</td>") ;
		}
		b.append("</tr>");
	}
	
	private void renderChunk(StringBuilder b, TokenCollection col) {
		IToken[]  token = col.getSingleTokens() ;
		b.append("<tr>");
		b.append(TD_HEADER).append("POS").append("</td>") ;
		for(int j = 0; j < token.length; j++) {
			PosTag ptag = token[j].getFirstTagType(PosTag.class) ;
			b.append(TD_VALUE) ;
			if(ptag != null) {
				String value = StringUtil.joinStringArray(ptag.getPosTag()) ;
				value = value.replace("pos:", "") ;
				if("Mrk".equals(value)) value = token[j].getOriginalForm() ;
				b.append(value); 
			}
			b.append("</td>") ;
		}
		b.append("</tr>");
		
		token = col.getTokens() ;
		b.append("<tr>");
		b.append(TD_HEADER).append("Chunk").append("</td>") ;
		for(int j = 0; j < token.length; j++) {
			ChunkTag chunkTag = token[j].getFirstTagType(ChunkTag.class) ;
			int colspan = 1 ;
			if(token[j] instanceof TokenCollection) {
				colspan = ((TokenCollection)token[j]).getTokens().length ;
			}
			b.append("<td style='border: 1px solid gray; white-space: nowrap; text-align: center; padding: 1px' colspan='").append(colspan).append("'>");
			if(chunkTag != null) {
				String tag = chunkTag.getTagValue() ;
				b.append(tag.substring("chunk:".length())); 
			}
			b.append("</td>") ;
		}
		b.append("</tr>");
	}
	
	private void renderEntity(StringBuilder b, IToken[]  token) {
		b.append("<tr>");
		b.append(TD_HEADER).append("Entity").append("</td>") ;
		
		for(int j = 0; j < token.length; j++) {
			String entityType = null ;
			int colspan = 1 ;
			if(token[j] instanceof TokenCollection) {
				colspan = ((TokenCollection)token[j]).getTokens().length ;
			}
			List<TokenTag> tags = token[j].getTag() ;
			if(tags != null) {
				for(int k = tags.size() - 1; k >= 0; k--) {
					TokenTag tag = tags.get(k) ;
					if(tag instanceof CurrencyTag)    entityType = "curency" ;
					else if(tag instanceof DateTag)   entityType = "date" ;
					else if(tag instanceof TimeTag)   entityType = "time" ;
					else if(tag instanceof PhoneTag)  entityType = "phone" ;
					else if(tag instanceof EntityTag) entityType = "loc" ;
					if(entityType != null) break ;
				}
			}
			b.append("<td style='border: 1px solid gray; white-space: nowrap; text-align: center; padding: 1px' colspan='").append(colspan).append("'>");
			if(entityType != null) b.append(entityType); 
			b.append("</td>") ;
		}
		b.append("</tr>");
	}
}