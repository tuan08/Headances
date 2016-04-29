headvances.views.DummyDetail = Ext.extend(Ext.Panel, {
  scroll: 'vertical',
  layout: { type: 'vbox', align: 'stretch' },
  cls: 'DummyDetail',

  initComponent: function(){
    this.dockedItems = [{
      xtype: 'toolbar',
      items: [
        headvances.views.Util.newBackButton(this, this.prevCard)
      ]
    }];
    
    this.items = [{
      tpl: new Ext.XTemplate('<h6>{name}</h6>'),
      data: this.record.data,
      styleHtmlContent: true
    }];
    
    this.dummyList = new Ext.List({
      itemTpl: [
        '<h6>{name}</h6>',
        '<div>{hello}</div>'
      ],
      store: headvances.data.DummyStore,
      listeners: {
        selectionchange: {fn: this.onDummySelect, scope: this}
      },
      styleHtmlContent: true,
      scroll: false,
      autoHeight: true,
      style: 'width: 100%;'
    });
    
    this.items.push({ xtype: 'toolbar', title: 'Speaker(s)', ui: 'gray', cls: 'small_title' })
    this.items.push(this.dummyList);
    Ext.repaint();
    
    this.listeners = {
      activate: { 
        fn: function(){
          if (this.dummyList) {
            this.dummyList.getSelectionModel().deselectAll();
          }
        }, 
        scope: this 
      }
    };
    
    headvances.views.DummyDetail.superclass.initComponent.call(this);
  },
  
  onDummySelect: function(selectionmodel, records){
    if (records[0] !== undefined) {
      var dummyCard = new headvances.views.DummyDetail({
        prevCard: this,
        record: records[0]
      });
      this.ownerCt.setActiveItem(dummyCard, 'slide');
    }
  }
});

Ext.reg('DummyDetail', headvances.views.DummyDetail);
