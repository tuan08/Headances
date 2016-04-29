if(crawler == null) var crawler = {} ;

crawler.Master = Backbone.Model.extend({
});


crawler.MasterView = Backbone.View.extend({
	el: $("#ctrl-col-master"),

  events: { 
    'click li.onClickSiteInfo': 'onClickSiteInfo',
    'click li.onClickDataProcessInfo': 'onClickDataProcessInfo',
    'click li.onClickURLScheduleInfo': 'onClickURLScheduleInfo',
    'click li.onClickURLCommitInfo': 'onClickURLCommitInfo',
    'click li.onClickJVMInfo': 'onClickJVMInfo'
  },    
  
  _template: _.template(
    "<h6>" + 
    "  <a href=\"javascript:$('#master-ctrl-block').toggle('slow')\">" +
    "    <%=member.host%>:<%=member.port%>" + 
    "  </a>" + 
    "</h6>" +
    "<ul id='master-ctrl-block'>" +
    //" <li>Queue" +
    //"   <ul>" +
    //"     <li>Queue 1</li>" +
    //"   </ul>" +
    //" </li>" +
    " <li class='clickable onClickSiteInfo'>Site Info</li>" +
    " <li class='clickable onClickDataProcessInfo'>Data Process Info</li>" +
    " <li class='clickable onClickURLScheduleInfo'>URL Schedule Info</li>" +
    " <li class='clickable onClickURLCommitInfo'>URL Commit Info</li>" +
    " <li class='clickable onClickJVMInfo'>JVM Info</li>" +
    "</ul>" +
    "<a href='javascript:crawler.Crawler.testUpdate()'>Test Update</a>"
  ),
  
	initialize: function () {
	  _.bindAll(this, 'render', 'onClickSiteInfo', 'onClickDataProcessInfo', 'onClickURLScheduleInfo', 'onClickURLCommitInfo');
	  this.model.bind('change', this.render);
	  this.render() ;
	},
  
	render: function() {
	  var params = { member: this.model.get('member') } ;
    $(this.el).html(this._template(params)); 
  },

  onClickSiteInfo : function() { 
    var info = new crawler.SiteInfo({ member: this.model.get('member')});
    info.update() ;
    var view = new crawler.SiteInfoView({ model: info });
  },

  onClickDataProcessInfo : function() { 
    var info = new crawler.DataProcessInfo({ member: this.model.get('member')});
    info.update() ;
    var dataProcessInfoView = new crawler.DataProcessInfoView({ model: info });
  },

  onClickURLScheduleInfo : function() { 
    var info = new crawler.URLScheduleInfo({ member: this.model.get('member')});
    info.update() ;
    var view = new crawler.URLScheduleInfoView({ model: info });
  },

  onClickURLCommitInfo : function() { 
    var info = new crawler.URLCommitInfo({ member: this.model.get('member')});
    info.update() ;
    var view = new crawler.URLCommitInfoView({ model: info });
  },

  onClickJVMInfo : function() { 
    var member = this.model.get('member') ;
    var data = crawler.Server.getJVMInfo(member.host, member.port) ;
    var info = new uicomp.JVMInfo({ data: data});
    var view = new uicomp.JVMInfoView({ model: info });
    crawler.Crawler.workspace.addTab('JVMInfoTab', 'JVM Info', view.getHtmlFragment());
  }
});
