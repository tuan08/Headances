if(crawler == null) var crawler = {} ;

crawler.SiteInfo = Backbone.Model.extend({
	update : function() { 
    var member = this.get('member') ;
    var data = crawler.Server.getSiteInfos(member.host, member.port) ;
    this.set({data: data});
  }
});


crawler.SiteInfoView = Backbone.View.extend({
  el: $('body'),
  events: { 
    'click #SiteInfoSet input': 'onClickInfo'
  },    
  
  _template: _.template(
    "<div style='margin-bottom: 10px' class='ui-widget-header ui-corner-all'>" +
    "  <span id='SiteInfoSet'>" +
    "    <input type='radio' id='ConfigBtn' name='info' checked='checked' /><label for='ConfigBtn'>Config</label>" +
    "    <input type='radio' id='ProcessBtn' name='info' /><label for='ProcessBtn'>Process</label>" +
    "    <input type='radio' id='StatusBtn' name='info' /><label for='StatusBtn'>Fetch Status</label>" +
    "    <input type='radio' id='ResponseCodeBtn' name='info' /><label for='ResponseCodeBtn'>Response Code</label>" +
    "    <input type='radio' id='ErrorCodeBtn' name='info' /><label for='ErrorCodeBtn'>Error Code</label>" +
    "    <input type='radio' id='PageTypesBtn' name='info' /><label for='PageTypesBtn'>Page Types</label>" +
    "    <input type='radio' id='FetchCountBtn' name='info' /><label for='FetchCountBtn'>Fetch Count</label>" +
    "  </span>" +
    "</div>" +

    "<div id='ConfigPane' class='SiteInfo'>" +
    "  <table class='border'>" +
    "    <thead>" +
    "    <tr>" +
    "      <th>Site</th>" +
    "      <th>XPath Config</th>" +
    "      <th>Inject URL</th>" +
    "      <th>Max Conn</th>" +
    "      <th>Crawl Deep</th>" +
    "      <th>Refresh Time</th>" +
    "      <th>Modify</th>" +
    "      <th>Status</th>" +
    "    </tr>" +
    "    </thead>" +
    "    <tbody>" +
    "  <% _.each(data, function(entry) { %>" +
    "    <tr>" +
    "      <td><%=entry.siteConfig.hostname%></td>" +
    "      <td><%=entry.siteConfig.xpathConfig != null%></td>" +
    "      <td><%=entry.siteConfig.injectUrl%></td>" +
    "      <td><%=entry.siteConfig.maxConnection%></td>" +
    "      <td><%=entry.siteConfig.crawlDeep%></td>" +
    "      <td><%=entry.siteConfig.refreshPeriod%></td>" +
    "      <td><%=entry.modify%></td>" +
    "      <td><%=entry.siteConfig.status%></td>" +
    "    </tr>" +
    "  <% }); %>"  +
    "    </tbody>" +
    "  </table>" +
    "</div>" +

    "<div id='ProcessPane' class='SiteInfo' style='display: none'>" +
    "  <table class='border'>" +
    "    <thead>" +
    "    <tr>" +
    "      <th>Site</th>" +
    "      <th>Schedule</th>" +
    "      <th>Process</th>" +
    "    </tr>" +
    "    </thead>" +
    "    <tbody>" +
    "  <% _.each(data, function(entry) { %>" +
    "    <tr>" +
    "      <td><%=entry.siteConfig.hostname%></td>" +
    "      <td><%=entry.scheduleCount%></td>" +
    "      <td><%=entry.processCount%></td>" +
    "    </tr>" +
    "  <% }); %>"  +
    "    </tbody>" +
    "  </table>" +
    "</div>" +

    "<div id='StatusPane' class='SiteInfo' style='display: none'>" +
    "  <table class='border'>" +
    "    <thead>" +
    "    <tr>" +
    "      <th>Site</th>" +
    "      <th>All</th>" +
    "      <th>New</th>" +
    "      <th>Other</th>" +
    "      <th>Pending</th>" +
    "      <th>Waiting</th>" +
    "      <th>wRedirect</th>" +
    "    </tr>" +
    "    </thead>" +
    "    <tbody>" +
    "  <% _.each(data, function(entry) { %>" +
    "  <%  var status = entry.fetchStatus; %>" +
    "    <tr>" +
    "      <td><%=entry.siteConfig.hostname%></td>" +
    "      <td><%=status.All%></td>" +
    "      <td><%=status.New%></td>" +
    "      <td><%=status.Other%></td>" +
    "      <td><%=status.Pending%></td>" +
    "      <td><%=status.Waiting%></td>" +
    "      <td><%=status.wRedirect%></td>" +
    "    </tr>" +
    "  <% }); %>"  +
    "    </tbody>" +
    "  </table>" +
    "</div>" +

    "<div id='ResponseCodePane' class='SiteInfo' style='display: none'>" +
    "  <table class='border'>" +
    "    <thead>" +
    "    <tr>" +
    "      <th>Site</th>" +
    "      <th>All</th>" +
    "      <th>NONE</th>" +
    "      <th>Ok</th>" +
    "      <th>RC100</th>" +
    "      <th>RC200</th>" +
    "      <th>RC300</th>" +
    "      <th>RC400</th>" +
    "      <th>RC500</th>" +
    "      <th>Unknown</th>" +
    "    </tr>" +
    "    </thead>" +
    "    <tbody>" +
    "  <% _.each(data, function(entry) { %>" +
    "  <%  var rc = entry.responseCode; %>" +
    "    <tr>" +
    "      <td><%=entry.siteConfig.hostname%></td>" +
    "      <td><%=rc.All%></td>" +
    "      <td><%=rc.NONE%></td>" +
    "      <td><%=rc.OK%></td>" +
    "      <td><%=rc.RC100%></td>" +
    "      <td><%=rc.RC200%></td>" +
    "      <td><%=rc.RC300%></td>" +
    "      <td><%=rc.RC400%></td>" +
    "      <td><%=rc.RC500%></td>" +
    "      <td><%=rc.Unknown%></td>" +
    "    </tr>" +
    "  <% }); %>"  +
    "    </tbody>" +
    "  </table>" +
    "</div>" +

    "<div id='ErrorCodePane' class='SiteInfo' style='display: none'>" +
    "  <table class='border'>" +
    "    <thead>" +
    "    <tr>" +
    "      <th>Site</th>" +
    "    </tr>" +
    "    </thead>" +
    "    <tbody>" +
    "  <% _.each(data, function(entry) { %>" +
    "    <tr>" +
    "      <td><%=entry.siteConfig.hostname%></td>" +
    "    </tr>" +
    "  <% }); %>"  +
    "    </tbody>" +
    "  </table>" +
    "</div>" +

    "<div id='PageTypesPane' class='SiteInfo' style='display: none'>" +
    "  <table class='border'>" +
    "    <thead>" +
    "    <tr>" +
    "      <th>Site</th>" +
    "    </tr>" +
    "    </thead>" +
    "    <tbody>" +
    "  <% _.each(data, function(entry) { %>" +
    "    <tr>" +
    "      <td><%=entry.siteConfig.hostname%></td>" +
    "    </tr>" +
    "  <% }); %>"  +
    "    </tbody>" +
    "  </table>" +
    "</div>" +

    "<div id='FetchCountPane' class='SiteInfo' style='display: none'>" +
    "  <table class='border'>" +
    "    <thead>" +
    "    <tr>" +
    "      <th>Site</th>" +
    "      <th>All</th>" +
    "      <th>FC0</th>" +
    "      <th>FC1-5</th>" +
    "      <th>FC5-10</th>" +
    "      <th>FC10-25</th>" +
    "      <th>FC>25</th>" +
    "    </tr>" +
    "    </thead>" +
    "    <tbody>" +
    "  <% _.each(data, function(entry) { %>" +
    "  <%  var fc = entry.fetchCount; %>" +
    "    <tr>" +
    "      <td><%=entry.siteConfig.hostname%></td>" +
    "      <td><%=fc.All%></td>" +
    "      <td><%=fc.FC0%></td>" +
    "      <td><%=fc['FC1-5']%></td>" +
    "      <td><%=fc['FC5-10']%></td>" +
    "      <td><%=fc['FC10-25']%></td>" +
    "      <td><%=fc['FC>25']%></td>" +
    "    </tr>" +
    "  <% }); %>"  +
    "    </tbody>" +
    "  </table>" +
    "</div>"
  ),
  
	initialize: function () {
	  _.bindAll(this, 'render');
	  this.model.bind('change', this.render);
	  this.render() ;
	},
  
	render: function() {
    var data = this.model.get('data') ;
	  var params = { data : data } ;
    var html = this._template(params);

    var el = $("<div style='padding-bottom: 30px'>").append(html);
    $("#SiteInfoSet", el).buttonset();

    $('table', el).dataTable({
      "sPaginationType": "full_numbers",
    });
    crawler.Crawler.workspace.addTab('SiteInfoTab', 'Site Info', el) ;
  },

  onClickInfo: function(e) { 
    var selectPaneId = e.target.id.replace('Btn', 'Pane') ;
    $('div.SiteInfo').each(function() {
      var id = $(this).attr('id') ;
      if(id == selectPaneId) $(this).css('display', 'block');
      else $(this).css('display', 'none');
    });
  }
});
