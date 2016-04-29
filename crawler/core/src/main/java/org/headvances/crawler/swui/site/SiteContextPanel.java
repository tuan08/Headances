package org.headvances.crawler.swui.site;

import java.awt.BorderLayout ;
import java.awt.event.ActionEvent ;
import java.awt.event.ActionListener ;
import java.io.File ;
import java.util.ArrayList ;
import java.util.LinkedHashMap ;
import java.util.List ;
import java.util.Set ;
import java.util.TreeSet ;

import javax.swing.AbstractAction ;
import javax.swing.DefaultComboBoxModel ;
import javax.swing.JButton ;
import javax.swing.JComboBox ;
import javax.swing.JComponent ;
import javax.swing.JFileChooser ;
import javax.swing.JTabbedPane ;
import javax.swing.JToolBar ;

import org.headvances.cluster.ClusterClient;
import org.headvances.cluster.task.TaskResult;
import org.headvances.crawler.cluster.task.GetSiteContextTask ;
import org.headvances.crawler.cluster.task.ModifySiteContextTask ;
import org.headvances.crawler.swui.CrawlerContext ;
import org.headvances.crawler.swui.comp.ComponentNode ;
import org.headvances.crawler.swui.comp.ComponentPanel ;
import org.headvances.json.JSONReader ;
import org.headvances.json.JSONWriter ;
import org.headvances.swingui.SwingUtil ;
import org.headvances.swingui.component.DropDownButton ;
import org.headvances.swingui.component.JPopupMenuExt ;
import org.headvances.swingui.component.StatusBar ;
import org.headvances.util.statistic.StatisticsSet ;
import org.headvances.xhtml.site.SiteConfig;
import org.headvances.xhtml.site.SiteContext;
import org.headvances.xhtml.site.SiteContextFilter;
import org.headvances.xhtml.site.SiteContextFilterSet;
import org.headvances.xhtml.site.SiteContext.Modify;
import org.headvances.xhtml.site.SiteScheduleStat;
import org.headvances.xhtml.url.URLDatumStatisticMap;

/**
 * $Author: Tuan Nguyen$ 
 **/
public class SiteContextPanel extends ComponentPanel {
  private ComponentNode           compNode ;

  private SiteContextFilterSet    contextFilterSet ;

  private SiteContextFilterAction filterDropDown ;
  private SiteConfigTable         configTable ;

  private StatusBar               statusBar ;

  private InputFilterExpression   hostnameSearchCombobox ;

  private JTabbedPane             tabbedPane ;

  public SiteContextPanel(ComponentNode compNode) throws Exception {
    this.compNode = compNode ;
    statusBar = new StatusBar() {
      public void setStatus(String action, String status) {
        if(contextFilterSet != null) {
          String suffix = "context = " + contextFilterSet.getSiteContexts().size()  + 
            ", filter context = " + contextFilterSet.getFilteredSiteContexts().size() + 
            ", changed context = " + contextFilterSet.getTotalChangedSiteContexts() ;
          status = suffix + " | " + status;
        }
        super.setStatus(action, status) ;
      }
    };
    add(statusBar, BorderLayout.SOUTH) ;
    update();
  }

  protected JComponent createBodyPane() {
    tabbedPane = new JTabbedPane();
    configTable = new SiteConfigTable();
    return tabbedPane;
  }

  public StatusBar getStatusBar() { return this.statusBar  ; }
  
  public SiteContextFilterSet getSiteContextFilterSet() { return this.contextFilterSet ; }
  
  protected void initJToolBar(final JToolBar toolBar) {
    JButton commitBtn = new JButton("Commit");
    commitBtn.addActionListener(new ActionListener() {
      public void actionPerformed(ActionEvent event) {
        try {
          List<SiteContext> contexts = contextFilterSet.getSiteContexts();
          ModifySiteContextTask task = new ModifySiteContextTask();
          task.add(contexts);
          ClusterClient client = CrawlerContext.getInstance().getCrawlerClient();
          TaskResult.dump(client.execute(task));
          changeContexts(contextFilterSet);
          clearFilter();
          statusBar.setStatus("Commit", "Contexts changed succesful!") ;
        } catch (Exception ex) {
          SwingUtil.showError(SiteContextPanel.this, "Commit", ex);
        }
      }
    });
    toolBar.add(commitBtn);
    //Export Button
    DropDownButton exportBtn = new DropDownButton("Export");
    exportBtn.add(new AbstractAction("All") {
      public void actionPerformed(ActionEvent e) {
        try {
          JFileChooser fc = new JFileChooser();
          int returnVal = fc.showDialog(SiteContextPanel.this, "Export");
          if (returnVal == JFileChooser.APPROVE_OPTION) {
            File file = fc.getSelectedFile();
            List<SiteContext> contexts = contextFilterSet.getSiteContexts() ;
            JSONWriter writer = new JSONWriter(file.getCanonicalPath()) ;
            for(int i = 0; i < contexts.size(); i++) {
              SiteContext context = contexts.get(i) ;
              writer.write(context.getSiteConfig()) ;
            }
            writer.close() ;
            statusBar.setStatus("Export All", "New SiteContext file saved succesful!") ;
          } 
        } catch (Exception ex) {
          SwingUtil.showError(SiteContextPanel.this, "Export", ex);
        }
      }
    });

    exportBtn.add(new AbstractAction("By Filter") {
      public void actionPerformed(ActionEvent e) {
        try {
          JFileChooser fc = new JFileChooser();
          int returnVal = fc.showDialog(SiteContextPanel.this, "Export");
          if (returnVal == JFileChooser.APPROVE_OPTION) {
            File file = fc.getSelectedFile();
            List<SiteContext> contexts = contextFilterSet.getFilteredSiteContexts() ;
            JSONWriter writer = new JSONWriter(file.getCanonicalPath()) ;
            for(int i = 0; i < contexts.size(); i++) {
              SiteContext context = contexts.get(i) ;
              writer.write(context.getSiteConfig()) ;
            }
            writer.close() ;
            statusBar.setStatus("Export By Filter", "New SiteContext file saved succesful!") ;
          } 
        } catch (Exception ex) {
          SwingUtil.showError(SiteContextPanel.this, "Export", ex);
        }
      }
    });
    toolBar.add(exportBtn);
    //Import Button
    JButton importBtn = new JButton("Import");
    importBtn.addActionListener(new ActionListener() {
      public void actionPerformed(ActionEvent event) {
        try {
          JFileChooser fc = new JFileChooser();
          int returnVal = fc.showDialog(SiteContextPanel.this, "Import");
          if (returnVal != JFileChooser.APPROVE_OPTION) {
            return ;
          }
          File file = fc.getSelectedFile();
          JSONReader reader = new JSONReader(file.getCanonicalPath());
          List<SiteConfig> configs = reader.readAll(SiteConfig.class) ;

          LinkedHashMap<Object, SiteContext> contextLinkedHashMap = 
            contextFilterSet.getContextLinkedHashMap();
          int countDuplicate = 0, countNew = 0;
          for(int i = 0; i < configs.size(); i++) {
            String key = configs.get(i).getHostname() ;
            SiteContext context = new SiteContext(configs.get(i)) ;

            if(contextLinkedHashMap.containsKey(key)) {
              context.setModify(Modify.MODIFIED);
              contextLinkedHashMap.remove(key);
              contextLinkedHashMap.put(key, context);
              countDuplicate++;
            } else {
              context.setModify(Modify.ADD);
              contextLinkedHashMap.put(key, context) ;
              countNew++;
            }
          }
          List<SiteContext> contexts = contextFilterSet.getSiteContexts(contextLinkedHashMap) ;
          contextFilterSet = new SiteContextFilterSet(contexts) ;

          clearFilter();
          onUpdateSiteContextFilterSet(contextFilterSet) ;
          reader.close() ;
          statusBar.setStatus("Import", 
              "Update " + countDuplicate + " duplicated and " + countNew + " new SiteContext!") ;
        } catch (Exception ex) {
          SwingUtil.showError(SiteContextPanel.this, "Import", ex);
        }
      }
    });
    toolBar.add(importBtn);
    toolBar.addSeparator();
    toolBar.add(new SiteContextBulkActions()) ;
    filterDropDown = new SiteContextFilterAction();
    toolBar.add(filterDropDown) ;

    hostnameSearchCombobox = new InputFilterExpression();
    toolBar.add(hostnameSearchCombobox);
    JButton searchBtn = new JButton("Search");
    searchBtn.addActionListener(new ActionListener() {
      public void actionPerformed(ActionEvent event) {
        try {
          String exp = hostnameSearchCombobox.getFilterExpression();
          if(exp == null || exp.length() == 0) {
            contextFilterSet.removeFilter(SiteContextFilter.SiteNameFilter.class) ;
          } else {
            contextFilterSet.addFilter(new SiteContextFilter.SiteNameFilter(exp)) ;
          }
          onUpdateSiteContextFilterSet(contextFilterSet);
          statusBar.setStatus("Search", contextFilterSet.getFilteredSiteContexts().size() + " results");
        } catch (Exception ex) {
          SwingUtil.showError(SiteContextPanel.this, "Search", ex);
        }
      }
    });
    toolBar.add(searchBtn);
  }

  protected void update() throws Exception {
    GetSiteContextTask task = new GetSiteContextTask();
    ClusterClient client = CrawlerContext.getInstance().getCrawlerClient();
    List<SiteContext> contexts  = client.execute(task, compNode.getMember());
    
    contextFilterSet = new SiteContextFilterSet(contexts);
    onUpdateSiteContextFilterSet(contextFilterSet);
    clearFilter() ;
    statusBar.setStatus("Update", "Refresh contexts") ;
  }

  public void onUpdateSiteContextFilterSet(SiteContextFilterSet filter) {
    int tabIdx = ((tabbedPane.getSelectedIndex() < 0)||(filter.getFilteredSiteContexts().size() == 0)) ? 0 : tabbedPane.getSelectedIndex() ;

    tabbedPane.removeAll() ;
    contextFilterSet = filter ;	
    List<SiteContext> contexts = filter.getFilteredSiteContexts();
    if(contexts.size() == 0) return ;
    
    configTable.populate(contexts);
    tabbedPane.add("Config", configTable);
    boolean masterInfo = false ;
    for(int i = 0; i < contexts.size(); i++) {
    	if(contexts.get(i).hasAttribute(SiteScheduleStat.class)) {
    		masterInfo = true ;
    		break ;
    	}
    }
    if(masterInfo) {
    	SiteProcessTable  processTable = new SiteProcessTable() ;
    	processTable.populate(contexts);
    	tabbedPane.add("Process", processTable);

    	populateURLDatumStatisticMap(contexts);
    }
    tabbedPane.setSelectedIndex(tabIdx);
  }

  public void populateURLDatumStatisticMap(List<SiteContext> contexts) {
    if(contexts == null || contexts.size() == 0) return ;
    StatisticsSet[] statisticMap = new StatisticsSet[contexts.size()];
    for(int i = 0; i < contexts.size(); i++) {
      SiteContext context = contexts.get(i) ;
      URLDatumStatisticMap urlDatumStatisMap = context.getAttribute(URLDatumStatisticMap.class);
      statisticMap[i] = urlDatumStatisMap.getStatisticMap();
      statisticMap[i].setName(context.getSiteConfig().getHostname());
    }

    for(String selCategory : statisticMap[0].getCategories()) {
      StatisticTab tab = new StatisticTab(statisticMap, selCategory) ;
      tabbedPane.add(tab.getName(), tab);
    }
  }

  static public class StatisticTab extends AbstractSiteTable {
    public StatisticTab(StatisticsSet[] map, String category) {
      final JPopupMenuExt popup = new SiteContextPopupMenu(this);
      getJTable().addMouseListener(popup.createPopupTriggerListener());

      setName(category) ;
      String[] header = map[0].getCategoryKeys(category);
      Object[][] data = new Object[map.length][] ;

      for(int i = 0; i < map.length; i++) {
        List<Object> holder = new ArrayList<Object>();
        holder.add(map[i].getName()) ;
        try {
          for(Object sel : map[i].getCategoryData(category, header)) holder.add(sel) ;
        } catch(NullPointerException ex) {
          System.err.println("SiteContext: " + map[i].getName() + ", category: " + category) ;
          map[i].report(System.err) ;
          throw ex ;
        }
        data[i] = holder.toArray() ;
      }

      List<Object> holder = new ArrayList<Object>();
      holder.add("Site");
      for(String sel : map[0].getCategoryKeys(category)) {
        holder.add(sel);
      }
      header = holder.toArray(new String[holder.size()]);
      setData(header, data, 50) ;
    }

    public void populate(List<SiteContext> contexts) {
    }
  }

  public void updateRow(int selectRow, SiteContext context) { 
    configTable.updateRow(selectRow, context) ;	
  }

  public void addSiteContext(SiteContext context) {
    String key = context.getSiteConfig().getHostname();
    LinkedHashMap<Object, SiteContext> contextLinkedHashMap = contextFilterSet.getContextLinkedHashMap();
    if(contextLinkedHashMap.containsKey(key)) {
      contextLinkedHashMap.remove(key);
      context.setModify(Modify.MODIFIED);
      contextLinkedHashMap.put(key, context);
    } else {
      context.setModify(Modify.ADD);
      contextLinkedHashMap.put(key, context) ;
    }
    List<SiteContext> contexts = contextFilterSet.getSiteContexts(contextLinkedHashMap) ;
    onUpdateSiteContextFilterSet(new SiteContextFilterSet(contexts));
    statusBar.setStatus("Add New SiteContext", "Done!") ;
  }

  private void changeContexts(SiteContextFilterSet filter) {
    List<SiteContext> contexts = filter.getSiteContexts();
    for(int i = 0; i < contexts.size(); i++) {
      SiteContext context = contexts.get(i);
      if(context.getModify() == Modify.DELETE) contexts.remove(context);
      else if(context.getModify() != Modify.NONE) context.setModify(Modify.NONE);
    }
    onUpdateSiteContextFilterSet(new SiteContextFilterSet(contexts));
  }

  public void clearFilter() {
    contextFilterSet.removeAllFilter();
    filterDropDown.clearFilterSelection();
    hostnameSearchCombobox.clearText();
  }

  static public class InputFilterExpression extends JComboBox {
    private Set<String> histories = new TreeSet<String>() ;

    public InputFilterExpression(){
      setBounds(250, 240, 250, 20);
      setEditable(true);
    }

    public void clearText() {
      setSelectedItem("");
    }

    public String getFilterExpression() {
      String exp = (String) getSelectedItem() ;
      if(exp != null&&exp.length() != 0&&!histories.contains(exp)) {
        histories.add(exp);
        removeAllItems();
        setModel(new DefaultComboBoxModel(histories.toArray()));
      }
      setSelectedItem(exp);
      return exp ;
    }
  }
}