if(crawler == null) var crawler = {} ;

crawler.DataProcessInfo = Backbone.Model.extend({
	update : function() { 
    var member = this.get('member') ;
    var data = crawler.Server.getDataProcessInfo(member.host, member.port) ;
    this.set({data: data});
  }
});


crawler.DataProcessInfoView = Backbone.View.extend({
  events: { 
    'click .onUpdateDataProcessInfo': 'onUpdateDataProcessInfo',
  },    
  
  _template: _.template(
    "<table class='border'>" +
    "  <tr>" +
    "    <td>Process Count</td>" +
    "    <td><%=data.processCount%></td>" +
    "  </tr>" +
    "  <tr>" +
    "    <td>Sum Process Time</td>" +
    "    <td><%=data.sumProcessTime%>ms</td>" +
    "  </tr>" +

    "  <tr>" +
    "    <td>HTML Process Count</td>" +
    "    <td><%=data.htmlProcessCount%></td>" +
    "  </tr>" +
    "  <tr>" +
    "    <td>Sum HTML Process time</td>" +
    "    <td><%=data.sumHtmlProcessTime%>ms</td>" +
    "  </tr>" +
    "</table>" 
  ),
  
	initialize: function () {
	  _.bindAll(this, 'render');
	  this.model.bind('change', this.render);
	  this.render() ;
	},
  
	render: function() {
    var data = this.model.get('data') ;
    //var json = JSON.stringify(data, null, ' ');
	  var params = { data : data } ;
    var html = this._template(params);
    crawler.Crawler.workspace.addTab('DataProcessInfoTab', 'Data Process Info', html) ;
  },

	onUpdateDataProcessInfo: function() { 
    alert('onUpdateDataProcessInfo') ;
    this.model.update() ; 
  }

});
