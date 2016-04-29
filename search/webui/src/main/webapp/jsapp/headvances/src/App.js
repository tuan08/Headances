Ext.ns('headvances', 'headvances.views');

headvances.cfg = {};

headvances.App = Ext.extend(Ext.TabPanel, {
  fullscreen: true,
  tabBar: { dock: 'bottom', layout: { pack: 'center' } },
  cardSwitchAnimation: false,
  
  initComponent: function() {
    if (navigator.onLine) {
      this.items = [
        { xtype: 'SearchForm', iconCls: 'search', title: 'Search'},
        { title: 'Monitor', xtype: 'MonitorMain', iconCls: 'bookmarks' },
        { title: 'Analyse', xtype: 'AnalyseForm', iconCls: 'settings' },
        { title: 'About', xtype: 'aboutlist', iconCls: 'info', pages: this.aboutPages }
      ];
    } else {
      this.on('render', function() { this.el.mask('No internet connection.'); }, this);
    }
    
    headvances.cfg = {};
    headvances.cfg.shortUrl = this.shortUrl;
    headvances.cfg.title = this.title;
    
    headvances.App.superclass.initComponent.call(this);
  }
});

Ext.setup({
  statusBarStyle: 'black',
  onReady: function() {
    headvances.App = new headvances.App({
      title: 'Headvances'
    });
  }
});
