headvances.views.HtmlPage = Ext.extend(Ext.Panel, {
  autoLoad: 'about.html',
  scroll: 'vertical',
  styleHtmlContent: true,
  initComponent: function(){
    var toolbarBase = { xtype: 'toolbar', title: this.title };
    if (this.prevCard !== undefined) {
      toolbarBase.items =  headvances.views.Util.newBackButton(this, this.prevCard) ;
    }
    
    this.dockedItems = toolbarBase;
    
    Ext.Ajax.request({
      url: this.url,
      success: function(rs) { this.update(rs.responseText); },
      scope: this
    });
    headvances.views.HtmlPage.superclass.initComponent.call(this);
  }
});

Ext.reg('htmlpage', headvances.views.HtmlPage) ;
