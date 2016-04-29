package org.headvances.search.swui.search;

import java.awt.BorderLayout;
import java.awt.Dimension;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.JButton;
import javax.swing.JComponent;
import javax.swing.JDialog;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JTextArea;
import javax.swing.JTextField;
import javax.swing.SpringLayout;

import org.headvances.swingui.SpringUtilities;
import org.headvances.swingui.SwingUtil;
/**
 * $Author: Tuan Nguyen$ 
 **/
public class QueryFormPanel extends JDialog {
	private InputForm formPanel ;
	private SearchPanel searchPanel ;
	
	public QueryFormPanel(SearchPanel searchPanel) {
		super(SwingUtil.findAncestorOfType(searchPanel, JFrame.class), "Query Form", true);
		this.searchPanel = searchPanel ;
		
		setLayout(new BorderLayout());
		formPanel = new InputForm() ;
		add(formPanel, BorderLayout.CENTER) ;
		
		JPanel buttonPane = new JPanel();
		JButton okBtn = new JButton("OK") ;
		okBtn.addActionListener(new ActionListener() {
      public void actionPerformed(ActionEvent e) {
      	try {
	        formPanel.search() ;
      	} catch (Exception e1) {
      		SwingUtil.showError(QueryFormPanel.this, "Query Form", e1) ;
      	}
      	QueryFormPanel.this.setVisible(false) ;
      }
		});
		buttonPane.add(okBtn);
		
		JButton cancelBtn = new JButton("Cancel") ;
		cancelBtn.addActionListener(new ActionListener() {
      public void actionPerformed(ActionEvent e) {
      	QueryFormPanel.this.dispose() ;
      }
		});
		buttonPane.add(cancelBtn);
		
		add(buttonPane, BorderLayout.SOUTH) ;
		setPreferredSize(new Dimension(600, 400)) ;
		pack();
		setLocationRelativeTo(searchPanel);
	}
	
	public class InputForm extends JPanel {
		private JTextField indexFld, typeFld, limitFld ;
		private JTextArea  queryFld ;
		
		public InputForm() {
			SpringLayout layout = new SpringLayout() ;
			setLayout(layout);
			
			String[] labels = {"Index: ", "Type: ", "Limit: ", "Query: "};
			
			indexFld           = new JTextField("web") ;
			typeFld            = new JTextField("webpage") ;
			limitFld           = new JTextField("100") ;
			String QUERY = 
			  "{\n" +
			  "  \"bool\" : {\n" +
			  "      \"must\" : {\n" +
			  "         \"text\" : {\n" +
			  "             \"entities.icontent.title\" : \"hà nội\" \n" +
			  "           }\n" +
			  "      },\n" +
			  "      \"must\" : {\n" +
			  "         \"term\" : {\n" +
			  "             \"tags\" : \"webpage:detail\" \n" +
			  "           }\n" +
			  "      }\n" +
			  "  }\n" +
			  "}" ;			
			queryFld           = new JTextArea(QUERY) ;
			JComponent[] input = { indexFld, typeFld, limitFld, queryFld } ;
			
			int numPairs = labels.length;
			for (int i = 0; i < numPairs; i++) {
				JLabel label = new JLabel(labels[i], JLabel.TRAILING);
				add(label);
				if(input[i] instanceof JTextField) input[i].setMaximumSize(new Dimension(500, 30)) ;
				label.setLabelFor(input[i]);
				add(input[i]);
			}
			SpringUtilities.makeCompactGrid(this, numPairs, 2, /*rows, cols*/ 6, 6,/*initX, initY*/ 6, 6/*xPad, yPad*/);
		}
		
		public void search() throws Exception {
			String index     = indexFld.getText() ;
			String type = typeFld.getText() ;
			String jsonQuery = queryFld.getText() ;
			searchPanel.updateSearch(index, type, 100, jsonQuery) ;
		}
	}
}