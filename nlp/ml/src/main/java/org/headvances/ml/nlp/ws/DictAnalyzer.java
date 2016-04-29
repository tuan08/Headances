package org.headvances.ml.nlp.ws;

import java.io.File;
import java.io.PrintStream;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;

import org.headvances.nlp.NLPResource;
import org.headvances.nlp.dict.Dictionary;
import org.headvances.nlp.dict.Entry;
import org.headvances.nlp.token.IToken;
import org.headvances.nlp.token.TokenCollection;
import org.headvances.nlp.wtag.WTagDocumentReader;
import org.headvances.util.ConsoleUtil;

/**
 * $Author: Tuan Nguyen$ 
 **/
public class DictAnalyzer {
	static boolean isLetters(String token) {
		for(char sel : token.toCharArray()) {
			if(Character.isLetter(sel) || sel == ' ') continue ;
			return false ;
		}
		return true ;
	}
	
	static public void main(String[] args) throws Exception {
		Dictionary dict = NLPResource.getInstance().getDictionary(Dictionary.DICT_RES) ;
		WTagDocumentReader reader = new WTagDocumentReader() ;
		String[] files = MLWS.getFiles("d:/ml-data/wtag");
		Map<String, String> holder = new HashMap<String, String>() ;
		for(int i = 0; i < files.length; i++) {
			TokenCollection[] collection = reader.read(new File(files[i])) ;
			for(TokenCollection selCol : collection) {
				IToken[] token = selCol.getTokens() ;
				for(IToken selToken : token) {
					Entry entry = dict.getEntry(selToken.getNormalizeForm()) ;
					if(entry == null) {
						if(isLetters(selToken.getNormalizeForm())) {
							holder.put(selToken.getNormalizeForm(), selToken.getNormalizeForm()) ;
						}
					}
				}
			}
			System.out.println("Read File: " + files[i]);
		}
		PrintStream out = ConsoleUtil.getUTF8SuportOutput() ;
		String[] tokens = holder.values().toArray(new String[holder.size()]);
		Arrays.sort(tokens) ;
		for(String token : tokens) {
		  if(token.indexOf(' ') > 0) out.println(token);
		}
	}
}
