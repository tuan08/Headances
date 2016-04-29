package org.headvances.data;

import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

import org.headvances.util.text.StringUtil;
/**
 * $Author: Tuan Nguyen$ 
 **/
public class Entity extends HashMap<String, String> {
	final static public String SEPARATOR = "\n---\n" ;
	
	public Entity() { super(5) ; }

	public void add(String name, String value) {
	  if(value == null) remove(name);
	  else put(name, value) ;
	}
	
	public String getString(String name) {
		String str = get(name) ;
		if(str == null) { return null ; }
		return str;
	}
	
	public void add(String name, String[] value) {
		if(value == null) {
			remove(name) ;
		} else {
			String store = StringUtil.joinStringArray(value, SEPARATOR) ;
			put(name, store);
		}
	}
	
	public String[] getStringArray(String name){
	  String str = get(name);
	  if(str == null){ return null; }
	  return StringUtil.toStringArray(str, SEPARATOR);
	}
	
	public void add(String name, int value) {
		put(name, Integer.toString(value)) ;
	}
	
	public int getInteger(String name){
	  String str = get(name);
	  if(str == null) { return 0; }
	  return Integer.parseInt(str);
	}
	
	public void add(String name, float value){
	  put(name, Float.toString(value));
	}
	
	public float getFloat(String name){
	  String str = get(name);
	  if(str == null) { return 0; }
	  return Float.parseFloat(str);
	}
	
	public void add(String name, double value){
	  put(name, Double.toString(value));
	}
	
	public double getDouble(String name){
	  String str = get(name);
	  if(str == null) { return 0; }
	  return Double.parseDouble(str);
	}
	
	public void add(String name, long value){
	  put(name, Long.toString(value));
	}
	
	public long getLong(String name){
	  String str = get(name);
	  if(str == null) { return 0; }
	  return Long.parseLong(str);
	}
	
	public void add(String name, short value){
	  put(name, Short.toString(value));
	}
	
	public short getShort(String name){
	  String str = get(name);
	  if(str == null) { return 0; }
	  return Short.parseShort(str);
	}
	
	public void add(String name, boolean value){
	  put(name, Boolean.toString(value));
	}
	
	public boolean getBoolean(String name){
	  String str = get(name);
	  if(str == null) { return false; }
	  return Boolean.parseBoolean(str);
	}
	
	public String toString() {
		StringBuilder b = new StringBuilder() ;
		Iterator<Map.Entry<String, String>> i = entrySet().iterator() ; 
		while(i.hasNext()) {
			Map.Entry<String, String> entry = i.next() ;
			b.append(entry.getKey()).append(": ").append(entry.getValue()) ;
			b.append("\n") ;
		}
		return b.toString() ;
	}
}