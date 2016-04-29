var PieChartDataSample = {
  "recordName"  : "name",
  "chartField"  : 'relevance', 
  "chartFields" : ['relevance'],

  "data" : [
    {"name" : "Article", "relevance" : 20} ,
    {"name" : "Blog", "relevance" : 5} ,
    {"name" : "Forum", "relevance" : 30} ,
    {"name" : "Product", "relevance" : 30} ,
    {"name" : "Classified", "relevance" : 16} ,
    {"name" : "Job", "relevance" : 6}
  ]
};
headvances.views.PieChartPanel = Ext.extend(Ext.Panel, {
  title:   'Pie Chart',
  cls:     'PieChartPanel',
  iconCls: 'pie',
  scroll:  'vertical',
  layout:  'fit',

  initComponent: function() {
    if(this.chartData == null) {
      this.chartData = PieChartDataSample;
    }

    var store = new Ext.data.JsonStore({ 
      fields: [this.chartData.recordName, this.chartData.chartFields], 
      data: this.chartData.data }
    );

    this.chartPanel = new Ext.Panel({
      title:   'Pie Chart',
      layout:  'fit',
      iconCls: 'pie',

      dockedItems: {
        dock: 'top',
        xtype: 'toolbar',
        title: this.title,
        items: [
          { xtype: 'spacer' }, 
          headvances.views.Util.newBackButton(this, this.prevCard),
          { xtype: 'button', iconCls: 'shuffle', iconMask: true, ui: 'plain', handler: this.onRefresh}
        ]
      },
      items: [
        {
          xtype: 'chart',
          cls: 'piecombo1',
          theme: 'Demo',
          store: store,
          animate: true,
          legend: {
            position: { portrait: 'bottom', landscape: 'left' }
          },
          interactions: ['rotate', 'reset'],
          series: [{
            type: 'pie',
            field: this.chartData.chartField,
            donut: 25,
            showInLegend: true,
            highlightDuration: 500,
            highlight: { segment: { margin: 20 } },
            label: { field: this.chartData.recordName }
          }]
        }
      ]
    });

    this.items = this.chartPanel;
    headvances.views.PieChartPanel.superclass.initComponent.call(this);
  },

  onRefresh : function() {
    var data = this.chartPanel.store.getData() ;
    this.chartPanel.store.loadData(data); 
  }
});

Ext.reg('PieChartPanel', headvances.views.PieChartPanel);
