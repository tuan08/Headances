if(crawler == null) var crawler = {} ;

crawler.FetcherInfo = Backbone.Model.extend({
	update : function() { 
    var member = this.get('member') ;
    var data = crawler.Server.getFetcherInfos(member.host, member.port) ;
    this.set({data: data});
  }
});


crawler.FetcherInfoView = Backbone.View.extend({
	el: $("body"),

  events: { 
    'click span.onShowThreadDetail': 'onShowThreadDetail'
  },    
  
  _template: _.template(
    "<table class='border' style='margin-bottom: 10px'>" +
    "  <thead>" +
    "    <tr>" +
    "      <th>#</th>" +
    "      <th>Fetch</th>" +
    "      <th>RC 100</th>" +
    "      <th>RC 200</th>" +
    "      <th>RC 300</th>" +
    "      <th>RC 400</th>" +
    "      <th>RC 500</th>" +
    "      <th>RC Other</th>" +
    "    </tr>" +
    "  </thead>" +

    "  <tbody style='height: 400px; overflow-y: scroll'>" +
    "  <%for(var i =0; i < data.length; i++) { %>" +
    "    <tr>" +
    "      <td><span class='clickable onShowThreadDetail'><%=i%></span></td>" +
    "      <td><%=data[i].Fetch%></td>" +
    "      <td><%=data[i]['RC 100']%></td>" +
    "      <td><%=data[i]['RC 200']%></td>" +
    "      <td><%=data[i]['RC 300']%></td>" +
    "      <td><%=data[i]['RC 400']%></td>" +
    "      <td><%=data[i]['RC 500']%></td>" +
    "      <td><%=data[i]['RC Other']%></td>" +
    "    </tr>" +
    "  <% } %>"  +
    "  </tbody>" +
    "</table>" +
    "<br/><br/>" +
    "<h6>Recent Fetch Urls</h6>" +
    "<hr/>" +
    "<div id='RecentFetchUrls' style='height: 175px; overflow-y: scroll'>" +
    "  <% _.each(data[0]['Recent Fetch Urls'], function(url) { %>" +
    "    <div><a href='<%=url%>'><%=url%></a><div>" +
    "  <% }); %>"  +
    "</div>"
  ),
  
	initialize: function () {
	  _.bindAll(this, 'render', 'onShowThreadDetail');
	  this.model.bind('change', this.render);
	  this.render() ;
	},
  
	render: function() {
    var data = this.model.get('data') ;
    //var json = JSON.stringify(data, null, ' ');
	  var params = { data : data } ;
    var html = this._template(params);
    var el = $("<div style='padding-bottom: 30px'>").append(html);

    $("table", el ).dataTable({ "sPaginationType": "full_numbers" });
    crawler.Crawler.workspace.addTab('FetcherInfoTab', 'Fetcher Info', el) ;
  },

	onShowThreadDetail: function(evt) { 
    var index = evt.target.innerHTML ;
    var sel = this.model.get('data')[index] ;
    var urls = sel['Recent Fetch Urls'];
    var html = '' ;
    for(var i = 0; i < urls.length; i++) {
      html += "<div><a href='" + urls[i] +"'>" + urls[i] + "</a><div>" ;
    }
    $('#RecentFetchUrls').html(html) ;
  }
});
