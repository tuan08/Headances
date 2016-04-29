var BarChartDataSample = {
  recordName   : 'name',
  chartFields : ['iphone 3gs', 'iphone 4'],

  data : [
    {"name" : "1/12/2011", "iphone 3gs" : 20, "iphone 4": 10 } ,
    {"name" : "2/12/2011", "iphone 3gs" : 30, "iphone 4": 15 } ,
    {"name" : "3/12/2011", "iphone 3gs" : 30, "iphone 4": 15 } ,
    {"name" : "4/12/2011", "iphone 3gs" : 30, "iphone 4": 15 } 
  ]
};

var BarChartGeneralSample = {
  recordName   : 'name',
  chartFields : ['iphone 3gs', 'iphone 4'],

  data : [
    {"name" : "all", "iphone 3gs" : 100, "iphone 4": 65 } ,
    {"name" : "article", "iphone 3gs" : 25, "iphone 4": 12 } ,
    {"name" : "Blog", "iphone 3gs" : 35, "iphone 4": 25 } ,
    {"name" : "Forum", "iphone 3gs" : 35, "iphone 4": 18 } ,
    {"name" : "Product", "iphone 3gs" : 35, "iphone 4": 35 } ,
    {"name" : "Classified", "iphone 3gs" : 15, "iphone 4": 21 },
    {"name" : "Job", "iphone 3gs" : 15, "iphone 4": 21 }
  ]
};

var BarChartOpinionSample = {
  recordName   : 'name',
  chartFields : ['rank -2', 'rank -1', "rank 0", "rank 1", "rank 2"],

  data : [
    { "name" : "Article",    "rank -2" : 25, "rank -1": 12, "rank 0": 10, "rank 1" : 15, "rank 2" : 17 } ,
    { "name" : "Blog",       "rank -2" : 25, "rank -1": 12, "rank 0": 10, "rank 1" : 15, "rank 2" : 17 } ,
    { "name" : "Forum",      "rank -2" : 25, "rank -1": 12, "rank 0": 10, "rank 1" : 15, "rank 2" : 17 } ,
    { "name" : "Product",    "rank -2" : 25, "rank -1": 12, "rank 0": 10, "rank 1" : 15, "rank 2" : 17 } ,
    { "name" : "Classified", "rank -2" : 25, "rank -1": 12, "rank 0": 10, "rank 1" : 15, "rank 2" : 17 } ,
    { "name" : "Job",        "rank -2" : 25, "rank -1": 12, "rank 0": 10, "rank 1" : 15, "rank 2" : 17 }
  ]
};

headvances.views.BarChartPanel = Ext.extend(Ext.Panel, {
  title:   'Bar Chart',
  iconCls: 'bar',
  cls:     'BarChartPanel',
  scroll:  'vertical',
  layout:  'fit',

  initComponent: function() {
    if(this.chartData == null) {
      this.chartData = BarChartOpinionSample;
    }

    var store = new Ext.data.JsonStore({ 
      fields: [this.chartData.recordName, this.chartData.chartFields], 
      data: this.chartData.data }
    );

    this.barChart = new Ext.chart.Panel({
      title:   this.title,
      iconCls: 'bar',
      layout:  'fit',

      dockedItems: [
        headvances.views.Util.newBackButton(this, this.prevCard),
        { iconCls: 'shuffle', iconMask: true, ui: 'plain', handler: this.onRefresh }
      ] ,
      items: [{
        xtype: 'chart',
        cls: 'barcombo1',
        store: store,
        animate: true,
        shadow: false,
        legend: { position: { portrait: 'right', landscape: 'top' } },
        interactions: [
          'reset',
          'togglestacked',
          {
            type: 'panzoom',
            axes: { left: {} } 
          },
          {
            type: 'iteminfo',
            gesture: 'taphold',
            panel: {
              dockedItems: [ { dock: 'top', xtype: 'toolbar', title: 'Details' } ],
              html: 'Testing'
            },
            listeners: {
              'show': function(me, item, panel) {
                var storeItem = item.storeItem;
                panel.update('<ul><li><b>Month:</b> ' + storeItem.get('name') + '</li><li><b>Value: </b> ' + storeItem.get('2008') + '</li></ul>');
              }
            }
          },
          {
            type: 'itemcompare',
            offset: { x: -10 },
            listeners: {
              'show': function(interaction) {
                var val1 = interaction.item1.value, val2 = interaction.item2.value;

                this.barChart.descriptionPanel.setTitle(val1[0] + ' to ' + val2[0] + ' : ' + Math.round((val2[1] - val1[1]) / val1[1] * 100) + '%');
                this.barChart.headerPanel.setActiveItem(1, { type: 'slide', direction: 'left' });
              },
              'hide': function() {
                this.barChart.headerPanel.setActiveItem(0, { type: 'slide', direction: 'right' });
              },
              scope: this
            }
          }
        ],
        axes: [
          {
            type: 'Numeric',
            position: 'bottom',
            fields: this.chartData.chartFields,
            label: { renderer: function(v) { return v.toFixed(0); } },
            title: 'Hits (thousands)',
            minimum: 0
          },
          {
            type: 'Category',
            position: 'left',
            fields: ['name'],
            title: 'Month'
          }
        ],
        series: [{
          type: 'bar',
          xField: this.chartData.recordName,
          yField: this.chartData.chartFields,
          axis: 'bottom',
          highlight: true,
          showInLegend: true
        }]
      }]

    });
    this.items = this.barChart;
    headvances.views.BarChartPanel.superclass.initComponent.call(this);
  },

  onRefresh : function() {
    var data = this.barChart.store.getData() ;
    this.barChart.store.loadData(data); 
  }
});

Ext.reg('BarChartPanel', headvances.views.BarChartPanel);
