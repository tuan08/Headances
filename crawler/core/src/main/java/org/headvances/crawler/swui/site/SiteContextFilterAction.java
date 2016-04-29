package org.headvances.crawler.swui.site;

import java.awt.event.ActionEvent ;
import java.awt.event.ActionListener ;
import java.util.ArrayList ;
import java.util.List ;

import javax.swing.AbstractAction ;
import javax.swing.JCheckBoxMenuItem ;

import org.headvances.swingui.SwingUtil ;
import org.headvances.swingui.component.DropDownButton ;
import org.headvances.xhtml.site.SiteConfig;
import org.headvances.xhtml.site.SiteContextFilter;
import org.headvances.xhtml.site.SiteContextFilterSet;
import org.headvances.xhtml.site.SiteContext.Modify;

public class SiteContextFilterAction extends DropDownButton {
  private ArrayList<JCheckBoxMenuItem> groupRCItems ;
  private ArrayList<JCheckBoxMenuItem> groupModifyItems ;
  private ArrayList<JCheckBoxMenuItem> groupURLCountItems ;
  private ArrayList<JCheckBoxMenuItem> groupStatusItems ;
  private ArrayList<JCheckBoxMenuItem> groupRedirectCountItems ;
  private ArrayList<JCheckBoxMenuItem> groupFetchCountItems ;
  
  static final int[] responseCodeGroups = { 100, 200, 300, 400, 500, 10000 } ;
  static final int[] urlCountGroups     = { 1, 2, 10, 50, 100, 300, 500, 1000, 10000, 10000000 } ;
  
  static final String[] fetchCountGroups   = { "0", "1-5", "5-10", "10-25", ">25" } ;

  public SiteContextFilterAction() {
    super("Filter") ;

    groupRCItems = new ArrayList<JCheckBoxMenuItem>() ;
    groupModifyItems = new ArrayList<JCheckBoxMenuItem>() ;
    groupURLCountItems = new ArrayList<JCheckBoxMenuItem>() ;
    groupStatusItems = new ArrayList<JCheckBoxMenuItem>() ;
    groupRedirectCountItems = new ArrayList<JCheckBoxMenuItem>() ;
    groupFetchCountItems = new ArrayList<JCheckBoxMenuItem>() ;

    // Response Menu
    List<ActionListener> listeners = new ArrayList<ActionListener>() ;
    for (int i = 0; i < responseCodeGroups.length; i++) {
      final int group = responseCodeGroups[i] ;
      String path = "By Response > RC " + group ;

      int[] temp = { 1, 10, 50, 100, 500, 1000, 10000, 100000 } ;
      for (int j = 0; j < temp.length - 1; j++) {
        final int from = temp[j] ;
        final int to = temp[j + 1] ;
        String groupPath = from + " - " + to ;
        JCheckBoxMenuItem item = new JCheckBoxMenuItem(groupPath) ;
        registerMenuAction(path, item) ;
        groupRCItems.add(item) ;

        ActionListener listener = new ActionListener() {
          public void actionPerformed(ActionEvent e) {
            SiteContextPanel scp = SwingUtil.findAncestorOfType(SiteContextFilterAction.this, SiteContextPanel.class) ;
            final SiteContextFilterSet filter = scp.getSiteContextFilterSet() ;
            JCheckBoxMenuItem selectedItem = (JCheckBoxMenuItem) e.getSource() ;
            boolean selectedURLCount = selectedItem.isSelected() ;
            if (selectedURLCount) {
              for (JCheckBoxMenuItem item : groupRCItems) {
                if (!item.equals(selectedItem)) item.setSelected(false) ;
              }
              filter.addFilter(new SiteContextFilter.ResponseCodeFilter(group, from, to)) ;
            } else {
              filter.removeFilter(SiteContextFilter.ResponseCodeFilter.class) ;
            }
            scp.onUpdateSiteContextFilterSet(filter) ;
            scp.getStatusBar().setStatus("Filter By Response Code", "Done!") ;
          }
        } ;
        listeners.add(listener) ;
      }
    }
    for (int i = 0; i < groupRCItems.size(); i++) {
      groupRCItems.get(i).addActionListener(listeners.get(i)) ;
    }

    // URL Count
    listeners = new ArrayList<ActionListener>() ;
    String path = "By URL Count" ;
    for (int i = 0; i < urlCountGroups.length - 1; i++) {
      final int from = urlCountGroups[i] ;
      final int to = urlCountGroups[i + 1] ;
      String groupPath = from + " - " + to ;
      JCheckBoxMenuItem item = new JCheckBoxMenuItem(groupPath) ;
      registerMenuAction(path, item) ;
      groupURLCountItems.add(item) ;

      ActionListener listener = new ActionListener() {
        public void actionPerformed(ActionEvent e) {
          SiteContextPanel scp = SwingUtil.findAncestorOfType(SiteContextFilterAction.this, SiteContextPanel.class) ;
          final SiteContextFilterSet filter = scp.getSiteContextFilterSet() ;
          JCheckBoxMenuItem selectedItem = (JCheckBoxMenuItem) e.getSource() ;
          boolean selectedURLCount = selectedItem.isSelected() ;
          if (selectedURLCount) {
            for (JCheckBoxMenuItem item : groupURLCountItems) {
              if (!item.equals(selectedItem)) item.setSelected(false) ;
            }
            filter.addFilter(new SiteContextFilter.URLCountFilter(from, to)) ;
          } else {
            filter.removeFilter(SiteContextFilter.URLCountFilter.class) ;
          }
          scp.onUpdateSiteContextFilterSet(filter) ;
          scp.getStatusBar().setStatus("Filter By URL Count", "Done!") ;
        }
      } ;
      listeners.add(listener) ;
    }
    for (int i = 0; i < groupURLCountItems.size(); i++) {
      groupURLCountItems.get(i).addActionListener(listeners.get(i)) ;
    }

    //Redirect count
    listeners = new ArrayList<ActionListener>() ;
    path = "By URLRedirect Count" ;
    for (int i = 0; i < urlCountGroups.length - 1; i++) {
      final int from = urlCountGroups[i] ;
      final int to = urlCountGroups[i + 1] ;
      String groupPath = from + " - " + to ;
      JCheckBoxMenuItem item = new JCheckBoxMenuItem(groupPath) ;
      registerMenuAction(path, item) ;
      groupRedirectCountItems.add(item) ;

      ActionListener listener = new ActionListener() {
        public void actionPerformed(ActionEvent e) {
          SiteContextPanel scp = SwingUtil.findAncestorOfType(SiteContextFilterAction.this, SiteContextPanel.class) ;
          final SiteContextFilterSet filter = scp.getSiteContextFilterSet() ;
          JCheckBoxMenuItem selectedItem = (JCheckBoxMenuItem) e.getSource() ;
          boolean selectedURLCount = selectedItem.isSelected() ;
          if (selectedURLCount) {
            for (JCheckBoxMenuItem item : groupRedirectCountItems) {
              if (!item.equals(selectedItem)) item.setSelected(false) ;
            }
            filter.addFilter(new SiteContextFilter.URLRedirectCountFilter(from, to)) ;
          } else {
            filter.removeFilter(SiteContextFilter.URLRedirectCountFilter.class) ;
          }
          scp.onUpdateSiteContextFilterSet(filter) ;
          scp.getStatusBar().setStatus("Filter By URLRedirect Count", "Done!") ;
        }
      } ;
      listeners.add(listener) ;
    }
    for (int i = 0; i < groupRedirectCountItems.size(); i++) {
      groupRedirectCountItems.get(i).addActionListener(listeners.get(i)) ;
    }
    
    //Fetch count
    //TODO:
    listeners = new ArrayList<ActionListener>() ;
    for (int i = 0; i < fetchCountGroups.length; i++) {
      final String group = fetchCountGroups[i] ;
      path = "By Fetch Count > FC " + group ;

      int[] temp = { 1, 10, 50, 100, 500, 1000, 10000, 100000 } ;
      for (int j = 0; j < temp.length - 1; j++) {
        final int from = temp[j] ;
        final int to = temp[j + 1] ;
        String groupPath = from + " - " + to ;
        JCheckBoxMenuItem item = new JCheckBoxMenuItem(groupPath) ;
        registerMenuAction(path, item) ;
        groupFetchCountItems.add(item) ;

        ActionListener listener = new ActionListener() {
          public void actionPerformed(ActionEvent e) {
            SiteContextPanel scp = SwingUtil.findAncestorOfType(SiteContextFilterAction.this, SiteContextPanel.class) ;
            final SiteContextFilterSet filter = scp.getSiteContextFilterSet() ;
            JCheckBoxMenuItem selectedItem = (JCheckBoxMenuItem) e.getSource() ;
            boolean selectedURLCount = selectedItem.isSelected() ;
            if (selectedURLCount) {
              for (JCheckBoxMenuItem item : groupFetchCountItems) {
                if (!item.equals(selectedItem)) item.setSelected(false) ;
              }
              filter.addFilter(new SiteContextFilter.FetchCountFilter(group, from, to)) ;
            } else {
              filter.removeFilter(SiteContextFilter.FetchCountFilter.class) ;
            }
            scp.onUpdateSiteContextFilterSet(filter) ;
            scp.getStatusBar().setStatus("Filter By Fetch Count", "Done!") ;
          }
        } ;
        listeners.add(listener) ;
      }
    }
    for (int i = 0; i < groupFetchCountItems.size(); i++) {
      groupFetchCountItems.get(i).addActionListener(listeners.get(i)) ;
    }

    // By Modify
    listeners = new ArrayList<ActionListener>() ;
    for (final Modify sel : Modify.values()) {
      JCheckBoxMenuItem item = new JCheckBoxMenuItem(sel.toString()) ;
      registerMenuAction("By Modify", item) ;
      groupModifyItems.add(item) ;

      ActionListener listener = new ActionListener() {
        public void actionPerformed(ActionEvent e) {
          SiteContextPanel scp = SwingUtil.findAncestorOfType(SiteContextFilterAction.this, SiteContextPanel.class) ;
          final SiteContextFilterSet filter = scp.getSiteContextFilterSet() ;
          JCheckBoxMenuItem selectedItem = (JCheckBoxMenuItem) e.getSource() ;
          boolean selectedStatus = selectedItem.isSelected() ;
          if (selectedStatus) {
            for (JCheckBoxMenuItem item : groupModifyItems) {
              if (!item.equals(selectedItem)) item.setSelected(false) ;
            }
            filter.addFilter(new SiteContextFilter.ModifyFilter(sel)) ;
          } else {
            filter.removeFilter(SiteContextFilter.ModifyFilter.class) ;
          }
          scp.onUpdateSiteContextFilterSet(filter) ;
          scp.getStatusBar().setStatus("Filter By Modify", "Done!") ;
        }
      } ;
      listeners.add(listener) ;
    }
    for (int i = 0; i < groupModifyItems.size(); i++) {
      groupModifyItems.get(i).addActionListener(listeners.get(i)) ;
    }

    // By Status
    listeners = new ArrayList<ActionListener>() ;
    for (final String sel : SiteConfig.STATUS_ALL) {
      JCheckBoxMenuItem item = new JCheckBoxMenuItem(sel.toString()) ;
      registerMenuAction("By Status", item) ;
      groupStatusItems.add(item) ;

      ActionListener listener = new ActionListener() {
        public void actionPerformed(ActionEvent e) {
          SiteContextPanel scp = SwingUtil.findAncestorOfType(SiteContextFilterAction.this, SiteContextPanel.class) ;
          final SiteContextFilterSet filter = scp.getSiteContextFilterSet() ;
          JCheckBoxMenuItem selectedItem = (JCheckBoxMenuItem) e.getSource() ;
          boolean selectedStatus = selectedItem.isSelected() ;
          if (selectedStatus) {
            for (JCheckBoxMenuItem item : groupStatusItems) {
              if (!item.equals(selectedItem)) item.setSelected(false) ;
            }
            filter.addFilter(new SiteContextFilter.StatusFilter(sel.toString())) ;
          } else {
            filter.removeFilter(SiteContextFilter.StatusFilter.class) ;
          }
          scp.onUpdateSiteContextFilterSet(filter) ;
          scp.getStatusBar().setStatus("Filter By Status", "Done!") ;
        }
      } ;
      listeners.add(listener) ;
    }
    for (int i = 0; i < groupStatusItems.size(); i++) {
      groupStatusItems.get(i).addActionListener(listeners.get(i)) ;
    }

    add(new AbstractAction("To Commit") {
      public void actionPerformed(ActionEvent e) {
        SiteContextPanel scp = SwingUtil.findAncestorOfType(SiteContextFilterAction.this, SiteContextPanel.class) ;
        final SiteContextFilterSet filter = scp.getSiteContextFilterSet() ;
        scp.clearFilter() ;
        filter.addFilter(new SiteContextFilter.ToCommitFilter()) ;
        scp.onUpdateSiteContextFilterSet(filter) ;
        scp.getStatusBar().setStatus("Filter By Pre-commited SiteContexts", "Done!") ;
      }
    }) ;

    add(new AbstractAction("Clear") {
      public void actionPerformed(ActionEvent e) {
        SiteContextPanel scp = SwingUtil.findAncestorOfType(SiteContextFilterAction.this, SiteContextPanel.class) ;
        final SiteContextFilterSet filter = scp.getSiteContextFilterSet() ;
        filter.removeAllFilter() ;
        scp.clearFilter() ;
        scp.onUpdateSiteContextFilterSet(filter) ;
        scp.getStatusBar().setStatus("Clear Filter", "Done!") ;
      }
    }) ;
  }
  
  public void clearFilterSelection() {
    for(JCheckBoxMenuItem item : groupModifyItems) item.setSelected(false);
    for(JCheckBoxMenuItem item : groupRCItems) item.setSelected(false);
    for(JCheckBoxMenuItem item : groupStatusItems) item.setSelected(false);
    for(JCheckBoxMenuItem item : groupURLCountItems) item.setSelected(false);
    for(JCheckBoxMenuItem item : groupRedirectCountItems) item.setSelected(false);
    for(JCheckBoxMenuItem item : groupFetchCountItems) item.setSelected(false);
  }
}