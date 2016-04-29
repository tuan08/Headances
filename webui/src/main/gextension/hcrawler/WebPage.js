WebPage = {
  getSite : function() {
    var url = window.location.href ;
    var idx = url.indexOf('://') ;
    if(idx > 0) url = url.substring(idx + 3) ;
    var idx = url.indexOf('/') ;
    if(idx > 0) url = url.substring(0, idx) ;
    if(url.indexOf('www') >= 0) {
      idx = url.indexOf(".") ;
      if(idx > 0) url = url.substring(idx + 1) ;
    }
    return url;
  },

  getInjectURL : function() { 
    var url = window.location.href ;
    var idx = url.indexOf('://') ;
    var idx = url.indexOf('/', idx + 3) ;
    if(idx > 0) url = url.substring(0, idx) ;
    return url ; 
  },

  getSelectionXpath : function() { 
    var xpath = this.getXpath(window.getSelection().anchorNode) ;
    if(xpath == '' || xpath.indexOf('clickable selectSelectionXpath') >= 0) {
      return '';
    }
    return xpath ;
  },

  getXpath : function(el) { 
    var xpath = '' ;
    while(el != null) {
      if(el.localName != null) {
        if(xpath != '') xpath = '/' + xpath ; 
        var pseg = el.localName ;
        var clazz = el.className ;
        if(clazz != null && clazz != '') pseg = pseg + '[class=' + clazz + ']' ;
        xpath = pseg + xpath;
      }
      el = el.parentNode ;
    }
    return xpath ;
  },


  queryByXpath : function(xpath) { 
    xpath = xpath.replace(/\//g, ' > ') ;
    var result = $(document).find(xpath)
    return result ;
  },

  createDefaultSiteConfig : function() {
    var site = this.getSite() ;
    var config = {
      "hostname" : this.getSite(),
      "description": "",
      "injectUrl" : [this.getInjectURL()],
      "ignoreUrlPattern": null,
      "maxConnection" : 3,
      "crawlDeep": 3,
      "refreshPeriod": 86400,
      "xpathConfig": null
    };
    return config ;
  },

  createDefaultXpathConfig : function() {
    var config = {
      "name" : "",
      "urlPattern" : "",
      "xpath" : {
        "title" : "",
        "description" : "",
        "content" : ""
      }
    };
    return config ;
  }
};
