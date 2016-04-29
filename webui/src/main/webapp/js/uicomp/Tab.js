function Tab(id) {
  this.id = id ;
  this.tabCounter = 1 ;
  this.addTabContent = null;
  
  var thisTab = this ;

  jQuery(function() {
    // tabs init with a custom tab template and an "add" callback filling in the content
    var $tabs = $('#' + id).tabs({
      tabTemplate: '<li>' + 
                     '<a href="#{href}">#{label}</a> ' + 
                     '<span class="ui-icon ui-icon-close"/>' + 
                   '</li>',
      add: function(event, ui) {
        var tab_content = thisTab.addTabContent ;
        if(tab_content == null) {
          tab_content = 'Tab '+ thisTab.tabCounter + ' content.';
        }
        $(ui.panel).append(tab_content);
        $tabs.tabs('select', ui.index);
        thisTab.addTabContent = null ;
      }
    });

    // close icon: removing the tab on click
    $('#' + id + ' span.ui-icon-close').live('click', function() {
      var index = $('li',$tabs).index($(this).parent());
      $tabs.tabs('remove', index);
    });
  });

  this.addTab = function(id, title, content) {
    if(id == null) {
      id =  thisTab.id + '-' + thisTab.tabCounter;
      thisTab.tabCounter++;
    } else {
      this.removeTabById(id);
    }
    if(title == null) title = 'Tab '+ thisTab.tabCounter ;
    this.addTabContent = content ;
    $("#" + thisTab.id).tabs('add', '#'+ id, title, 0);
  };

  this.removeTabById = function(id) {
    if(id == null) return ;
    var tabs = $("#" + this.id  + ' > div');
    for(var i = 0; i < tabs.length; i++) {
      var selId =  $(tabs[i]).attr('id') ;
      if(id == selId) {
        $('#' + this.id).tabs('remove', i);
        return ; 
      }
    }
  }
} ;

if(uicomp == null) var uicomp = {} ;
uicomp.Tab = Tab ;
