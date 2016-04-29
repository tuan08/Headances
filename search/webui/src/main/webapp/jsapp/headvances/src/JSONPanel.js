headvances.views.JSONPanel = Ext.extend(Ext.Panel, {
  scroll: 'vertical',
  styleHtmlContent: true,

  initComponent: function(){
    var toolbarBase = { xtype: 'toolbar', title: this.title };
    if (this.prevCard !== undefined) {
      toolbarBase.items =  headvances.views.Util.newBackButton(this, this.prevCard) ;
    }
    
    this.dockedItems = toolbarBase;
    var data = { json: JSON.stringify(this.jsonObject, null, 2)} ;
    this.items = [{
      tpl: new Ext.XTemplate([
        '<pre>',
        '{json}',
        '</pre>'
      ]),
      data: data,
      styleHtmlContent: true
    }];
    
    headvances.views.JSONPanel.superclass.initComponent.call(this);
  }
});

Ext.reg('jsonpanel', headvances.views.JSONPanel) ;
