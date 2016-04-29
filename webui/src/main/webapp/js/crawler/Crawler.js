if(crawler == null) var crawler = {} ;

crawler.Crawler = {
  workspace  : new uicomp.Tab('crawler-workspace'),


  connect  : function() {
    var connectionFormView = new crawler.ConnectionFormView() ;
    connectionFormView.show() ;
  },

  onConnect : function(members) {
    for(var i = 0; i < members.length; i++) {
      if($.inArray('master', members[i].roles) >= 0) {
        this.crawlerMaster = new crawler.Master( { member: members[i] }) ;
        this.crawlerMasterView = new crawler.MasterView({ model: this.crawlerMaster }) ;
        break ;
      }
    }

    this.fetchersView = new crawler.FetchersView() ;
    for(var i = 0; i < members.length; i++) {
      if($.inArray('fetcher', members[i].roles) >= 0) {
        this.fetchersView.addFetcher(new crawler.Fetcher({ member : members[i] })) ;
      }
    }
  },


  testUpdate : function() {
    var newConfig = {
      member : { host: '127.0.0.2', port: 5700 }
    };
    this.crawlerMaster.set(newConfig) ;
  }
};

