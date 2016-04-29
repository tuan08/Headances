var hcrawlerView = null ;

function test() {
  if(hcrawlerView == null) {
    hcrawlerView = new HCrawlerConfigView() ;
  }
  hcrawlerView.model.toggle() ;
}
