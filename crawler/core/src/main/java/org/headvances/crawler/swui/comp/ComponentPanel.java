package org.headvances.crawler.swui.comp;

import java.awt.BorderLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.JButton;
import javax.swing.JComponent;
import javax.swing.JPanel;
import javax.swing.JToolBar;

import org.headvances.swingui.SwingUtil;

/**
 * $Author: Tuan Nguyen$
 **/
abstract public class ComponentPanel extends JPanel {
  private JToolBar toolBar;

  public ComponentPanel() {
    setLayout(new BorderLayout());
    toolBar = new JToolBar();
    toolBar.setFloatable(false);
    JButton updateBtn = new JButton("Update");
    updateBtn.addActionListener(new ActionListener() {
      public void actionPerformed(ActionEvent event) {
        try {
          update();
        } catch (Exception ex) {
          SwingUtil.showError(ComponentPanel.this, "Update", ex);
        }
      }
    });
    toolBar.add(updateBtn);
    initJToolBar(toolBar) ;
    
    add(toolBar, BorderLayout.NORTH);
    add(createBodyPane(), BorderLayout.CENTER);
  }

  abstract protected JComponent createBodyPane();

  protected void initJToolBar(JToolBar toolBar) {
  }
  
  protected void update() throws Exception {
  }
}