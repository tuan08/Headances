package org.headvances.analysis.swui;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.File;

import javax.swing.BoxLayout;
import javax.swing.JButton;
import javax.swing.JComponent;
import javax.swing.JFileChooser;
import javax.swing.JPanel;
import javax.swing.JToolBar;

import org.headvances.analysis.cluster.task.GetAnalysisServiceMonitorTask;
import org.headvances.cluster.ClusterClient;
import org.headvances.monitor.InvokeMonitor;
import org.headvances.swingui.SwingUtil;
/**
 * $Author: Tuan Nguyen$ 
 **/
public class AnalysisServiceInfoPanel extends ComponentPanel {
	private ComponentNode compNode ;
	private InvokeMonitorInfoPanel monitorInfoPanel ;

	public AnalysisServiceInfoPanel(ComponentNode cnode) throws Exception {
		super() ;
		this.compNode = cnode ;
		update() ;
	}
	
	protected void initJToolBar(JToolBar toolBar) {
		JButton inputBtn = new JButton("Input") ;
		inputBtn.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent event) {
				try {
					JFileChooser fc = new JFileChooser();
					fc.setCurrentDirectory(new File(".")) ;
					fc.setFileSelectionMode(JFileChooser.FILES_AND_DIRECTORIES);
					int returnVal = fc.showOpenDialog(AnalysisServiceInfoPanel.this);
					if (returnVal == JFileChooser.APPROVE_OPTION) {
						final File file = fc.getSelectedFile();
						AnalysisContext context = AnalysisContext.getInstance() ;
						context.inputDocument(compNode.getMember(), file.getCanonicalPath()) ;
					}
				} catch (Throwable ex) {
					SwingUtil.showError(AnalysisServiceInfoPanel.this, "Input Files", ex) ;
				}
			}
		});
		toolBar.add(inputBtn) ;
	}
	
	protected JComponent createBodyPane() {
		JPanel pane = new JPanel() ;
		pane.setLayout(new BoxLayout(pane, BoxLayout.Y_AXIS));

		monitorInfoPanel = new InvokeMonitorInfoPanel() ;
		pane.add(monitorInfoPanel) ;
		return pane ;
	}
	
	protected void update() throws Exception {
		GetAnalysisServiceMonitorTask task = new GetAnalysisServiceMonitorTask();
		ClusterClient client = AnalysisContext.getInstance().getClient();
		InvokeMonitor monitor = client.execute(task, compNode.getMember());
		monitorInfoPanel.populate(monitor) ;
	}
}