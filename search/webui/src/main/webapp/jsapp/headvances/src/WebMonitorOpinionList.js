Ext.regModel('WebMonitorOpinionList', {
  fields: ['id', 'opinion']
});

var WebMonitorOpinionListSampleData = [
  { id: 'id1', opinion: 'Iphone 3GS co toc do nhanh hon phien ban cu'},
  { id: 'id2', opinion: 'Phien ban Iphone 3GS mong hon' },
  { id: 'id3', opinion: 'Iphone 3gs su dung cac cong nghe moi nhat'}
];

headvances.views.WebMonitorOpinionList = Ext.extend(Ext.Panel, {
  layout: 'card',
  cls: 'WebMonitorOpinionList',

  initComponent: function() {
    var currPanel = this ;
    this.dockedItems = [{
      xtype: 'toolbar',
      items: [ 
        headvances.views.Util.newBackButton(this, this.prevCard) ,
        {
          text: 'Refresh', scope: this, handler: function() { }
        },
        {
          text: 'Filter', 
          scope: this, 
          handler: function() { 
            var nextCard = new headvances.views.WebMonitorOpinionFilter({ prevCard: currPanel});
            this.ownerCt.setActiveItem(nextCard, 'slide');
          }
        }
      ]
    }];
     
    
    var opinionStore = new Ext.data.Store({
      model: 'WebMonitorOpinionList',
      data:  this.opinions.opinion
    }) ;

    this.list = new Ext.List({
      scroll: 'vertical',
      style: 'width: 100%',
      styleHtmlContent: true,
      itemTpl: [ '<h6>{opinion}</h6>'],
      store: opinionStore,
      listeners: {
        selectionchange: {fn: this.onSelect, scope: this},
        scroller: {
          scroll: function(scroller, offset) {
            if(currPanel.loading) return;
            if(offset.y  < Math.abs(this.offsetBoundary.top) + 20) return;
            var filter = currPanel.filter;
            if(filter.currentPage >= filter.availablePage) return;
            filter.page = filter.page + 1 ;
            currPanel.update(filter, true) ;
          }
        }
      }
    });

    this.items = this.list;

    Ext.repaint();
    
    this.listeners = {
      activate: { 
        fn: function(){ this.list.getSelectionModel().deselectAll(); }, 
        scope: this 
      }
    };
    
    headvances.views.WebMonitorOpinionList.superclass.initComponent.call(this);
  },
  
  update: function(filter, appendData) { 
    currPanel = this ;
    currPanel.loading = true;
    currPanel.setLoading(true, true);
    Ext.Ajax.request({
      url: '../../rest/webmonitor/opinions',
      method:  'GET',
      params: filter,
      timeout: 5000,
      success: function(response, opts) {
        var jsonObj = Ext.util.JSON.decode(response.responseText);
        currPanel.opinions = jsonObj ;
        currPanel.list.store.loadData(jsonObj.opinion, appendData);
        currPanel.setLoading(false);
        currPanel.loading = false;
        currPanel.filter = filter;
      },
      failure: function ( result, request) { 
        Ext.Msg.alert('List Opinion Failed', result.responseText); 
        currPanel.setLoading(false);
        currPanel.loading = false;
      } 
    });
  },

  onSelect: function(selectionmodel, records){ 
    if (records[0] == undefined) return ;
  }
});

Ext.reg('WebMonitorOpinionList', headvances.views.WebMonitorOpinionList);
