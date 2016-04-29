Ext.regModel('WebMonitor', {
  fields: ['entity', 'description']
});

headvances.data.MonitorData = {
  "monitor" : [
    {
      "entity":    "Iphone 3GS", 
      "description": "monitor keyword"
    },
    {
      "entity":    "iphone 4", 
      "description": "monitor keyword",
    }
  ]
};


headvances.data.WebMonitorStore = new Ext.data.Store({
  model: 'WebMonitor',
  data:  headvances.data.MonitorData.monitor
}) ;

headvances.views.MonitorMain = Ext.extend(Ext.Panel, {
  layout: 'card',

  initComponent: function() {
    this.list = new Ext.List({
      itemTpl: [
        '<h6>{entity}</h6>',
        '<p>{description}</p>',
      ],
      loadingText: false,
      styleHtmlContent: true,
      store: headvances.data.WebMonitorStore
    });
    
    this.list.on('selectionchange', this.onSelect, this);

    var onRender = function() {
    };
    
    this.list.on('render', onRender, this);
    
    this.listpanel = new Ext.Panel({
      layout: 'fit',
      dockedItems: [{ 
        xtype: 'toolbar', title: 'Monitor Web Data' ,
        items: [
          {
            iconMask: true,
            iconCls: 'add',
            scope: this,
            handler: function() { }
          },
          {
            iconMask: true,
            iconCls: 'refresh',
            scope: this,
            handler: function() {
              /*
              var entry = [ {
                "id":    "refresh", 
                "description": "monitor keyword"
              } ];
              headvances.data.WebMonitorStore.loadData(entry, true);
              this.list.bindStore(headvances.data.WebMonitorStore);
              */
              this.refresh() ;
            }
          }
        ]
      }],
      items: this.list,
      listeners: {
        activate: { 
          fn: function() {
            this.list.getSelectionModel().deselectAll();
            Ext.repaint();
          }, 
          scope: this 
        }
      }
    })
    
    this.items = this.listpanel;
    
    this.on('activate', this.onActivate, this);
    
    headvances.views.MonitorMain.superclass.initComponent.call(this);
    this.refresh() ;
  },
  
  refresh: function(){
    var currPanel = this ;
    currPanel.setLoading(true, true);
    Ext.Ajax.request({
      //url: 'json/WebMonitor.json',
      url: '/rest/webmonitor/list',
      timeout: 5000,
      success: function(response, opts) {
        var jsonObj = Ext.util.JSON.decode(response.responseText);
        currPanel.setLoading(false);
        headvances.data.WebMonitorStore.loadData(jsonObj.monitor, false);
        currPanel.setLoading(false);
      },
      failure: function ( result, request) { 
        Ext.Msg.alert('Search Failed', result.responseText); 
        panel.setLoading(false);
      } 
    });

  },

  onActivate: function() {
  },
  
  onSelect: function(selectionmodel, records){
    if(records[0] == undefined) return ;
    var nextCard = new headvances.views.WebMonitorSummary({ prevCard: this.listpanel, monitorData: records[0].data });
    this.setActiveItem(nextCard, 'slide');
  }
});

Ext.reg('MonitorMain', headvances.views.MonitorMain);
