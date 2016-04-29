package org.headvances.analysis.swui;

import java.awt.BorderLayout;
import java.util.List;

import javax.swing.JLabel;
import javax.swing.JPanel;

import org.headvances.monitor.InvokeMonitor;
import org.headvances.swingui.component.TableView;

/**
 * $Author: Tuan Nguyen$ 
 **/
public class InvokeMonitorInfoPanel extends JPanel {
	private JLabel title ;
	private TableView infoTable ;

	public InvokeMonitorInfoPanel() {
		setLayout(new BorderLayout()) ;
		this.title = new JLabel() ;
		add(title, BorderLayout.NORTH) ;
		this.infoTable = new TableView() ;
		add(infoTable, BorderLayout.CENTER) ;
	}
	
	public void populate(InvokeMonitor info) {
		if(info == null) return ;
		title.setText(info.getDescription()) ;
		String[] header = { "Name", "Value" } ;
		List<String> messages = info.getMessages() ;
		Object[][] data = new Object[messages.size() + 2][] ;
		data[0] = new Object[] { "Call", info.getCount() } ;
		data[1] = new Object[] { "Error", info.getErrorCount() } ;
		
		for(int i = 0; i < messages.size(); i++) {
			data[i + 2] = new Object[] { "Message", messages.get(i) } ;
		}
		infoTable.setData(header, data) ;
	}
}