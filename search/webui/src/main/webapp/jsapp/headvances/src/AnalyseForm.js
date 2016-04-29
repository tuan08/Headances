headvances.views.AnalyseForm = Ext.extend(Ext.Panel, {
  layout: 'card',

  initComponent: function() {
    this.formTab = new Ext.form.FormPanel({
      id: 'AnalyseForm',
      url: '/rest/query',
      mode: 'POST',
      submitOnAction: false,
      submit: function() { this.doAnalyse() ; },

      listeners: {
        submit : function(form, result){
          console.log('success',Ext.toArray(arguments));
        },
        exception : function(form, result){
          console.log('failure', Ext.toArray(arguments));
        },
        scope: this
      },

      items: [
        {
          xtype: 'selectfield', name: 'algorithm',  value: "statistic",
          label: 'ML Algorithm', labelWidth: '120px',
          options: [
            { text: 'Statistic', value: 'statistic' }, 
            { text: 'Maxent', value: 'maxent' }, 
            { text: 'CRF', value: 'crf' }
          ]
        },
        {
          xtype: 'selectfield', name: 'mode',  value: "all",
          label: 'Opinion mode', labelWidth: '120px',
          options: [
            { text: 'All', value: 'all' }, 
            { text: 'Product Opinion', value: 'product' }
          ]
        },
        {
          xtype: 'textfield', name: 'entities', label: 'Entities', labelWidth: '120px',
        },
        {
          xtype: 'textareafield', 
          name: 'text',
          maxRows: 10,
          value: 
            'Tách từ các trường hợp đặc biệt:\n\n' +
            'Học sinh học sinh học\n' +
            'cần tuyên truyền thông tin\n' +
            'mặt người và mặt trăng\n\n' +

            'Nhận dạng cụm tiền đồng: 1000 đồng, 4.199.000 đ, 2.990.000đ,  3-4 triệu, ' +
            '1,2 triệu, 1000(vnđ), 1000vnd, 1tr2, 3triệu3, 12.000đ/km, 8triệu/tháng, 2 triệu 2, ' +
            '100 ngàn đồng, ba đô la\n\n' +

            'Nhận dạng cụm tiền usd: 1000 (usd), 1000 usd, 15.5(usd), usd1000, 2100$/m2, ' +
            '3 trăm tỷ đô, ba tỷ đô\n\n' +

            'Nhận dạng cụm số phone: 0928996379, 01686778225, 0928.996.379, 0934.489768, ' +
            '04.6277.1234, 84.4.37547.460, 04.3.786.82.92, 04-3736-6491, 04-37831999, ' +
            '(08) 3855 6666, (+84) 983 870 204, (+84) 4 3754 8864, (+84) 983.870.205, ' +
            '(+84) 4.37.54.78.13, 04 3754 8864, 0928 996 379, 0164 203 5555' +
            '\n\n' +

            'Nhận dạng cụm thời gian: 00:36, 00:36am, 00:36pm, 00:36:00, 00:36:00am, 00:36:00pm, ' +
            '07:00 GMT, 07:00 GMT+7, 07:00 UTC, 07:00 UTC-7:00, 07:00 (GMT+7), 14:20(GMT+7), ' +
            '07:00 GMT-07, 07:00 (GMT-07), 07:00 AM (GMT+7), 07:00 PM (GMT-07:00), ' +
            '06:02:00 AM (GMT-07), 07:00 (UTC-7:00), 07:00 UTC-07:00, 07:00 (UTC-07:00)' +

            'Nhận dạng cụm ngày tháng:\n' + 
            '02/20/2012 , 02-20-2012, 02.20.2012\n' +
            'Chủ nhật 12 tháng hai năm 2012 blah, blah, blah, blah...\n' + 
            'Ngày 12 tháng hai năm 2012 blah, blah, blah, blah...\n' +
            'Hanoi 12 tháng hai năm 2012 blah, blah, blah, blah...\n' +
            '01 January, 2012 blah, blah, blah, blah...\n' +
            'Thursday, February 02, 2012 blah, blah, blah, blah...\n' +
            'Wed, February 1, 2012 blah, blah, blah, blah...\n' +
            'Feb 02, 2012 blah, blah, blah, blah...\n' +
            'February, 01 2012 blah blah blah blah...' +
            '\n\n' +
            
            'Nhận dạng cụm địa chỉ:\n' +
            '186 Trương Định, Hà Nội blah, blah, blah, blah...\n' +
            '186 Trương Định, Phường Trương Định, Quận Hai Bà Trưng, Hà Nội blah, blah, blah, blah...\n' +
            '186 Trương Định, Phường Trương Định, Hà Nội blah, blah, blah, blah...\n' +
            '186 Trương Định, Quận Hai Bà Trưng, Hà Nội blah, blah, blah, blah...\n' +
            '186 Trương Định, Quận Hai Bà Trưng blah, blah, blah, blah...\n' +
            '186 Trương Định blah, blah, blah, blah...\n' +
            '23/30 Trương Định blah, blah, blah, blah...\n' +
            '23n/30/100 Trương Định blah, blah, blah, blah...\n'
        },
        { 
          xtype: 'button', 
          style: 'margin: .5em', 
          text: 'Analyse', 
          handler: this.doAnalyse, 
          scope: this 
        }
      ]
    }) ;

    this.panel = new Ext.Panel({
      xtype: 'card',
      scroll: 'vertical',
      dockedItems: [
        { dock: 'top', xtype: 'toolbar', title: 'Analyse' }
      ],
      items: this.formTab
    });
    this.items = this.panel ;
    headvances.views.AnalyseForm.superclass.initComponent.call(this);
  },

  doAnalyse: function(button, event) {
    var currPanel = this ;
    currPanel.setLoading(true, true);
    Ext.Ajax.request({
      url: '../../rest/analyse',
      method: 'POST',
      params: this.formTab.getValues(),
      timeout: 15000,
      success: function(response, opts) {
        var jsonObj = Ext.util.JSON.decode(response.responseText);
        currPanel.setLoading(false);
        var config = {
          prevCard: this.panel,
          analyseResult: jsonObj
        }
        var nextCard = new headvances.views.AnalyseResult(config);
        currPanel.setActiveItem(nextCard, 'slide');
      },
      failure: function (result, request) { 
        Ext.Msg.alert('Analyse Failed', result.responseText); 
        currPanel.setLoading(false);
      } 
    });

  }
});

Ext.reg('AnalyseForm', headvances.views.AnalyseForm);
