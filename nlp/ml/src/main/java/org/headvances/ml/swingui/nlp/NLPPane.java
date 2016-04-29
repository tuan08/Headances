package org.headvances.ml.swingui.nlp;

import java.awt.BorderLayout;
import java.awt.Dimension;
import java.util.HashMap;
import java.util.Map;

import javax.swing.JPanel;
import javax.swing.JSplitPane;

import org.headvances.ml.swingui.nlp.config.AppConfig;
import org.headvances.ml.swingui.nlp.config.PluginConfig;
import org.headvances.swingui.component.Lifecycle;

public class NLPPane extends JPanel implements Lifecycle {
	private Map<String, NLPPlugin> plugins ;
	
	private WorkingWorkspace wspace ; 
	private ControlWorkspace cspace ;
	
	public NLPPane() {
		setLayout(new BorderLayout())   ;
		wspace = new WorkingWorkspace() ;
		cspace = new ControlWorkspace() ;
		JSplitPane splitPane = new JSplitPane(JSplitPane.HORIZONTAL_SPLIT, cspace, wspace);
		splitPane.setOneTouchExpandable(true);
		splitPane.setDividerLocation(300);
		splitPane.setPreferredSize(new Dimension(1000, 650));
		add(splitPane, BorderLayout.CENTER) ;
	}
	
	public void onInit() {	
		try {
	    AppConfig config = AppConfig.load("classpath:nlp-config.json");
	    plugins = new HashMap<String, NLPPlugin>() ;
	    for(PluginConfig sel : config.getPlugins()) {
	    	NLPPlugin plugin = (NLPPlugin)Class.forName(sel.getType()).newInstance() ;
	    	plugin.init(sel) ;
	    	plugin.onInit(this) ;
	    	plugins.put(sel.getName(), plugin) ;
	    }
	    NLPPlugin[] allPlugins = plugins.values().toArray(new  NLPPlugin[plugins.size()]) ;
	    for(NLPPlugin sel :  allPlugins) {
	    	for(NLPPlugin other :  allPlugins) {
		    	if(sel != other) sel.connect(other) ;
		    }
	    }
		} catch (Exception e) {
	    e.printStackTrace();
    }
	}

  public void onDestroy() { 
  	wspace.onDestroy() ; 
  }
  
	public WorkingWorkspace getWorkingWorkspace() { return this.wspace ; }
	
	public ControlWorkspace getControlWorkspace() { return this.cspace ; }
}