package org.headvances.swingui;
/**
 * $Author: Tuan Nguyen$ 
 **/
public interface ApplicationPlugin {
	public void onInit(SwingApplication app) ;
	public void onDestroy(SwingApplication app) ;
}
