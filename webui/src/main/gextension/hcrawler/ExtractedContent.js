ExtractedContent = Backbone.Model.extend({
});


ExtractedContentView = Backbone.View.extend({
	el: $('body'),

  _template: _.template(
    "<div id='HCrawlerConfig-ExtractedContent' title='Extracted Content'>" +
    "  <div id='HCrawlerConfig-ExtractedContent-Tabs'>" +
    "    <ul>" +
    "      <%for(var name in extractedContent) { %>" +
    "        <li><a href='#HCrawlerConfig-ExtractedContent-tintuc'><%=name%></a></li>" +
    "      <%}%>"  +
    "    </ul>" +
    "    <%for(var name in extractedContent) { %>" +
    "    <%  var extract = extractedContent[name] ; %>" +
    "        <div id='#HCrawlerConfig-ExtractedContent-<%=name%>'>" +
    "          <h3><%=extract.title%></h3>" +
    "           <br/>" +
    "          <div style='font-weight: bold'><%=extract.description%></div>" +
    "           <br/><br/>" +
    "          <div><%=extract.content%></div>" +
    "        </div>" +
    "    <%}%>"  +
    "  </div>" +
    "</div>"
  ),
  
	initialize: function (options) {
    this.extractedContent = options.extractedContent ;
    this.model = new ExtractedContent() ;
	  this.render() ;
	},
  
	render: function() {
    $("#HCrawlerConfig-ExtractedContent").remove();

	  var params = {
      "extractedContent": this.extractedContent
    } ;
    $('body').append(this._template(params)); 

    $("#HCrawlerConfig-ExtractedContent").dialog({
      autoOpen: true,
      height: 450, width: 700,
      modal: true,
      buttons: {
        Close: function() {
          $(this).dialog( "close" );
        }
      }
    });
    $('#HCrawlerConfig-ExtractedContent-Tabs').tabs() ;
  },
});

