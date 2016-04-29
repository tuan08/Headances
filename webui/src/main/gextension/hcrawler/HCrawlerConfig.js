Util = {
  formatNull : function(obj) {
    if(obj == null || obj == undefined) return '' ;
    return obj ;
  },

  formatNewline : function(obj) {
    if(obj == null || obj == undefined) return '' ;
    return obj.replace(/\r/g, '<br/>') ;
  },

  joinStringArray: function(array) {
    if(array == null) return '' ;
    return array.join() ;
  },

  toStringArray: function(string) {
    if(string == null) return null ;
    if(string == '') return [] ;
    var array = string.split(',') ;
    for(var i = 0; i < array.length; i++) array[i] = array[i].trim() ;
    return array ;
  }
}

HCrawlerConfig = Backbone.Model.extend({
	initialize: function () {
    this.set({'toggle': false}) ;
    var site = WebPage.getSite() ;
    var siteConfig = Server.getSiteConfig(site) ;
    this.set({"siteConfig": siteConfig}) ;
  },

  toggle: function() {
    var toggle = this.get('toggle') ;
    this.set({'toggle': !toggle}) ;
  }
});

HCrawlerConfigView = Backbone.View.extend({
	el: $('body'),

  events: { 
    'click span.selXpathConfig': 'selXpathConfig',
    'click span.addXpathConfig': 'addXpathConfig',
    'click span.saveXpathConfig': 'saveXpathConfig',
    'click span.rmXpathConfig': 'rmXpathConfig',
    'click span.highlightXpathConfig': 'highlightXpathConfig',
    'click span.optimizeXpathConfig': 'optimizeXpathConfig',
    'click span.testExtractXpathConfig': 'testExtractXpathConfig',
    'mousedown label.selectSelectionXpath': 'selectSelectionXpath',
    'click button.commitSiteConfig': 'commitSiteConfig'
  },    
  
  _template: _.template(
    "<div id='HCrawlerConfig' title='HCrawler Configuration'>" +
    "  <div id='HCrawlerConfigTabs'>" +
    "    <ul>" +
    "      <li><a href='#HCrawlerConfigSiteTab'>Site</a></li>" +
    "      <li><a href='#HCrawlerConfigXpathTab'>Xpath</a></li>" +
    "      <li><a href='#HCrawlerConfigServerTab'>Server</a></li>" +
    "      <li><a href='#HCrawlerConfig-help'>Help</a></li>" +
    "    </ul>" +

    "    <div id='HCrawlerConfigSiteTab'>" +
    "      <form>" +
    "        <label>Site</label>" +
    "        <input type='text' name='hostname' value='<%=siteConfig.hostname%>'/>" +
    "        <label>Description</label>" +
    "        <input type='text' name='description' value='<%=Util.formatNull(siteConfig.description)%>'/>" +
    "        <label>Tags</label>" +
    "        <input type='text' name='tags' value='<%=Util.joinStringArray(siteConfig.tags)%>'/>" +
    "        <label>Ignore Url Pattern</label>" +
    "        <input type='text' name='ignoreUrlPattern' value='<%=Util.joinStringArray(siteConfig.ignoreUrlPattern)%>'/>" +
    "        <label >Inject URL</label>" +
    "        <input type='text' name='injectUrl' value='<%=Util.joinStringArray(siteConfig.injectUrl)%>'/>" +
    "        <label>Max Connection</label>" +
    "        <input type='text' name='maxConnection' value='<%=siteConfig.maxConnection%>'/>" +
    "        <label>Crawl Deep</label>" +
    "        <input type='text' name='crawlDeep' value='<%=siteConfig.crawlDeep%>'/>" +
    "        <label>Refresh Period</label>" +
    "        <input type='text' name='refreshPeriod' value='<%=siteConfig.refreshPeriod%>'/>" +

    "        <label style='display: inline-block'>Crawl Subdomain</label>" +
    "        <input class='checkbox' type='checkbox' checked='false' name='crawlSubDomain' value='<%=siteConfig.crawlSubDomain%>'/>" +
    "      </form>" +
    "    </div>" +

    "    <div id='HCrawlerConfigXpathTab'>" +
    "      <div>" +
    "        <% _.each(siteConfig.xpathConfig, function(entry) { %>" +
    "          <span class='link selXpathConfig'><%=entry.name%></span> | " +
    "        <% }); %>"  +
    "        [<span class='link addXpathConfig'>New</span>]" +
    "      </div>" +
    "      <form>" +
    "        <label>Name</label>" +
    "        <input type='text' name='name' value='<%=xpathConfig.name%>'/>" +
    "        <label >URL Pattern</label>" +
    "        <input type='text' name='urlPattern' value='<%=xpathConfig.urlPattern%>'/>" +
    "        <label class='clickable selectSelectionXpath' for='title'>Title</label>" +
    "        <textarea name='title'><%=xpathConfig.xpath.title%></textarea>" +
    "        <label class='clickable selectSelectionXpath' for='description'>Description</label>" +
    "        <textarea name='description'><%=xpathConfig.xpath.description%></textarea>" +
    "        <label class='clickable selectSelectionXpath' for='mainContent'>Content</label>" +
    "        <textarea name='mainContent'><%=xpathConfig.xpath.content%></textarea>" +
    "      </form>" +
    "      <div style='text-align: center; padding-top: 10px'>" +
    "        <span class='link optimizeXpathConfig'>Optimize</span>" +
    "        <span class='link highlightXpathConfig'>Highlight</span>" +
    "        <span class='link testExtractXpathConfig'>Test</span>" +
    "        <span class='link rmXpathConfig'>Remove</span>" +
    "        <span class='link saveXpathConfig'>Save</span>" +
    "      </div>" +
    "    </div>" +

    "    <div id='HCrawlerConfigServerTab'>" +
    "      <form>" +
    "        <label for='webServer'>Web Server</label>" +
    "        <input id='webServer' type='text' name='webServer' value='http://localhost:8080/rest'/>" +

    "        <label>Crawler Server</label>" +
    "        <input type='text' name='server' value='localhost:5700'/>" +
    "      </form>" +
    "      <div style='text-align: center; padding-top: 10px'>" +
    "        <span class='link saveXpathConfig'>Save</span>" +
    "      </div>" +
    "    </div>" +

    "    <div id='HCrawlerConfig-help'>" +
    "      <p>Proin elit arcu, rutrum commodo, vehicula tempus, commodo a, risus. Curabitur" +
    "      Phasellus ipsum. Nunc tristique tempus lectus.</p>" +
    "    </div>" +
    "  </div>" +
    "  <div style='text-align: center; padding-top: 10px'>" +
    "    <button class='commitSiteConfig'>Save Site Config</button>" +
    "  </div>" +
    "</div>"
  ),
  
	initialize: function () {
	  _.bindAll(this, 'render', 'renderToggle', 'selectSelectionXpath', 'selXpathConfig', 'saveXpathConfig', 'addXpathConfig', 'rmXpathConfig');
    this.model = new HCrawlerConfig() ;
    this.state = {} ;
	  this.model.bind('change:toggle', this.renderToggle);
    this.render() ; 
	},
  
	render: function() {
    $("#HCrawlerConfig").remove();

    var siteConfig = this.model.get('siteConfig') ;
    var xpathConfig = null;
    if(siteConfig.xpathConfig != null) {
      for(var i = 0; i < siteConfig.xpathConfig.length; i++) {
        if(siteConfig.xpathConfig[i].name == this.state.xpath) {
          xpathConfig = siteConfig.xpathConfig[i];
          break ;
        }
      }
      if(xpathConfig == null) {
        xpathConfig = siteConfig.xpathConfig[0] ;
        this.state.xpath = xpathConfig.name;
      }
    }
    if(xpathConfig == null) {
      xpathConfig = WebPage.createDefaultXpathConfig() ;
    }
	  var params = {
      "siteConfig": siteConfig,
      "xpathConfig": xpathConfig,
    } ;
    var html = this._template(params) ; 
    var body = $('body'); 
    body.append(html); 

    $("#HCrawlerConfig").dialog({
      autoOpen: true,
      height: 'auto', width: 400,
      hide: { effect: 'drop', direction: "down" },
      modal: false,
      position: ['right', 'top'],
      zIndex: 1000,
      open: function (e, ui) {
        var uidialog = $(this).closest('.ui-dialog');
        uidialog.css({position: 'fixed'});
        $('.ui-dialog-titlebar', uidialog).css({"padding": "3px 10px", "line-height": "18px"}) ;
      }
    });

    $('#HCrawlerConfigTabs').tabs() ;
    if(this.state.tab != null) {
      $('#HCrawlerConfigTabs').tabs('select', this.state.tab);
      this.state.tab = null ;
    } 
  } ,

	renderToggle: function() {
    var toggle = this.model.get('toggle') ;
    if(toggle) this.show() ;
    else this.hide() ;
  } ,

	show: function() {
    $("#HCrawlerConfig").dialog( "open" );
  } ,

  hide : function() {
    $("#HCrawlerConfig").dialog("close");
  },

  saveXpathConfig : function() { 
    var siteConfig = this.model.get('siteConfig') ;
    var tab = $('#HCrawlerConfigXpathTab') ;
    var nameInput = $("input[name=name]", tab) ;
    var name = nameInput.val() ;
    if(name == '') return ;
    var xpathConfig = null ;
    if(siteConfig.xpathConfig != null) {
      for(var i = 0; i < siteConfig.xpathConfig.length; i++) {
        if(name == siteConfig.xpathConfig[i].name) {
          xpathConfig = siteConfig.xpathConfig[i];
          break ;
        }
      }
    } else {
      siteConfig.xpathConfig = [] ;
    }
    var newConfig = false ;
    if(xpathConfig == null) {
      xpathConfig = { xpath: {}} ;
      siteConfig.xpathConfig.push(xpathConfig) ;
      newConfig = true ;
    }
    xpathConfig.name = name ;
    xpathConfig.urlPattern = $("input[name=urlPattern]", tab).val() ;
    xpathConfig.xpath.title = $("textarea[name=title]", tab).val() ;
    xpathConfig.xpath.description = $("textarea[name=description]", tab).val() ;
    xpathConfig.xpath.content = $("textarea[name=mainContent]", tab).val() ;
    this.state.tab = 1 ;
    this.state.xpath = name ;
    if(newConfig) this.render() ;
  },

  selXpathConfig : function(evt) { 
    var name = evt.target.textContent;
    var siteConfig = this.model.get('siteConfig') ;
    var xpathConfig = null ;
    for(var i = 0; i < siteConfig.xpathConfig.length; i++) {
      if(name == siteConfig.xpathConfig[i].name) {
        xpathConfig = siteConfig.xpathConfig[i];
        break ;
      }
    }
    var tab = $('#HCrawlerConfigXpathTab') ;
    $("input[name=name]", tab).val(xpathConfig.name) ;
    $("input[name=urlPattern]", tab).val(xpathConfig.urlPattern) ;
    $("textarea[name=title]", tab).val(xpathConfig.xpath.title) ;
    $("textarea[name=description]", tab).val(xpathConfig.xpath.description) ;
    $("textarea[name=mainContent]", tab).val(xpathConfig.xpath.content) ;
    this.state.xpath = xpathConfig.name;
  },

  addXpathConfig : function() { 
    var tab = $('#HCrawlerConfigXpathTab') ;
    $("input[name=name]", tab).val('') ;
    $("input[name=urlPattern]", tab).val('') ;
    $("textarea[name=title]", tab).val('') ;
    $("textarea[name=description]", tab).val('') ;
    $("textarea[name=mainContent]", tab).val('') ;
  },

  rmXpathConfig : function() { 
    var siteConfig = this.model.get('siteConfig') ;
    var tab = $('#HCrawlerConfigXpathTab') ;
    var nameInput = $("input[name=name]", tab) ;
    var name = nameInput.val() ;
    for(var i = 0; i < siteConfig.xpathConfig.length; i++) {
      if(name == siteConfig.xpathConfig[i].name) {
        siteConfig.xpathConfig.splice(i, 1) ;
        break ;
      }
    }
    this.state.tab = 1 ;
    this.render() ;
  },

  selectSelectionXpath : function(evt) { 
    var xpath = WebPage.getSelectionXpath() ;
    if(xpath == '') {
      alert("Please select a text!!!") ;
      return ;
    }
    var forAttr = evt.target.getAttribute('for') ;
    var tab = $('#HCrawlerConfigXpathTab') ;
    $("textarea[name=" + forAttr + "]", tab).val(xpath) ;
  },

  optimizeXpathConfig : function() { 
    var tab = $('#HCrawlerConfigXpathTab') ;
    var fields = ['title', 'description', 'mainContent'] ;
    for(var i = 0; i < fields.length; i++) {
      var sel = $("textarea[name=" + fields[i] + "]", tab);
      var val = sel.val() ;
      var path = val.split('/') ;
      if(path.length < 6) continue ;
      var clazzCount = 0, pathCount = 0 ;
      var optimizePath = null ;
      for(var j = path.length - 1; j >0; j--) {
        if(path[j].indexOf('class=') > 0) clazzCount++ ;
        pathCount++ ;
        if(optimizePath == null) optimizePath = path[j]  ;
        else optimizePath = path[j] + '/' + optimizePath  ;
        if(clazzCount >=2 && pathCount >=3) break ;
        if(pathCount >= 5) break ;
      }
      sel.val(optimizePath) ;
    }
  },
  
  highlightXpathConfig : function() { 
    $("[highlight=true]").each(function() {
      $(this).removeAttr('highlight') ;
      $(this).css({"background" : '' });
    });
    var tab = $('#HCrawlerConfigXpathTab') ;
    this.highlightBlock($("textarea[name=title]", tab).val(), 'blue');
    this.highlightBlock($("textarea[name=description]", tab).val(), 'gray');
    this.highlightBlock($("textarea[name=mainContent]", tab).val(), 'lightgray');
  },

  highlightBlock : function(xpath, color) { 
    if(xpath == null || xpath == '') return ;
    var result = WebPage.queryByXpath(xpath) ;
    for(var i = 0; i < result.size(); i++) {
      $(result[i]).css({"background" : color });
      $(result[i]).attr("highlight" , "true");
    }
  },

  commitSiteConfig : function() { 
    var siteConfig = this.model.get('siteConfig') ;
    this.saveXpathConfig() ;
    var tab = $('#HCrawlerConfigSiteTab') ;
    siteConfig.hostname = $("input[name=hostname]", tab).val() ;
    siteConfig.description = $("input[name=description]", tab).val() ;
    siteConfig.tags = Util.toStringArray($("input[name=tags]", tab).val()) ;
    siteConfig.ignoreUrlPattern = Util.toStringArray($("input[name=ignoreUrlPattern]", tab).val()) ;
    siteConfig.injectUrl = Util.toStringArray($("input[name=injectUrl]", tab).val()) ;
    siteConfig.maxConnection = $("input[name=maxConnection]", tab).val() ;
    siteConfig.crawlDeep = $("input[name=crawlDeep]", tab).val() ;
    siteConfig.refreshPeriod = $("input[name=refreshPeriod]", tab).val() ;
    siteConfig.crawlSubDomain = $("input[name=crawlSubDomain]", tab).val() ;
    siteConfig = Server.saveSiteConfig(siteConfig) ;
  },


  testExtractXpathConfig : function() { 
    this.saveXpathConfig() ;
    var url = window.location.href ;
    var siteConfig = this.model.get('siteConfig') ;
    var extract = Server.testExtractXpathConfig(url, siteConfig) ;
    var extractedContentView = new ExtractedContentView({"extractedContent": extract}) ;
  }
});
