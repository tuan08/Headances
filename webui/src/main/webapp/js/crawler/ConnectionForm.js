if(crawler == null) var crawler = {} ;

crawler.ConnectionForm = Backbone.Model.extend({
});


crawler.ConnectionFormView = Backbone.View.extend({
	el: $('body'),

  _template: _.template(
    "<div id='connection-form' title='Create Connection'>" +
    "  <form>" +
    "    <fieldset>" +
    "      <label for='url'>URL</label>" +
    "      <input id='name' type='text' name='url' value='127.0.0.1:5700' class='text ui-widget-content ui-corner-all' />" +
    "      <label for='username'>Username</label>" +
    "      <input type='text' name='username' id='username' value='crawler' class='text ui-widget-content ui-corner-all' />" +
    "      <label for='password'>Password</label>" +
    "      <input type='password' name='password' id='password' value='crawler' class='text ui-widget-content ui-corner-all' />" +
    "    </fieldset>" +
    "  </form>" +
    "</div>"
  ),
  
	initialize: function () {
    this.model = new crawler.ConnectionForm() ;
	  this.render() ;
	},
  
	render: function() {
	  var params = {
    } ;
    $('body').append(this._template(params)); 

    $("#connection-form").dialog({
      autoOpen: false,
      height: 340, width: 400,
      modal: true,
      buttons: {
        Connect: function() {
          var url = $('#connection-form :input[name=\'url\']').val() ;
          var username = $('#connection-form :input[name=\'username\']').val() ;
          var password = $('#connection-form :input[name=\'password\']').val() ;
          var members = crawler.Server.connect(url, username, password) ;
          crawler.Crawler.onConnect(members);
          $(this).dialog( "close" );
        },
        Cancel: function() {
          $(this).dialog( "close" );
        }
      }
    });
  },

	show: function() {
    $("#connection-form" ).dialog( "open" );
  },

	close: function() {
    $("#connection-form").dialog("close");
  },
});

