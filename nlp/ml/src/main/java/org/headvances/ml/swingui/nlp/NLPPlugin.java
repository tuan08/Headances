package org.headvances.ml.swingui.nlp;

import org.headvances.ml.swingui.nlp.config.PluginConfig;

public interface NLPPlugin {
	
	public void init(PluginConfig plugin) ;
	public PluginConfig getPluginConfig() ;
	
	public void onInit(NLPPane app) ;
	public void onDestroy(NLPPane app) ;

	public void connect(NLPPlugin aplugin) ;
	public void disconnect(NLPPlugin aplugin) ;

}
