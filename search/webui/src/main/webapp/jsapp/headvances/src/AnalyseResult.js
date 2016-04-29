headvances.views.AnalyseResult = Ext.extend(Ext.Panel, {
  scroll: 'both',
  layout: { type: 'vbox', align: 'stretch' },
  cls: 'AnalyseResult',

  initComponent: function() {
    this.dockedItems = [{
      xtype: 'toolbar',
      items: [ 
        headvances.views.Util.newBackIconButton(this, this.prevCard)
      ]
    }];
     
    this.items = [{
      tpl: new Ext.XTemplate([
        '<h6>Extracted opinion</h6>',
        '<hr/>',
        '<p>Entities: {request.entities}</p>',
        '<tpl for="extractOpinions">',
        '  <p>{opinion}</p>',
        '</tpl>',
        '<br/>',

        '<h6>Text Classify</h6>',
        '<hr/>',
        '<div>Category: {textClassify.category}</div>',
        '<div>Probability: {textClassify.probability}</div>',
        '<div>Time: {textClassify.analyzedTime}ms</div>',
        '<br/>',

        '<h6>Text</h6>',
        '<div>Time: {textAnalyse.analyzedTime}ms</div>',
        '<hr/>',
        '<tpl for="textAnalyse.line">',
        '  <p>{.}</p>',
        '</tpl>'
      ]),
      data: this.analyseResult,
      styleHtmlContent: true
    }];
    
    Ext.repaint();
    
    headvances.views.AnalyseResult.superclass.initComponent.call(this);
  }
});

Ext.reg('AnalyseResult', headvances.views.AnalyseResult);
