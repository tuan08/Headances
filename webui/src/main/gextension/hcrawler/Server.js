Server = {
  webServerURL: 'http://localhost:8080/rest',
  crawlerHost: 'localhost',
  crawlerPort: 5700,

  getSiteConfig : function(hostname) {
    var returnData = null ;
    $.ajax({ 
      async: false ,
      type: "GET",
      dataType: "json",
      url: this.webServerURL + '/getSiteConfig',
      data: { "host": this.crawlerHost, "port": this.crawlerPort, "hostname": hostname},
      success: function(data) {  returnData = data ; }
    });
    if(returnData == null) {
      returnData = WebPage.createDefaultSiteConfig() ;
    }
    return returnData ;
  },

  saveSiteConfig : function(config) {
    var returnData = null ;
    $.ajax({ 
      async: false ,
      type: "POST",
      dataType: 'json',
      contentType : 'application/json',
      url: this.webServerURL + '/saveSiteConfig?host=' + this.crawlerHost + '&port=' + this.crawlerPort,
      data:  JSON.stringify(config) ,
      error: function(data) {  alert(JSON.stringify(data)) ; },
      success: function(data) {  returnData = data ; }
    });
    return returnData ;
  },


  testExtractXpathConfig : function(url, config) {
    var returnData = null ;
    $.ajax({ 
      async: false ,
      type: "POST",
      dataType: 'json',
      contentType : 'application/json',
      url: this.webServerURL + '/testExtractXpathConfig?url=' + url,
      data:  JSON.stringify(config) ,
      error: function(data) {  alert(JSON.stringify(data)) ; },
      success: function(data) {  returnData = data ; }
    });
    return returnData ;
  }
};
