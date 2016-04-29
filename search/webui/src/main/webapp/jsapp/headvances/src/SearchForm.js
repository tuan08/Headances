headvances.views.SearchForm = Ext.extend(Ext.Panel, {
  layout: 'card',

  initComponent: function() {
    this.formTab = new Ext.form.FormPanel({
      id: 'searchForm',
      url: '/rest/query',
      mode: 'get',
      submitOnAction: false,
      submit: function() { this.onSearch() ; },

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
          xtype: 'textfield', name: 'query', placeHolder: 'Search Keywords', required: true
        },
        {
          layout: 'hbox',
          defaults: {xtype: 'button', flex: 1, style: 'margin: .5em;'},
          items: [
            { text: 'Search', handler: this.onSearch, scope: this }
          ]
        },
        {
          xtype: 'fieldset',
          title: 'Search Field',
          defaults: { xtype: 'checkboxfield', labelWidth: '120px', checked: true },
          items: [ 
            { name: 'title', label: 'Title', value: true },
            { name: 'description', label: 'Description', value: true },
            { name: 'content', label: 'Content', value: true }
          ]
        },
        {
          xtype: 'fieldset',
          title: 'Filter',
          defaults: { xtype: 'checkboxfield', labelWidth: '120px', checked: true },
          items: [ 
            {
              xtype: 'selectfield', name: 'contentType', label: 'Content Type', value: null,
              options: [
                { text: 'All', value: null }, 
                { text: 'Article', value: 'article' }, 
                { text: 'Blog', value: 'blog' }, 
                { text: 'Forum', value: 'forum' },
                { text: 'Product', value: 'product' },
                { text: 'Classified', value: 'classified' },
                { text: 'Job', value: 'job' }
              ]
            },
            {
              xtype: 'selectfield', name: 'pageType', label: 'Page Type', value: 'detail',
              options: [
                { text: 'All', value: 'all' }, 
                { text: 'Detail', value: 'detail' }, 
                { text: 'List', value: 'list' }, 
                { text: 'Unknown', value: 'unknowni-type' }
              ]
            }
          ]
        }
      ]
    }) ;


    this.panel = new Ext.Panel({
      xtype: 'card',
      title: 'Search',
      iconCls: 'search', cls: 'search',
      scroll: 'vertical',
      dockedItems: [
        { dock: 'top', xtype: 'toolbar', title: 'Search Form' }
      ],
      items: this.formTab
    });
    this.items = this.panel ;
    headvances.views.SearchForm.superclass.initComponent.call(this);
  },

  onSearch : function(button, event) {
    var currPanel = this ;
    currPanel.setLoading(true, true);
    Ext.Ajax.request({
      url: '../../rest/query',
      method: 'GET',
      params: this.formTab.getValues(),
      timeout: 15000,
      success: function(response, opts) {
        var jsonObj = Ext.util.JSON.decode(response.responseText);
        currPanel.setLoading(false);
        var nextCard = new headvances.views.SearchRecordList({ prevCard: this.panel, searchData: jsonObj});
        currPanel.setActiveItem(nextCard, 'slide');
      },
      failure: function ( result, request) { 
        Ext.Msg.alert('Search Failed', result.responseText); 
        currPanel.setLoading(false);
      } 
    });
  }
});

Ext.reg('SearchForm', headvances.views.SearchForm);
