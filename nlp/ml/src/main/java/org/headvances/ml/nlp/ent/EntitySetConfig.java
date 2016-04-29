package org.headvances.ml.nlp.ent;

import java.util.HashMap;
import java.util.Map;
/**
 * $Author: Tuan Nguyen$ 
 **/
public class EntitySetConfig {
	static Map<String, EntitySetConfig> CONFIGS = new HashMap<String, EntitySetConfig>() ;
	
	static {
		CONFIGS.put(
				"np", 
				new EntitySetConfig("np", new String[]{ "org:", "loc:", "per:", "evt:" }));
		CONFIGS.put(
				"dimension", 
				new EntitySetConfig("dimension", new String[]{ "qt:dist:", "qt:sprc:", "qt:vol:"}));
		CONFIGS.put(
				"time", 
				new EntitySetConfig("time", new String[]{ "qt:time:", "time:"}));
		CONFIGS.put(
				"weight", 
				new EntitySetConfig("weight", new String[]{ "qt:weight:"}));
		CONFIGS.put(
				"speed", 
				new EntitySetConfig("speed", new String[]{ "qt:speed:"}));
		CONFIGS.put(
				"other", 
				new EntitySetConfig("other", new String[]{ "qt:bin:", "phone:", "percent:"}));
		
		CONFIGS.put(
				"num", 
				new EntitySetConfig("num", new String[]{ "time:", "phone:", "percent:", "qt:" }));
		CONFIGS.put(
				"all", 
				new EntitySetConfig("all", new String[]{ "org:", "loc:", "per:", "evt:", "time:", "phone:", "percent:", "qt:" }));
		
		CONFIGS.put("qtag", new EntitySetConfig("qtag", new String[]{ "qtag:"}));
	}
	
	final public String set ;
	final public String[] tagPrefix ;
	
	public EntitySetConfig(String set, String[] tagPrefix) {
		this.set = set ;
		this.tagPrefix = tagPrefix ;
	}
	
	static public EntitySetConfig getConfig(String name) {
		return CONFIGS.get(name) ;
	}
}