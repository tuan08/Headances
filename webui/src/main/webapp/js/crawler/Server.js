if(crawler == null) var crawler = {} ;

crawler.Server = {
  serverUrl: '/rest/',

  connect : function(url, username, password) {
    var server = this ;
    $.ajax({ 
      type: "GET",
      dataType: "json",
      url: this.serverUrl + "connect",
      data: { "url": url, "username": username, "password": password},
      async: false ,
      success: function(data){        server.members = data ; }
    });
    return server.members ;
  },

  executeGet : function(host, port, method) {
    var server = this ;
    var returnData = null ;
    $.ajax({ 
      type: "GET",
      dataType: "json",
      url: this.serverUrl + method,
      data: { "host": host, "port": port},
      async: false ,
      error: function(data) {  
        console.debug("Error: \n" + JSON.stringify(data)) ; 
      },
      success: function(data) {  returnData = data ; }
    });
    return returnData ;
  },

  getSiteInfos : function(host, port) {
    return this.executeGet(host, port, "getSiteInfos") ;
  },

  getFetcherInfos : function(host, port) {
    return this.executeGet(host, port, "getFetcherInfos") ;
  },

  getSiteConfig : function(host, port, hostname) {
    var server = this ;
    var returnData = null ;
    $.ajax({ 
      type: "GET",
      dataType: "json",
      url: this.serverUrl + 'getSiteConfig',
      data: { "host": host, "port": port, "hostname": hostname},
      async: false ,
      success: function(data) {  returnData = data ; }
    });
    return returnData ;
  },

  saveSiteConfig : function(host, port, config) {
    var returnData = null ;
    $.ajax({ 
      async: false ,
      type: "POST",
      dataType: "json",
      contentType: "application/json; charset=utf-8",
      url: this.serverUrl + 'saveSiteConfig?host=' + host + '&port=' + port,
      data:  JSON.stringify(config) ,
      error: function(data) {  
        console.debug("Error: \n" + JSON.stringify(data)) ; 
      },
      success: function(data) {  
        returnData = data ; 
      }
    });
    return returnData ;
  },

  getDataProcessInfo : function(host, port) {
    return this.executeGet(host, port, "getDataProcessInfo") ;
  },

  getURLScheduleInfo : function(host, port) {
    return this.executeGet(host, port, "getURLDatumScheduleInfo") ;
  },

  getURLCommitInfo : function(host, port) {
    return this.executeGet(host, port, "getURLDatumCommitInfo") ;
  },

  getJVMInfo : function(host, port) {
    return this.executeGet(host, port, "getJVMInfo") ;
  }
}
