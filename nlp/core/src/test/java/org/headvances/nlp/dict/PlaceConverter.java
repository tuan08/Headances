package org.headvances.nlp.dict;

import java.io.InputStream;

import org.headvances.json.JSONReader;
import org.headvances.json.JSONWriter;
import org.headvances.util.IOUtil;
import org.headvances.util.text.StringUtil;

/**
 * $Author: Tuan Nguyen$ 
 **/
public class PlaceConverter {
	static public void main(String[] args) throws Exception {
		InputStream is = IOUtil.loadRes("file:target/vn.place.json") ;
  	JSONReader reader = new JSONReader(is) ;
  	JSONWriter writer = new JSONWriter("target/vn.place2.json") ;
  	Meaning meaning = null ;
  	while((meaning = reader.read(Meaning.class)) != null) {
  		String name = meaning.getName() ;
  		if(name.startsWith("thị trấn ")) {
  			name = name.substring("thị trấn ".length()) ;
  			meaning.setName(name) ;
  			//System.out.println(name + ": " + StringUtil.joinStringArray(meaning.getType()));
  		} else if(name.startsWith("thị xã ")) {
  			name = name.substring("thị xã ".length()) ;
  			meaning.setName(name) ;
  			//System.out.println(name + ": " + StringUtil.joinStringArray(meaning.getType()));
  		} else if(name.startsWith("trường ") &&  name.length() > 15) {
  			System.out.println(name + ": " + StringUtil.joinStringArray(meaning.getType()));
  			continue ;
  		}
  		String[] type = meaning.getType() ;
  		type = StringUtil.removeDuplicate(type) ;
  		meaning.setType(type) ;
  		writer.write(meaning); 
  	}
  	reader.close() ;
  	writer.close() ;
	}
}
