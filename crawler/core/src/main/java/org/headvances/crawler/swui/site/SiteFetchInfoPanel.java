package org.headvances.crawler.swui.site;

import java.awt.event.ActionEvent ;
import java.awt.event.KeyAdapter ;
import java.awt.event.KeyEvent ;
import java.awt.event.MouseAdapter ;
import java.awt.event.MouseEvent ;

import javax.swing.AbstractAction ;
import javax.swing.JComponent ;
import javax.swing.JScrollPane ;
import javax.swing.JSplitPane ;
import javax.swing.JTabbedPane ;
import javax.swing.JTextArea ;

import org.headvances.crawler.swui.comp.ComponentPanel ;
import org.headvances.swingui.DesktopUtil ;
import org.headvances.swingui.component.JPopupMenuExt ;
import org.headvances.swingui.component.JTablePagination ;
import org.headvances.swingui.component.TableView ;
import org.headvances.util.ExceptionUtil ;
import org.headvances.xhtml.site.SiteConfig;
import org.headvances.xhtml.site.SiteContext;
import org.headvances.xhtml.site.SiteContext.Modify;

public class SiteFetchInfoPanel extends ComponentPanel {
  private SiteInfoFetcherTable infoFetcherTable ;
  private DetailPanel          detailPanel ;
  private SiteInfoFetcher[]    siteInfoFetchers ;

  public SiteFetchInfoPanel(int[] selectedRow, SiteContext[] selectedContext) throws Exception {
    fetch(selectedRow, selectedContext);
    update();
  }

  protected void update() throws Exception {
    infoFetcherTable.populate() ;
  }

  protected void fetch(int[] selectedRow, SiteContext[] selectedContext) throws InterruptedException {
    siteInfoFetchers = new SiteInfoFetcher[selectedContext.length];
    for(int i = 0; i < selectedContext.length; i++) {
      SiteContext context = selectedContext[i] ;
      int selRow = selectedRow[i];
      SiteInfoFetcher info = new SiteInfoFetcher(selRow, context) ;
      info.doFetch() ;
      siteInfoFetchers[i] = info ;
    }
  }

  protected JComponent createBodyPane() {
    infoFetcherTable = new SiteInfoFetcherTable();

    infoFetcherTable.addMouseListener(new MouseAdapter(){     
      public void mouseClicked(MouseEvent e) {
        int viewRow = infoFetcherTable.getSelectedRow();
        if (viewRow >= 0) {
          SiteInfoFetcher info = siteInfoFetchers[viewRow];
          detailPanel.update(info);
        }
      }
    });
    
    infoFetcherTable.addKeyListener(new KeyAdapter() {
      public void keyPressed(KeyEvent e) {
        if(e.isControlDown() && e.getKeyCode() == KeyEvent.VK_O) {
          int viewRow = infoFetcherTable.getSelectedRow() ;
          SiteInfoFetcher info = siteInfoFetchers[viewRow] ;
          String url = info.getUrl() ;
          DesktopUtil.openBrowser(SiteFetchInfoPanel.this, url) ;
        }
      }
    }) ;
    
    JScrollPane listURLPane = new JScrollPane(infoFetcherTable);
    detailPanel = new DetailPanel() ;
    JSplitPane splitPane = new JSplitPane(JSplitPane.VERTICAL_SPLIT, listURLPane, detailPanel);
    splitPane.setOneTouchExpandable(true);
    splitPane.setDividerLocation(150);
    return splitPane ;
  }

  //REVIEW
  private class SiteInfoFetcherTable extends TableView {
    public SiteInfoFetcherTable() {
      //final JPopupMenuExt popup = new SiteInfoFetcherPopupMenu(this);
      //addMouseListener(popup.createPopupTriggerListener());
    }

    public void populate() {
      if(siteInfoFetchers == null || siteInfoFetchers.length == 0) return ;
      String[] header = { "#", "Site", "Status" } ;
      Object[][] data   = new Object[siteInfoFetchers.length][] ;
      for(int i = 0; i < siteInfoFetchers.length; i++) {
        SiteContext context = siteInfoFetchers[i].getSiteContext();
        SiteConfig config = context.getSiteConfig() ;
        String statusFetch = siteInfoFetchers[i].getStatus();
        data[i] = new Object[] {
            i,
            config.getHostname(),
            statusFetch
        };
      }
      infoFetcherTable.setData(header, data);
      infoFetcherTable.getColumnModel().getColumn(0).setMaxWidth(50);
      detailPanel.update(siteInfoFetchers[0]);
    }
  }

  private class DetailPanel extends JTabbedPane {
    private JTextArea headerTxtArea, xhtmlTxtArea, errorTxtArea ;
    private JTablePagination extractedUrlTable ;
    
    public DetailPanel() {
      setTabLayoutPolicy(JTabbedPane.SCROLL_TAB_LAYOUT);
      headerTxtArea = new JTextArea();
      xhtmlTxtArea = new JTextArea();
      extractedUrlTable = new JTablePagination() ;
      errorTxtArea = new JTextArea();
      
      extractedUrlTable.getJTable().addKeyListener(new KeyAdapter() {
        public void keyPressed(KeyEvent e) {
          if(e.isControlDown() && e.getKeyCode() == KeyEvent.VK_O) {
          	int row = extractedUrlTable.getJTable().getSelectedRow() ;
            String url = extractedUrlTable.getJTable().getValueAt(row, 1).toString();
            DesktopUtil.openBrowser(SiteFetchInfoPanel.this, url) ;
          }
        }
      }) ;
      
      add("Header", new JScrollPane(headerTxtArea));
      add("XHTML", new JScrollPane(xhtmlTxtArea));
      add("Extracted URL", extractedUrlTable);
      add("Error", new JScrollPane(errorTxtArea));
    }
    
    public void update(SiteInfoFetcher info) {
      String header = "", xhtml = "", error = "" ;
      String[] extractedUrl = new String[100] ;
      
      header = info.getHeader() ;
      xhtml = info.getXhtml() ;
      extractedUrl = info.getExtractedUrl();
      error = (info.getError() != null) ? ExceptionUtil.getStackTrace(info.getError(), null) : "No Error"  ;
      
      headerTxtArea.setText(header);
      xhtmlTxtArea.setText(xhtml);
      errorTxtArea.setText(error);
      
      if(extractedUrl ==  null) return ;
      
      String[] headers = { "#", "URL" } ;
      Object[][] data = new Object[extractedUrl.length][] ;
      for(int i = 0; i < extractedUrl.length; i++) {
        data[i] = new Object[] { 
            i, 
            extractedUrl[i] 
        };
      }
      extractedUrlTable.setData(headers, data, 50) ;
      extractedUrlTable.getJTable().getColumnModel().getColumn(0).setMaxWidth(50) ;
    }
  }
}
