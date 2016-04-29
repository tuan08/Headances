Ext.regModel('WebMonitorSummaryCommand', {
  fields: ['label', 'command']
});


headvances.views.WebMonitorSummary = Ext.extend(Ext.Panel, {
  scroll: 'vertical',
  layout: { type: 'vbox', align: 'stretch' },
  cls: 'WebMonitorSummary',

  initComponent: function() {
    this.dockedItems = [{
      xtype: 'toolbar',
      items: [ 
        headvances.views.Util.newBackIconButton(this, this.prevCard),
        {
          iconMask: true, iconCls: 'refresh', 
          scope: this, 
          handler: function() { this.onReload(); }
        },
        {
          iconMask: true, iconCls: 'arrow_right', 
          scope: this, 
          handler: function() { this.onExecute() ; }
        },
        {
          iconMask: true, iconCls: 'trash', 
          scope: this, 
          handler: function() { }
        },
        {
          iconMask: true, iconCls: 'info', 
          scope: this, 
          handler: function() { 
           var config = { 
             title: 'Execute Info',
             prevCard: this, 
             jsonObject: this.monitorData
           }
           var nextCard = new headvances.views.JSONPanel(config);
           this.ownerCt.setActiveItem(nextCard, 'slide');
          }
        }
      ]
    }];
     
    this.items = [{
      tpl: new Ext.XTemplate([
        '<h6>{description}</h6>',
        '<div>Design:    *****</div>',
        '<div>Camera:    ****</div>',
        '<div>Usability: ****</div>',
        '<div>Speed:     ***</div>',
        '<div>Battery:   xxxxx</div>',
        '<div>Problem:   xxx</div>'
      ]),
      data: this.monitorData,
      styleHtmlContent: true
    }];
    
    var commands = [
      { label: 'Relevance Distribution', command: 'relevance'},
      { label: 'Opinion Relevance Distribution', command: 'opinionRelevance'},
      { label: 'Opinion Category Distribution', command: 'opinionCategory'},
      { label: 'Opinion List', command: 'opinionList'}
    ];

    var commandStore = new Ext.data.Store({
      model: 'WebMonitorSummaryCommand',
      data:  commands
    }) ;

    var newCommandList = function(onSelect, scope) {
      var commandList = new Ext.List({
        itemTpl: [ '<h6>{label}</h6>'],
        store: commandStore,
        listeners: {
          selectionchange: {fn: onSelect, scope: scope}
        },
        style: 'width: 100%',
        styleHtmlContent: true,
        scroll: false
      });
      return commandList;
    };

    this.items.push({ xtype: 'toolbar', title: 'More Detail', cls: 'small_title' })
    var commandList = newCommandList(this.onMoreDetail, this);    
    this.items.push(commandList);

    Ext.repaint();
    
    this.listeners = {
      activate: { 
        fn: function(){
          commandList.getSelectionModel().deselectAll();
        }, 
        scope: this 
      }
    };
    
    headvances.views.WebMonitorSummary.superclass.initComponent.call(this);
  },

  createOpinionChartData : function(opinionDatas) {
    var chartData = {
      recordName   : 'name',
      chartFields : ['rank -1', "rank 0", "rank 1"],
      data : [ ]
    };
    if(opinionDatas == null) {
      var barData = { "name" : "Article", "rank -2" : 25, "rank -1": 12, "rank 0": 10, "rank 1" : 15, "rank 2" : 17 };
      chartData.data.push(barData) ;
    } else {
      for(var i = 0; i < opinionDatas.length; i++) {
        var opinionData = opinionDatas[i] ;
        var entry = { 
          "name"    : opinionData.name, 
          "rank -1" : opinionData.rankMinus1.count, 
          "rank 0"  : opinionData.rankPlus0.count, 
          "rank 1"  : opinionData.rankPlus1.count, 
        };
        chartData.data.push(entry) ;
      }
    }
    return chartData ;
  },

  createOpinionChartDataBk : function(opinionDatas) {
    var chartData = {
      recordName   : 'name',
      chartFields : ['rank -2', 'rank -1', "rank 0", "rank 1", "rank 2"],
      data : [ ]
    };
    if(opinionDatas == null) {
      var barData = { "name" : "Article", "rank -2" : 25, "rank -1": 12, "rank 0": 10, "rank 1" : 15, "rank 2" : 17 };
      chartData.data.push(barData) ;
    } else {
      for(var i = 0; i < opinionDatas.length; i++) {
        var opinionData = opinionDatas[i] ;
        var entry = { 
          "name"    : opinionData.name, 
          "rank -2" : opinionData.rankMinus2.count, 
          "rank -1" : opinionData.rankMinus1.count, 
          "rank 0"  : opinionData.rankPlus0.count, 
          "rank 1"  : opinionData.rankPlus1.count, 
          "rank 2"  : opinionData.rankPlus2.count 
        };
        chartData.data.push(entry) ;
      }
    }
    return chartData ;
  },
  
  onMoreDetail: function(selectionmodel, records){ 
    if (records[0] == undefined) return ;
    var nextCard = null ;
    if(records[0].data.command == 'relevance') {
      var chartData = {
        "recordName"  : "name",
        "chartField"  : 'count', 
        "chartFields" : ['count']
      };
      chartData.data = this.monitorData.contentDistribution;
      var config = {
        prevCard: this,
        title: 'Relevance Distribution',
        chartData: chartData
      };
      nextCard = new headvances.views.PieChartPanel(config);
    } else if(records[0].data.command == 'opinionRelevance') {
      var config = {
        prevCard: this,
        title: 'Opinion Relevance Distribution',
        chartData: this.createOpinionChartData(this.monitorData.opinionContentDistribution) 
      };
      nextCard = new headvances.views.BarChartPanel(config);
    } else if(records[0].data.command == 'opinionCategory') {
      var config = {
        prevCard: this,
        title: 'Opinion Category Distribution',
        chartData: this.createOpinionChartData(this.monitorData.opinionCategoryDistribution) 
      };
      nextCard = new headvances.views.BarChartPanel(config);
    } else {
      var config = {
        prevCard: this,
        title: 'Opinions',
        filter: {
          entity: this.monitorData.entity,
          contentType: '',
          category: '',
          page: 1
        },
        opinions: {}
      };
      var currPanel = this ;
      currPanel.setLoading(true, true);
      Ext.Ajax.request({
        url: '../../rest/webmonitor/opinions',
        method:  'GET',
        params:  config.filter,
        timeout: 5000,
        success: function(response, opts) {
          var jsonObj = Ext.util.JSON.decode(response.responseText);
          config.opinions = jsonObj;
          currPanel.setLoading(false);
          var nextCard = new headvances.views.WebMonitorOpinionList(config);
          currPanel.ownerCt.setActiveItem(nextCard, 'slide');
        },
        failure: function ( result, request) { 
          Ext.Msg.alert('Search Failed', result.responseText); 
          currPanel.setLoading(false);
        } 
      });
      return;
    }
    this.ownerCt.setActiveItem(nextCard, 'slide');
  },

  onExecute: function() { 
    var currPanel = this ;
    currPanel.setLoading(true, true);
    Ext.Ajax.request({
      url: '../../rest/webmonitor/execute?entity=' + currPanel.monitorData.entity,
      timeout: 5000,
      success: function(response, opts) {
        var jsonObj = Ext.util.JSON.decode(response.responseText);
        currPanel.monitorData = Ext.apply(currPanel.monitorData, jsonObj) ;
        currPanel.setLoading(false);
        Ext.Msg.alert('Execute Successfully', "Execute Successfully, press reload button to update the result"); 
      },
      failure: function ( result, request) { 
        Ext.Msg.alert('Execute Failed', result.responseText); 
        currPanel.setLoading(false);
      } 
    });
  },

  onReload: function() { 
    var currPanel = this ;
    currPanel.setLoading(true, true);
    Ext.Ajax.request({
      url: '../../rest/webmonitor/entity?entity=' + currPanel.monitorData.entity,
      timeout: 5000,
      success: function(response, opts) {
        var jsonObj = Ext.util.JSON.decode(response.responseText);
        currPanel.monitorData = Ext.apply(currPanel.monitorData, jsonObj) ;
        currPanel.setLoading(false);
      },
      failure: function ( result, request) { 
        Ext.Msg.alert('Execute Failed', result.responseText); 
        currPanel.setLoading(false);
      } 
    });
  }
});

Ext.reg('WebMonitorSummary', headvances.views.WebMonitorSummary);
