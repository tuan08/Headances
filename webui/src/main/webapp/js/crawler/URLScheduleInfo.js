if(crawler == null) var crawler = {} ;

crawler.URLScheduleInfo = Backbone.Model.extend({
	update : function() { 
    var member = this.get('member') ;
    var data = crawler.Server.getURLScheduleInfo(member.host, member.port) ;
    this.set({data: data});
  }
});


crawler.URLScheduleInfoView = Backbone.View.extend({
  events: { 
    'click .onUpdateURLScheduleInfo': 'onUpdateURLScheduleInfo',
  },    
  
  _template: _.template(
    "<table class='border' style='margin-bottom: 10px'>" +
    "  <thead>" +
    "    <tr>" +
    "      <th>Time</th>" +
    "      <th>Exec Time</th>" +
    "      <th>URL Count</th>" +
    "      <th>Schedule</th>" +
    "      <th>Delay Schedule</th>" +
    "      <th>Pending</th>" +
    "      <th>Waiting</th>" +
    "    </tr>" +
    "  </thead>" +

    "  <tbody style='height: 400px; overflow-y: scroll'>" +
    "  <% _.each(data, function(entry) { %>" +
    "    <tr>" +
    "      <td><%=new Date(entry.time).toUTCString()%></td>" +
    "      <td><%=entry.execTime%></td>" +
    "      <td><%=entry.urlCount%></td>" +
    "      <td><%=entry.scheduleCount%></td>" +
    "      <td><%=entry.delayScheduleCount%></td>" +
    "      <td><%=entry.pendingCount%></td>" +
    "      <td><%=entry.waitingCount%></td>" +
    "    </tr>" +
    "  <% }); %>"  +
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

    $("table", el ).dataTable({
      "sPaginationType": "full_numbers"
    });
    crawler.Crawler.workspace.addTab('URLScheduleInfoTab', 'URL Schedule Info', el) ;
  },

	onUpdateURLScheduleInfo: function() { 
    alert('onUpdateURLScheduleInfo') ;
    this.model.update() ; 
  }
});
