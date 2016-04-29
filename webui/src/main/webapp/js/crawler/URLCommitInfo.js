if(crawler == null) var crawler = {} ;

crawler.URLCommitInfo = Backbone.Model.extend({
	update : function() { 
    var member = this.get('member') ;
    var data = crawler.Server.getURLCommitInfo(member.host, member.port) ;
    this.set({data: data});
  }
});


crawler.URLCommitInfoView = Backbone.View.extend({
  events: { 
    'click .onUpdateURLCommitInfo': 'onUpdateURLCommitInfo',
  },    
  
  _template: _.template(
    "<table class='border'>" +
    "  <thead>" +
    "  <tr>" +
    "    <th>Time</th>" +
    "    <th>Exec Time</th>" +
    "    <th>Commit URL</th>" +
    "    <th>New URL</th>" +
    "    <th>New URL List</th>" +
    "    <th>New URL Detail</th>" +
    "  </tr>" +
    "  </thead>" +

    "  <tbody>" +
    "<% _.each(data, function(entry) { %>" +
    "  <tr>" +
    "    <td><%=new Date(entry.time).toUTCString()%></td>" +
    "    <td><%=entry.execTime%></td>" +
    "    <td><%=entry.urlcommitCount%></td>" +
    "    <td><%=entry.newURLFoundCount%></td>" +
    "    <td><%=entry.newURLTypeList%></td>" +
    "    <td><%=entry.newURLTypeDetail%></td>" +
    "  </tr>" +
    "<% }); %>"  +
    "  </tbody>" +
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
    var el = $("<div style='padding-bottom: 30px'>").append(html);
    $('table', el).dataTable({
      "sPaginationType": "full_numbers",
    });
    crawler.Crawler.workspace.addTab('URLCommitInfoTab', 'URL Commit Info', el) ;
  },

	onUpdateURLCommitInfo: function() { 
    alert('onUpdateURLCommitInfo') ;
    this.model.update() ; 
  }
});

