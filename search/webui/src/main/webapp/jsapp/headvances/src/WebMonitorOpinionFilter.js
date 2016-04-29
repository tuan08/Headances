headvances.views.WebMonitorOpinionFilter = Ext.extend(Ext.Panel, {
  layout: 'card',

  initComponent: function() {
    this.formTab = new Ext.form.FormPanel({
      id: 'opinionFilterForm',
      url: '/rest/query',
      mode: 'GET',
      submitOnAction: false,
      submit: function() { this.onFilter() ; },

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
          xtype: 'fieldset',
          title: 'Filter',
          defaults: { labelWidth: '120px'},
          items: [ 
            {
              xtype: 'selectfield', name: 'contentType', label: 'Content Type', value: "all",
              options: [
                { text: 'All', value: 'all' }, 
                { text: 'Comment', value: 'candidate:comment' }, 
                { text: 'Article', value: 'article' }, 
                { text: 'Blog', value: 'blog' }, 
                { text: 'Forum', value: 'forum' },
                { text: 'Product', value: 'product' },
                { text: 'Classified', value: 'classified' },
                { text: 'Job', value: 'job' }
              ]
            },
            {
              xtype: 'selectfield', name: 'category', label: 'Category', value: "all",
              options: [
                { text: 'All', value: 'all' }, 
                { text: 'General', value: 'product:general' },
                { text: 'Battery', value: 'product:battery'}, 
                { text: 'Design', value: 'product:design'},
                { text: 'Sound', value: 'product:sound'}, 
                { text: 'Display', value: 'product:display'}, 
                { text: 'Size', value: 'product:size'}, 
                { text: 'Keypad', value: 'product:keypad'}, 
                { text: 'Weight', value: 'product:weight'}, 
                { text: 'Camera', value: 'product:camera'}, 
                { text: 'Speed', value: 'product:speed'}, 
                { text: 'Usability', value: 'product:usability'}, 
                { text: 'Reception', value: 'product:reception'}, 
                { text: 'Feature', value: 'product:feature'}, 
                { text: 'Problem', value: 'product:problem'}
              ]
            },
            {
              xtype: 'selectfield', name: 'rank', label: 'rank', value: "all",
              options: [
                { text: 'All', value: 'all' }, 
                { text: '-1', value: '-1'}, 
                { text:  '0', value:  '0'}, 
                { text:  '1', value:  '1'}
              ]
            }
          ]
        },
        {
          layout: 'hbox',
          defaults: {xtype: 'button', flex: 1, style: 'margin: .5em;'},
          items: [
            { text: 'Filter', handler: this.onFilter, scope: this }
          ]
        }
      ]
    }) ;


    this.panel = new Ext.Panel({
      xtype: 'card',
      scroll: 'vertical',
      dockedItems: [
        { 
          dock: 'top',
          xtype: 'toolbar', 
          title: 'Opinion Filter' ,
          items: [
            headvances.views.Util.newBackButton(this, this.prevCard)
          ]
        }
      ],
      items: this.formTab
    });
    this.items = this.panel ;
    headvances.views.WebMonitorOpinionFilter.superclass.initComponent.call(this);
  },

  onFilter : function() {
    var filter = this.formTab.getValues();
    filter.page = 1 ;
    filter.entity = this.prevCard.filter.entity ;
    this.ownerCt.setActiveItem(this.prevCard, {
      type: 'slide',
      reverse: true,
      scope: this,
      after: function() { this.destroy(); }
    });
    this.prevCard.update(filter, false) ;
  }
});

Ext.reg('WebMonitorOpinionFilter', headvances.views.WebMonitorOpinionFilter);
