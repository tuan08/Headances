headvances.views.AboutList = Ext.extend(Ext.Panel, {
  layout: 'card',

  initComponent: function(){
    var aboutPages = [
      {
        title: 'About',
        card: { xtype: 'htmlpage', url: 'html/about.html' }
      }, {
        title: 'Overviews',
        card: { xtype: 'htmlpage', url: 'html/overviews.html' }
      }, {
        title: 'Help',
        card: { xtype: 'htmlpage', url: 'html/help.html' }
      }, {
        title: 'Credits',
        card: { xtype: 'htmlpage', url: 'html/credits.html' }
      }
    ] ;

    this.list = new Ext.List({
      title: 'About',
      itemTpl: '<div class="page">{title}</div>',
      ui: 'round',
      store: new Ext.data.Store({ fields: ['name', 'card'], data: aboutPages }),
      listeners: {
        selectionchange: {fn: this.onSelect, scope: this}
      },
    });
    
    this.listpanel = new Ext.Panel({
      title: 'About',
      layout: 'fit',
      dockedItems: { xtype: 'toolbar', title: 'About' },
      items: this.list
    })
    
    this.listpanel.on('activate', function(){
      this.list.getSelectionModel().deselectAll();
    }, this);
    
    this.items = [this.listpanel];
    
    headvances.views.AboutList.superclass.initComponent.call(this);
  },
  
  onSelect: function(sel, records){
    if (records[0] !== undefined) {
      var newCard = Ext.apply({}, records[0].data.card, { 
        prevCard: this.listpanel,
        title: records[0].data.title
      });
      
      this.setActiveItem(Ext.create(newCard), 'slide');
    }
  }
});

Ext.reg('aboutlist', headvances.views.AboutList);
