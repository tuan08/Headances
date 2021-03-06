package org.headvances.nlp.dict;

import java.io.InputStream;
import java.util.HashMap;
import java.util.Map;

import org.headvances.json.JSONReader;
import org.headvances.nlp.token.tag.MeaningTag;
import org.headvances.nlp.util.StringPool;
import org.headvances.util.IOUtil;
import org.headvances.util.text.StringUtil;
/**
 * $Author: Tuan Nguyen$ 
 **/
public class WordTreeDictionary {
	final static public String[] DICT_RES = {
		"classpath:nlp/vn.lexicon.json",
		"classpath:nlp/vn.lexicon2.json"
	} ;

	private WordTree root = new WordTree() ;
	private Map<String, Entry>  entries = new HashMap<String, Entry>() ;

	public WordTreeDictionary() { }

	public WordTreeDictionary(String[] res) throws Exception { 
		for(String sel : res) {
			InputStream is = IOUtil.loadRes(sel) ;
			JSONReader reader = new JSONReader(is) ;
			Meaning meaning = null ;
			while((meaning = reader.read(Meaning.class)) != null) {
				add(meaning) ;
			}
		}
		optimize(new StringPool()) ;
	}

	public  WordTree getWordTree() { return this.root ; }

	public Entry getEntry(String name) { return entries.get(name) ; }

	public void add(Meaning meaning) {
		String[] array = StringUtil.merge(meaning.getVariant(), meaning.getName()) ;
		MeaningTag mtag = new MeaningTag(meaning) ;
		for(String sel : array) {
			String nName = sel.toLowerCase() ;
			String[] word = nName.split(" ") ;
			root.add(nName, word, 0, mtag) ;
		}
	}

	public void add(String word) {
		String nName = word.toLowerCase() ;
		String[] token = nName.split(" ") ;
		root.add(nName, token, 0, null) ;
	}
	
	public void add(String[] words) {
		for(String word :  words) {
			String nName = word.toLowerCase() ;
			String[] token = nName.split(" ") ;
			root.add(nName, token, 0, null) ;
		}
	}

	public void optimize(StringPool pool) {
		root.optimize(pool) ;
		this.entries.clear() ;
		root.collect(this.entries) ;
	}
}