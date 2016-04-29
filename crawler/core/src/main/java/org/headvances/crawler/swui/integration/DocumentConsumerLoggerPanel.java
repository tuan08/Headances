package org.headvances.crawler.swui.integration ;

import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.util.LinkedList;
import java.util.Map;

import javax.swing.JComponent;
import javax.swing.JScrollPane;
import javax.swing.JSplitPane;

import org.headvances.crawler.integration.DocumentConsumerLogger;
import org.headvances.crawler.integration.DocumentConsumerLogger.DocumentLog;
import org.headvances.swingui.component.JTablePagination;
import org.headvances.swingui.component.TableView;
import org.headvances.swingui.component.UpdatableJPanel;
import org.springframework.context.ApplicationContext;

public class DocumentConsumerLoggerPanel extends UpdatableJPanel {
  private TableView                            urlLogTable ;
  private DetailLogPanel                       detailLogPanel ;

  private Map<String, LinkedList<DocumentLog>> documentLogs ;
  
  public DocumentConsumerLoggerPanel() throws Exception {
    update() ;
  }

  protected JComponent createBodyPane() {
    urlLogTable = new TableView() ;
    urlLogTable.addMouseListener(new MouseAdapter() {
      public void mouseClicked(MouseEvent e) {
        int viewRow = urlLogTable.getSelectedRow() ;
        if (viewRow >= 0) {
          String hostname = urlLogTable.getValueAt(viewRow, 1).toString() ;
          LinkedList<DocumentLog> logs = documentLogs.get(hostname) ;
          detailLogPanel.update(logs) ;
        }
      }
    }) ;
    
    JScrollPane urlLogScrollPane = new JScrollPane(urlLogTable) ;
    detailLogPanel = new DetailLogPanel() ;
    JSplitPane splitPane = new JSplitPane(JSplitPane.VERTICAL_SPLIT, urlLogScrollPane, detailLogPanel) ;
    splitPane.setOneTouchExpandable(true) ;
    splitPane.setDividerLocation(150) ;
    return splitPane ;
  }

  protected void update() throws Exception {
    ApplicationContext ctx = DocumentConsumerLogger.getApplicationContext() ;
    DocumentConsumerLogger logger = ctx.getBean(DocumentConsumerLogger.class) ;
    documentLogs = logger.getLogs() ;
    populate() ;
  }

  public void populate() {
    if (documentLogs == null || documentLogs.size() == 0) return ;
    String[] hostnames = documentLogs.keySet().toArray(new String[documentLogs.size()-1]) ;
    String[] header = { "#", "Site", "Count" } ;
    Object[][] data = new Object[hostnames.length][] ;
    for (int i = 0; i < hostnames.length; i++) {
      String site = hostnames[i] ;
      data[i] = new Object[] { i, site, documentLogs.get(site).size() } ;
    }

    urlLogTable.setData(header, data) ;
    urlLogTable.getColumnModel().getColumn(0).setMaxWidth(50) ;
    detailLogPanel.update(documentLogs.get((hostnames[0])));
  }

  private class DetailLogPanel extends JTablePagination {
    public void update(LinkedList<DocumentLog> logs) {
      if (logs == null) return ;
      String[] header = { "#", "URL", "Content Length", "Response Code" } ;
      Object[][] data = new Object[logs.size()][] ;
      for (int i = 0; i < logs.size(); i++) {
        DocumentLog log = logs.get(i);
        data[i] = new Object[] { i, log.getUrl(), log.getContentLength(), log.getResponseCode() } ;
      }
      setData(header, data, 50) ;
      getJTable().getColumnModel().getColumn(0).setMaxWidth(50) ;
    }
  }
}
