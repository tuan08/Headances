package org.headvances.crawler.swui.site;

import java.awt.BorderLayout ;
import java.awt.Dimension ;
import java.awt.event.ActionEvent ;
import java.awt.event.ActionListener ;

import javax.swing.JButton ;
import javax.swing.JComboBox ;
import javax.swing.JComponent ;
import javax.swing.JDialog ;
import javax.swing.JFrame ;
import javax.swing.JLabel ;
import javax.swing.JPanel ;
import javax.swing.JTextArea ;
import javax.swing.JTextField ;
import javax.swing.SpringLayout ;

import org.headvances.swingui.SpringUtilities ;
import org.headvances.util.text.DateUtil ;
import org.headvances.xhtml.site.SiteConfig;
import org.headvances.xhtml.site.SiteContext;
import org.headvances.xhtml.site.SiteContext.Modify;

/**
 * $Author: Tuan Nguyen$
 **/
public class SiteContextEditor extends JDialog {
  private InputForm formPanel;
  private SiteContext selectContext;
  private SiteContextPanel siteContextPanel;
  private int selectRow;

  public SiteContextEditor(JFrame parent, SiteContextPanel siteCtxPanel, int sRow,  SiteContext selContext) {
    super(parent, "SiteContext Editor", true);
    this.siteContextPanel = siteCtxPanel;
    this.selectContext = selContext;
    this.selectRow = sRow;

    setLayout(new BorderLayout());
    setMinimumSize(new Dimension(600, 380)) ;
    formPanel = new InputForm();
    add(formPanel, BorderLayout.CENTER);
    
    formPanel.setMaximumSize(new Dimension(600, 400)) ;
    
    if(selectRow >= 0) {
      SiteConfig config = selectContext.getSiteConfig() ;
      formPanel.nameTextField.setText(config.getHostname());
      formPanel.injectUrlTextField.setText(config.getInjectUrl()[0]);
      formPanel.maxConnTextField.setSelectedItem(config.getMaxConnection());
      formPanel.deepCrawlTextField.setSelectedItem(config.getCrawlDeep());
      formPanel.refreshTimeTextField.setSelectedItem(config.getRefreshPeriod() / DateUtil.SECONDS_PER_DAY);
      if(config.getTags() != null) {
        formPanel.tagTextField.setSelectedItem(config.getTags()[0]);
      }
      
      formPanel.descriptionTextArea.setText(config.getDescription());
    } 

    JPanel buttonPane = new JPanel();
    JButton okBtn = new JButton("OK");
    okBtn.addActionListener(new ActionListener() {
      public void actionPerformed(ActionEvent e) {
        formPanel.update(selectContext);
        if(selectRow >= 0) {
          siteContextPanel.updateRow(selectRow, selectContext);
          siteContextPanel.getStatusBar().setStatus("Edit", "Edit " + selectContext.getSiteConfig().getHostname() + " Done!") ;
        } else { 
          selectContext.setModify(Modify.ADD);
          siteContextPanel.addSiteContext(selectContext);
          siteContextPanel.getStatusBar().setStatus("Add", "Add " + selectContext.getSiteConfig().getHostname() + " Done!") ;
        }
        SiteContextEditor.this.dispose();
      }
    });
    buttonPane.add(okBtn);

    JButton cancelBtn = new JButton("Cancel");
    cancelBtn.addActionListener(new ActionListener() {
      public void actionPerformed(ActionEvent e) {
        SiteContextEditor.this.dispose();
      }
    });
    buttonPane.add(cancelBtn);
    add(buttonPane, BorderLayout.SOUTH);
    
    pack();
    setLocationRelativeTo(parent);
    setVisible(true);
  }

  private class InputForm extends JPanel {
    private JTextField nameTextField, injectUrlTextField;
    private JComboBox maxConnTextField, deepCrawlTextField, refreshTimeTextField, tagTextField;
    private JTextArea descriptionTextArea;

    public InputForm() {
      SpringLayout layout = new SpringLayout();
      setLayout(layout);

      String[] labels      = { "Name: ", "Inject URL: ", "Max Connection: ", "Deep Crawler: " , 
                               "Refresh Time Period: ", "Tag: ", "Description: "};
      String[] maxConnect  = { "1", "2", "3", "4", "5" };
      String[] crawledDeep = { "1", "2", "3", "4", "5", "6", "7", "8", "9", "10" };
      String[] refreshTime = { "1", "3", "5" };
      String[] tags        = { "site:article", "site:classified", "site:forum", "site:job", "site:product " };

      nameTextField = new JTextField();
      injectUrlTextField = new JTextField();

      maxConnTextField = new JComboBox(maxConnect);
      maxConnTextField.setEditable(true);

      deepCrawlTextField  = new JComboBox(crawledDeep);
      deepCrawlTextField.setEditable(true);

      refreshTimeTextField = new JComboBox(refreshTime);
      refreshTimeTextField.setEditable(true);
      
      tagTextField = new JComboBox(tags);
      tagTextField.setEditable(true);

      descriptionTextArea = new JTextArea(10,20);
      descriptionTextArea.setEditable(true);

      JComponent[] input = { nameTextField, injectUrlTextField, maxConnTextField, deepCrawlTextField, 
                             refreshTimeTextField, tagTextField, descriptionTextArea };

      int numPairs = labels.length;
      for (int i = 0; i < numPairs; i++) {
        JLabel label = new JLabel(labels[i], JLabel.TRAILING);
        add(label);
        if (input[i] instanceof JTextField)
          input[i].setMaximumSize(new Dimension(400, 30));
        else if (input[i] instanceof JComboBox) {
          input[i].setBounds(250, 240, 250, 30);
        } 
        label.setLabelFor(input[i]);
        add(input[i]);
      }
      SpringUtilities.makeCompactGrid(this, numPairs, 2, /*rows, cols */6, 6,/* initX,initY*/6, 6/* xPad, yPad */);
    }

    private void update(SiteContext context) {
      context.setModify(Modify.MODIFIED) ;
      String hostName = nameTextField.getText();
      String injectUrl = injectUrlTextField.getText();
      int maxCon = Integer.parseInt(maxConnTextField.getSelectedItem().toString());
      int deepCrw = Integer.parseInt(deepCrawlTextField.getSelectedItem().toString());
      int refreshPeriod = (Integer.parseInt(refreshTimeTextField.getSelectedItem().toString())) * DateUtil.SECONDS_PER_DAY;
      String tag = tagTextField.getSelectedItem().toString() ;
      String description = descriptionTextArea.getText();

      context.getSiteConfig().setHostname(hostName);
      context.getSiteConfig().setInjectUrl(new String[]{injectUrl});
      context.getSiteConfig().setMaxConnection(maxCon);
      context.getSiteConfig().setCrawlDeep(deepCrw);
      context.getSiteConfig().setRefreshPeriod(refreshPeriod);
      //REVIEW
      context.getSiteConfig().addTag(tag) ;
      context.getSiteConfig().setDescription(description);
    }
  }
}