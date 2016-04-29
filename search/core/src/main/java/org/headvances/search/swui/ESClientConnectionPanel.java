package org.headvances.search.swui;

import java.awt.BorderLayout;
import java.awt.Dimension;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JComponent;
import javax.swing.JDialog;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JTextField;
import javax.swing.SpringLayout;

import org.headvances.swingui.SpringUtilities;
import org.headvances.swingui.SwingUtil;
/**
 * $Author: Tuan Nguyen$ 
 **/
public class ESClientConnectionPanel extends JDialog {
	private InputForm formPanel ;
	
	public ESClientConnectionPanel(JFrame parent) {
		super(parent, "ESClient Connection", true);
		
		setLayout(new BorderLayout());
		setMinimumSize(new Dimension(400, 170));
		formPanel = new InputForm() ;
		add(formPanel, BorderLayout.CENTER) ;
		
		JPanel buttonPane = new JPanel();
		JButton okBtn = new JButton("OK") ;
		okBtn.addActionListener(new ActionListener() {
      public void actionPerformed(ActionEvent e) {
      	try {
	        formPanel.connect() ;
      	} catch (Exception e1) {
      		SwingUtil.showError(ESClientConnectionPanel.this, "Crawler Client Connection", e1) ;
      	}
      	ESClientConnectionPanel.this.dispose() ;
      }
		});
		buttonPane.add(okBtn);
		
		JButton cancelBtn = new JButton("Cancel") ;
		cancelBtn.addActionListener(new ActionListener() {
      public void actionPerformed(ActionEvent e) {
      	ESClientConnectionPanel.this.dispose() ;
      }
		});
		buttonPane.add(cancelBtn);
		
		add(buttonPane, BorderLayout.SOUTH) ;
		
		pack();
		setLocationRelativeTo(parent);
		setVisible(true);
	}
	
	public class InputForm extends JPanel {
		private JTextField nameFld, passwordFld;
		private JComboBox addressComboList;
		
		public InputForm() {
			SpringLayout layout = new SpringLayout() ;
			setLayout(layout);
			
			String[] labels = {"Group Name: ", "Password: ", "Address: "};
			String[] address = {"localhost:9300", "192.168.6.21:9300"};
			
			nameFld      = new JTextField("search") ;
			passwordFld  = new JTextField("search") ;
			addressComboList = new JComboBox(address);
			addressComboList.setEditable(true);
			
			JComponent[] input = {nameFld, passwordFld, addressComboList} ;
			
			int numPairs = labels.length;
			for (int i = 0; i < numPairs; i++) {
				JLabel label = new JLabel(labels[i], JLabel.TRAILING);
				add(label);
				if(input[i] instanceof JTextField) input[i].setMaximumSize(new Dimension(200, 30)) ;
				else if(input[i] instanceof JComboBox) input[i].setBounds(250, 240, 250, 20);
				label.setLabelFor(input[i]);
				add(input[i]);
			}
			SpringUtilities.makeCompactGrid(this, numPairs, 2, /*rows, cols*/ 6, 6,/*initX, initY*/ 6, 6/*xPad, yPad*/);
		}
		
		public void connect() throws Exception {
			String name     = nameFld.getText() ;
			String password = passwordFld.getText() ;
			
			String address  = addressComboList.getSelectedItem().toString();
			ElasticSearchContext.getInstance().connect(new String[] { address }) ;
		}
	}
}