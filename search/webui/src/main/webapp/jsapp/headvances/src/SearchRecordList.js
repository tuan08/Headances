headvances.views.SearchRecordList = Ext.extend(Ext.Panel, {
  layout: 'card',

  initComponent: function() {
    //this.waitMask = new Ext.LoadMask(Ext.getBody(), {msg: "Please wait..."});
    var store = null ;
    if(this.searchData == null) {
      this.searchData = headvances.data.mock.SearchQueryResponse ;
    } 
    var store = new Ext.data.Store({
      model: 'SearchRecordList',
      data: this.searchData.recordDescription
    }) ;

    var currPanel = this ;
    this.list = new Ext.List({
      scroll: 'vertical',
      itemTpl: [
        '<h6>{index}. {title}</h6>',
        '<p>{bestMatch}</p>'
      ],
      loadingText: false,
      styleHtmlContent: true,
      store: store,
      listeners: {
        scroller: {
          scroll: function(scroller, offset) {
            if(currPanel.loading) return;
            if(offset.y  < Math.abs(this.offsetBoundary.top) + 20) return;
            var lresp = currPanel.searchData ;
            if(lresp.currentPage >= lresp.availablePage) return;
            currPanel.loading = true;
            currPanel.setLoading(true, true);
            var query = Ext.apply({}, currPanel.searchData.query);
            query.page = query.page + 1 ;
            Ext.Ajax.request({
              url: '../../rest/query',
              method: 'GET',
              params: query,
              timeout: 15000,
              success: function(response, opts) {
                var jsonObj = Ext.util.JSON.decode(response.responseText);
                currPanel.searchData = jsonObj ;
                currPanel.list.store.loadData(jsonObj.recordDescription, true);
                currPanel.setLoading(false);
                currPanel.loading = false;
              },
              failure: function ( result, request) { 
                Ext.Msg.alert('Search Failed', result.responseText); 
                currPanel.setLoading(false);
                currPanel.loading = false;
              } 
            });
          }
        }
      }
    });
    
    this.list.on('selectionchange', this.onSelect, this);

    this.listpanel = new Ext.Panel({
      layout: 'fit',
      dockedItems: [{ 
        xtype: 'toolbar', title: 'Found ' + this.searchData.indexInfo.totalHit ,
        items: [ headvances.views.Util.newBackButton(this, this.prevCard) ]
      }],
      items:  [
        this.list
        //,{ xtype: 'button', text: 'More', handler: function() {} , scope: this}
      ],
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
    
    headvances.views.SearchRecordList.superclass.initComponent.call(this);
  },
  
  onActivate: function(){
  },
  
  initializeData: function(data) {
    this.waitMask.hide();
  },
  
  onSelect: function(selectionmodel, records){
    if (records[0] == undefined) return ;
    var currPanel = this ;
    currPanel.setLoading(true, true);
    Ext.Ajax.request({
      url: '../../rest/cache/' + records[0].data.id,
      timeout: 5000,
      success: function(response, opts) {
        var jsonObj = Ext.util.JSON.decode(response.responseText);
        currPanel.setLoading(false);
        var nextCard = new headvances.views.SearchRecordCache({ prevCard: this.listpanel, cacheData: jsonObj });
        currPanel.setActiveItem(nextCard, 'slide');
      },
      failure: function ( result, request) { 
        Ext.Msg.alert('Search Failed', result.responseText); 
        panel.setLoading(false);
      } 
    });
  }
});

Ext.reg('SearchRecordList', headvances.views.SearchRecordList);
