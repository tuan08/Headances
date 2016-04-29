package org.headvances.swingui;

import java.awt.Component;
import java.awt.Desktop;
import java.net.URI;
/**
 * $Author: Tuan Nguyen$ 
 **/
public class DesktopUtil {
	static public void openBrowser(Component comp, String url) {
		if (Desktop.isDesktopSupported()) {
			Desktop desktop = Desktop.getDesktop();
			if(desktop.isSupported(Desktop.Action.BROWSE)) {
				try {
					desktop.browse(new URI(url)) ;
				} catch (Throwable t) {
					SwingUtil.showError(comp, "Open url: " + url, t);
				}
			}
		}
	}
}
