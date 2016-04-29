if(crawler == null) var crawler = {} ;

crawler.Fetcher = Backbone.Model.extend({
  id : 'fetcher'
});


crawler.FetcherView = Backbone.View.extend({
  tagName: 'div', // name of tag to be created        

	events: {
    'click li.onClickSiteInfo': 'onClickSiteInfo',
    'click li.onClickFetcherInfo': 'onClickFetcherInfo',
    'click li.onClickJVMInfo': 'onClickJVMInfo'
  },
  
  _template: _.template(
    "<h6><a href=\"javascript:$('#<%=baseId%>-ctrl-block').toggle('slow')\"><%=member.host%>:<%=member.port%></a></h6>" +
    "<ul id='<%=baseId%>-ctrl-block'>" +
    //" <li>Queue" +
    //"   <ul>" +
    //"     <li>Queue 1</li>" +
    //"     <li>Queue 2</li>" +
    //"   </ul>" +
    //" </li>" +
    " <li class='clickable onClickSiteInfo'>Site Info</li>" +
    " <li class='clickable onClickFetcherInfo'>Fetcher Info</li>" +
    " <li class='clickable onClickJVMInfo'>JVM Info</li>" +
    "</ul>"
  ),
  
	initialize: function () {
	  _.bindAll(this, 'render', 'onClickJVMInfo', 'onClickSiteInfo');
	  this.model.bind('change:member', this.render);
	},
  
	render: function() {
	  var params = { 
      member: this.model.get('member'),
      baseId: this.model.id
    } ;
    $(this.el).html(this._template(params)); 
    //$('ul', this.el).css("display", "none"); 
    return this ; // for chainable calls, like .render().el
  },

  onClickSiteInfo : function() { 
    var info = new crawler.SiteInfo({ member: this.model.get('member')});
    info.update() ;
    var view = new crawler.SiteInfoView({ model: info });
  },

  onClickFetcherInfo : function() { 
    var info = new crawler.FetcherInfo({ member: this.model.get('member')});
    info.update() ;
    var view = new crawler.FetcherInfoView({ model: info });
  },

  onClickJVMInfo : function() { 
    var member = this.model.get('member') ;
    var data = crawler.Server.getJVMInfo(member.host, member.port) ;
    var info = new uicomp.JVMInfo({ data: data});
    var view = new uicomp.JVMInfoView({ model: info });
    crawler.Crawler.workspace.addTab('JVMInfoTab', 'JVM Info', view.getHtmlFragment());
  }
});

crawler.Fetchers = Backbone.Collection.extend({
  model: crawler.Fetcher
});

crawler.FetchersView = Backbone.View.extend({
  el: $('#ctrl-col-fetchers'), // el attaches to existing element
  events: {
  },
  
  initialize: function(){
    _.bindAll(this, 'render', 'appendFetcher'); // every function that uses 'this' as the current object should be in here
    
    this.collection = new crawler.Fetchers();
    this.collection.bind('add', this.appendFetcher); // collection event binder
    this.render();
  },

  render: function(){
    var self = this;
    _(this.collection.models).each(function(fetcher) { // in case collection is not empty
      self.appendFetcher(fetcher);
    }, this);
  },

  appendFetcher: function(fetcher){
    var fetcherView = new crawler.FetcherView({model: fetcher});
    var html = fetcherView.render().el ;
    $('#ctrl-col-fetchers').append(html);
  },


  addFetcher: function(fetcher){
    fetcher.id = 'Fetcher-' + this.collection.size() ;
    this.collection.add(fetcher);
  }
});
