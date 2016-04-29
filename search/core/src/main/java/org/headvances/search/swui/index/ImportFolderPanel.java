package org.headvances.search.swui.index;

import java.awt.BorderLayout;
import java.awt.Dimension;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.io.File;
import java.util.ArrayList;
import java.util.List;

import javax.swing.JButton;
import javax.swing.JComponent;
import javax.swing.JDialog;
import javax.swing.JFileChooser;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JTextField;
import javax.swing.SpringLayout;

import org.headvances.search.ESJSONClient;
import org.headvances.search.swui.ESClientListener;
import org.headvances.search.swui.ElasticSearchContext;
import org.headvances.swingui.SpringUtilities;
import org.headvances.swingui.SwingUtil;
import org.headvances.util.FileUtil;
import org.headvances.util.IOUtil;
/**
 * $Author: Tuan Nguyen$ 
 **/
public class ImportFolderPanel extends JDialog {
	private InputForm formPanel ;
	
	public ImportFolderPanel(JFrame parent) {
		super(parent, "Import Folder", true);
		
		setLayout(new BorderLayout());
		formPanel = new InputForm() ;
		add(formPanel, BorderLayout.CENTER) ;
		
		JPanel buttonPane = new JPanel();
		JButton okBtn = new JButton("OK") ;
		okBtn.addActionListener(new ActionListener() {
      public void actionPerformed(ActionEvent e) {
      	try {
	        formPanel.doImport() ;
      	} catch (Exception e1) {
      		SwingUtil.showError(ImportFolderPanel.this, "Import", e1) ;
      	}
      	ImportFolderPanel.this.dispose() ;
      }
		});
		buttonPane.add(okBtn);
		
		JButton cancelBtn = new JButton("Cancel") ;
		cancelBtn.addActionListener(new ActionListener() {
      public void actionPerformed(ActionEvent e) {
      	ImportFolderPanel.this.dispose() ;
      }
		});
		buttonPane.add(cancelBtn);
		
		add(buttonPane, BorderLayout.SOUTH) ;
		setPreferredSize(new Dimension(400, 150)) ;
		pack();
		setLocationRelativeTo(parent);
		setVisible(true);
	}
	
	public class InputForm extends JPanel {
		private JTextField indexFld, typeFld, folderPathFld ;
		
		public InputForm() {
			SpringLayout layout = new SpringLayout() ;
			setLayout(layout);
			
			String[] labels = {"Index: ", "Type: ", "Folder: "};
			
			indexFld      = new JTextField("webpage") ;
			typeFld  = new JTextField("webpage") ;
			folderPathFld   = new JTextField("") ;
			folderPathFld.addMouseListener(new MouseAdapter() {
					public void mousePressed(MouseEvent e) {
						if(e.getClickCount() > 1) {
							JFileChooser fc = new JFileChooser();
							fc.setCurrentDirectory(new File("."));
							fc.setFileSelectionMode(JFileChooser.DIRECTORIES_ONLY);
		      		int returnVal = fc.showOpenDialog(ImportFolderPanel.this);
		      		if (returnVal == JFileChooser.APPROVE_OPTION) {
		      			File file = fc.getSelectedFile();
		      			folderPathFld.setText(file.getAbsolutePath()) ;
		      		}
						}
					}
				}
			);
			JComponent[] input = {indexFld, typeFld, folderPathFld} ;
			
			int numPairs = labels.length;
			for (int i = 0; i < numPairs; i++) {
				JLabel label = new JLabel(labels[i], JLabel.TRAILING);
				add(label);
				if(input[i] instanceof JTextField) input[i].setMaximumSize(new Dimension(200, 30)) ;
				label.setLabelFor(input[i]);
				add(input[i]);
			}
			SpringUtilities.makeCompactGrid(this, numPairs, 2, /*rows, cols*/ 6, 6,/*initX, initY*/ 6, 6/*xPad, yPad*/);
		}
		
		public void doImport() throws Exception {
			new Thread() {
				public void run() {
					try {
						String index       = indexFld.getText() ;
						String type        = typeFld.getText() ;
						String folderPath  = folderPathFld.getText() ; 
						String[] file = FileUtil.findFiles(folderPath, ".*\\.json") ;
						String settingFile = null ;
						String mappingFile = null ;
						List<String> datFiles = new ArrayList<String>() ;
						for(String selFile : file) {
							if(selFile.endsWith("setting.json")) settingFile = selFile ;
							else if(selFile.endsWith("mapping.json")) mappingFile = selFile ;
							else datFiles.add(selFile) ;
						}

						ESJSONClient esclient = 
							new ESJSONClient(ElasticSearchContext.getInstance().getESClient(), index, type) ;
						if(!esclient.hasIndex(index)) {
							String setting = null, mapping = null ;
							if(settingFile != null) setting  = IOUtil.getFileContentAsString(settingFile, "UTF8") ;
							else setting = IOUtil.getResourceAsString("webpage.setting.json", "UTF8") ;
							
							if(mappingFile != null) mapping  = IOUtil.getFileContentAsString(mappingFile, "UTF8") ;
							else mapping = IOUtil.getResourceAsString("webpage.mapping.json", "UTF8") ;
							esclient.createIndex(setting, mapping) ;
						}
						for(String selFile : datFiles) {
							esclient.doImport(selFile) ;
						}
						ElasticSearchContext.getInstance().broadcast(ESClientListener.UPDATE) ;
					} catch(Exception ex) {
						ex.printStackTrace() ;
					}
				}
			}.start() ;
		}
	}
}