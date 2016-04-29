Ext.ns('headvances', 'headvances.data.mock');

headvances.data.mock.SearchQueryResponse = {
  "query": {
    "query": "Iphone 3GS",

    "title": true,
    "description": true,
    "content": true,

    "contentType": "all",
    "pageType": "all",

    "index": "web",
    "page": 1
  },
  
  "recordDescription":[
    { 
      "id":"id 0",
      "description":"description 0",
      "url":"url 0",
      "title":"title 0"
    }
  ]
};
headvances.data.mock.SearchCacheResponse = {
  "id": "MockId",
  "url": "mock url",
  "title": "mock title",
  "description": "mock description",
  "content": "mock content",
  "bestMatch": "Best Match Highlight",
  "index": 0
};
