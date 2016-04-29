package org.headvances.ml.swingui.nlp.config;

import java.io.InputStream;
import java.io.StringReader;
import java.util.ArrayList;
import java.util.List;

import org.codehaus.jackson.JsonParser;
import org.codehaus.jackson.map.MappingJsonFactory;
import org.codehaus.jackson.map.SerializationConfig;
import org.headvances.util.IOUtil;

public class AppConfig {
	private List<PluginConfig> plugins ;

	public List<PluginConfig> getPlugins() { return this.plugins ; }
	public void setPlugins(List<PluginConfig> plugins) { this.plugins = plugins ; }

	public void add(PluginConfig plugin) {
		if(plugins == null) plugins = new ArrayList<PluginConfig>() ;
		for(int i = 0; i < plugins.size(); i++) {
			PluginConfig sel = plugins.get(i) ;
			if(sel.getName().equals(plugin.getName())) {
				plugins.set(i, plugin) ;
				return ;
			}
		}
		plugins.add(plugin) ;
	}
	
	static public AppConfig load(String res) throws Exception {
		InputStream is = IOUtil.loadRes(res) ;
		String JSON_OBJECT = IOUtil.getStreamContentAsString(is, "UTF-8") ;
		MappingJsonFactory factory = new MappingJsonFactory();
		factory.getCodec().configure(SerializationConfig.Feature.WRAP_ROOT_VALUE, true) ;
		factory.configure(JsonParser.Feature.AUTO_CLOSE_SOURCE, false); // all configuration before use
		JsonParser jp = factory.createJsonParser(new StringReader(JSON_OBJECT));
		AppConfig config = jp.readValueAs(AppConfig.class) ;
		return config ;
	}
}
