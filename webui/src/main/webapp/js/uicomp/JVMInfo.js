if(uicomp == null) var uicomp = {} ;

uicomp.JVMInfo = Backbone.Model.extend({
	update : function() { 
    var member = this.get('member') ;
    var data = crawler.Server.getJVMInfo(member.host, member.port) ;
    this.set({data: data});
  }
});


uicomp.JVMInfoView = Backbone.View.extend({
  el: $('body'),
  events: { 
    'click #JVMInfoSet input': 'onClickInfo',
    'click #ThreadInfoPane .ShowStacktrace': 'showStacktrace'
  },    
  
  _template: _.template(
    "<div style='width: 400px; margin-bottom: 10px' class='ui-widget-header ui-corner-all'>" +
    "  <span id='JVMInfoSet'>" +
    "    <input type='radio' id='MemoryInfoBtn' name='info' checked='checked' /><label for='MemoryInfoBtn'>Memory Info</label>" +
    "    <input type='radio' id='GarbageCollectionInfoBtn' name='info' /><label for='GarbageCollectionInfoBtn'>Garbage Collection Info</label>" +
    "    <input type='radio' id='ThreadInfoBtn' name='info' /><label for='ThreadInfoBtn'>Thread Info</label>" +
    "  </span>" +
    "</div>" +

    "<div class='JVMInfo' id='MemoryInfoPane'>" +
    "  <table class='border'>" +
    "    <thead>" +
    "    <tr>" +
    "      <th>Name</th>" +
    "      <th>Value</th>" +
    "    </tr>" +
    "    </thead>" +
    "    <tbody>" +
    "    <tr>" +
    "      <td>Init</td>" +
    "      <td><%=data.memoryInfo.init%></td>" +
    "    </tr>" +
    "    <tr>" +
    "      <td>Use</td>" +
    "      <td><%=data.memoryInfo.use%></td>" +
    "    </tr>" +
    "    <tr>" +
    "      <td>Committed</td>" +
    "      <td><%=data.memoryInfo.committed%></td>" +
    "    </tr>" +
    "    <tr>" +
    "      <td>Max</td>" +
    "      <td><%=data.memoryInfo.max%></td>" +
    "    </tr>" +
    "    </tbody>" +
    "  </table>" +
    "</div>" +

    "<div style='display: none' class='JVMInfo' id='GarbageCollectionInfoPane'>" +
    "  <table class='border'>" +
    "    <tr>" +
    "      <th>Name</th>" +
    "      <th>Collection Count</th>" +
    "      <th>Collection Time</th>" +
    "      <th>Pool Names</th>" +
    "    </tr>" +
    "  <% _.each(data.garbageCollectorInfo, function(entry) { %>" +
    "    <tr>" +
    "      <td><%=entry.name%></td>" +
    "      <td><%=entry.collectionCount%></td>" +
    "      <td><%=entry.collectionTime%></td>" +
    "      <td><%=entry.poolNames%></td>" +
    "    </tr>" +
    "  <% }); %>"  +
    "  </table>" +
    "</div>" +

    "<div id='ThreadInfoPane' class='JVMInfo' style='display: none'>" +
    "  <table class='border'>" +
    "    <thead>" +
    "    <tr>" +
    "      <th>Name</th>" +
    "      <th>Id</th>" +
    "      <th>Block Count</th>" +
    "      <th>Block Time</th>" +
    "      <th>Waited Count</th>" +
    "      <th>Waited Time</th>" +
    "      <th>State</th>" +
    "      <th>CPU Time</th>" +
    "      <th>User Time</th>" +
    "    </tr>" +
    "    </thead>" +
    "    <tbody>" +
    "  <% _.each(data.threadInfo.allThreadInfo, function(entry) { %>" +
    "  <% var name = entry.threadName; %>" +

    "    <tr>" +
    "      <td>" + 
    "      <div style='text-overflow: ellipsis; overflow: hidden; white-space: nowrap; width: 200px' class='clickable ShowStacktrace' title='<%=entry.threadName%>'>" + 
    "        <%=name%>" +
    "      </div>" + 
    "        <pre style='display: none'><pre><%=entry.threadStackTrace%></pre>" +
    "      </td>" +
    "      <td><%=entry.threadId%></td>" +
    "      <td><%=entry.threadBlockCount%></td>" +
    "      <td><%=entry.threadBlockTime%></td>" +
    "      <td><%=entry.threadWaitedCount%></td>" +
    "      <td><%=entry.threadWaitedTime%></td>" +
    "      <td><%=entry.threadState%></td>" +
    "      <td><%=entry.threadCPUTime%></td>" +
    "      <td><%=entry.threadUserTime%></td>" +
    "    </tr>" +
    "  <% }); %>"  +
    "    </tbody>" +
    "  </table>" +
    "</div>"
  ),
  
	initialize: function () {
	  _.bindAll(this, 'render');
	  this.model.bind('change', this.render);
	},
  
	getHtmlFragment: function() {
    var data = this.model.get('data') ;
	  var params = { data : data } ;
    var html = this._template(params);

    var el = $("<div style='padding-bottom: 30px'>").append(html);
    $("#JVMInfoSet", el).buttonset();

    $('#ThreadInfoPane table', el).dataTable({
      "sPaginationType": "full_numbers",
    });
    return el ;
  },

	render: function() {
    alert('The method render() of JVMInfo need to be overrided') ;
  },

  onClickInfo: function(e) { 
    var selectPaneId = e.target.id.replace('Btn', 'Pane') ;
    $('div.JVMInfo').each(function() {
      var id = $(this).attr('id') ;
      if(id == selectPaneId) $(this).css('display', 'block');
      else $(this).css('display', 'none');
    });
  },

  showStacktrace: function(e) { 
    var stacktrace = $("pre", e.target.parent).html();
    $(e.target).append("<div title='Stacktrace'>" + stacktrace+ "</div>"); 
    $("div", e.target).dialog({
      autoOpen: true,
      height: 300, width: 500,
      modal: true,
      buttons: {
        Close: function() {
          $(this).dialog( "close" );
        }
      }
    });
  }
});
