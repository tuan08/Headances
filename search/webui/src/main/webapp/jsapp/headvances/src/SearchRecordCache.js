headvances.views.SearchRecordCache = Ext.extend(Ext.Panel, {
  scroll: 'vertical',
  layout: { type: 'vbox', align: 'stretch' },
  cls: 'SearchRecordDtail',

  initComponent: function(){
    if(this.cacheData == null) {
      this.cacheData = headvances.data.mock.SearchCacheResponse;
    }
    this.dockedItems = [{
      xtype: 'toolbar',
      items: [
        headvances.views.Util.newBackButton(this, this.prevCard) ,
        {
          text: 'Link',
          scope: this,
          handler: function() {
          }
        }
      ]
    }];
     
    this.items = [{
      tpl: new Ext.XTemplate([
        '<h6>{title}</h6>',
        '<p>{description}</p>',
        '<p>{content}</p>'
      ]),
      data: this.cacheData,
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
      style: 'width: 100%;',
      styleHtmlContent: true,
      scroll: false
    });
    
    this.items.push({ xtype: 'toolbar', title: 'References', cls: 'small_title' })
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
    
    headvances.views.SearchRecordCache.superclass.initComponent.call(this);
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

Ext.reg('SearchRecordCache', headvances.views.SearchRecordCache);
