package org.headvances.swingui;

import java.awt.Component;
import java.awt.Container;
import java.util.ArrayList;
import java.util.List;

import javax.swing.JOptionPane;

import org.headvances.util.ExceptionUtil;

public class SwingUtil {
	static public <T extends Container> T findAncestorOfType(Container acomp, Class<T> type) {
		Container parent = acomp ;
		while(parent != null) {
			if(type.isInstance(parent)) return type.cast(parent) ;
			parent = parent.getParent() ;
		}
		return null ;
	}
	
	static public <T> T findDescentdantOfType(Container acomp, Class<T> type) {
		if(type.isInstance(acomp)) return type.cast(acomp) ;
		for(int i = 0; i < acomp.getComponentCount(); i++) {
			Component child = acomp.getComponent(i) ;
			if(type.isInstance(child)) return type.cast(child) ;
			if(child instanceof Container) {
				T found = findDescentdantOfType((Container) child, type) ;
				if(found != null) return found ;
			}
		}
 		return null ;
	}
	
	static public <T> List<T> findDescentdantsOfType(Container acomp, Class<T> type) {
		List<T> holder = new ArrayList<T>() ;
		findDescentdantsOfType(holder, acomp, type) ;
		return holder ;
	}
	
	static public <T> void findDescentdantsOfType(List<T> holder, Container acomp, Class<T> type) {
		if(type.isInstance(acomp)) holder.add((T)acomp) ;
		for(int i = 0; i < acomp.getComponentCount(); i++) {
			Component child = acomp.getComponent(i) ;
			if(child instanceof Container) {
				findDescentdantsOfType(holder, (Container)child, type) ;
			}
		}
	}
	
	static public void showError(Component comp, String title, Throwable error) {
		String msg = error.getMessage() ;
    msg += "\n" + ExceptionUtil.getStackTrace(error, null) ;
    JOptionPane.showMessageDialog(comp, msg, "OK", JOptionPane.ERROR_MESSAGE) ;
	}
}
