package org.headvances.swingui ;

import java.awt.Dimension;
import java.awt.Toolkit;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyEvent;
import java.util.ArrayList;
import java.util.List;

import javax.swing.JDesktopPane;
import javax.swing.JFrame;
import javax.swing.JInternalFrame;
import javax.swing.JMenu;
import javax.swing.JMenuBar;
import javax.swing.JMenuItem;
import javax.swing.KeyStroke;

import org.headvances.swingui.component.JMenuBarExt;

public class SwingApplication extends JFrame implements ActionListener {
	private List<ApplicationPlugin> plugins = new ArrayList<ApplicationPlugin>() ;
	private JDesktopPane desktop;
	private JMenuBarExt menuBar ;
	
	public SwingApplication() {
		super("Swing Application");
		//Make the big window be indented 25 pixels from each edge of the screen.
		int inset = 25 ;
		Dimension screenSize = Toolkit.getDefaultToolkit().getScreenSize();
		setBounds(inset, inset, screenSize.width  - inset*2, screenSize.height - inset*2);

		desktop = new JDesktopPane(); //a specialized layered pane
		setJMenuBar(initMenuBar());
		setContentPane(desktop);
		//Make dragging a little faster but perhaps uglier.
		desktop.setDragMode(JDesktopPane.OUTLINE_DRAG_MODE);
	}

	public JMenuBarExt getJMenuBar() { return this.menuBar ; }

	public void addPlugin(ApplicationPlugin plugin) {
		plugins.add(plugin) ;
	}
	
	public <T> T getPlugin(Class<T> clazz) {
		for(ApplicationPlugin sel : plugins) {
			if(sel.getClass() == clazz) return (T)sel ;
		}
		return null ;
	}
	
	protected JMenuBar initMenuBar() {
		this.menuBar = new JMenuBarExt();
		//Set up the lone menu.
		JMenu menu = new JMenu("File");
		menu.setMnemonic(KeyEvent.VK_D);
		menuBar.add(menu);

		JMenuItem menuItem = new JMenuItem("Quit");
		menuItem.setMnemonic(KeyEvent.VK_Q);
		menuItem.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_Q, ActionEvent.ALT_MASK));
		menuItem.setActionCommand("quit");
		menuItem.addActionListener(this);
		menu.add(menuItem);
		return menuBar;
	}

	public void onInit() {
		for(int i = 0; i < plugins.size(); i++) {
			ApplicationPlugin plugin = plugins.get(i) ;
			plugin.onInit(this) ;
		}
	}
	
	public void onDestroy() {
		for(int i = 0; i < plugins.size(); i++) {
			ApplicationPlugin plugin = plugins.get(i) ;
			plugin.onDestroy(this) ;
		}
	}
	
	//React to menu selections.
	public void actionPerformed(ActionEvent e) {
		quit();
	}

	public void addFrame(JInternalFrame frame) {
		frame.setVisible(true); //necessary as of 1.3
		desktop.add(frame);
		try {
			frame.setSelected(true);
		} catch (java.beans.PropertyVetoException e) {}
	}

	//Quit the application.
	protected void quit() { System.exit(0); }
	
	public static void main(String[] args) {
		javax.swing.SwingUtilities.invokeLater(new Runnable() {
			public void run() {
				//Make sure we have nice window decorations.
				JFrame.setDefaultLookAndFeelDecorated(true);
				//Create and set up the window.
				SwingApplication application = new SwingApplication();
				application.onInit() ;
				application.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
				//Display the window.
				application.setVisible(true);
			}
		});
	}
}