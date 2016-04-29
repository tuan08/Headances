Ext.ns('headvances', 'headvances.data');

Ext.regModel('Dummy', {
  fields: ['name', 'hello']
});

headvances.data.DummyData = {
  "dummies" : [
    {"name": "Dummy 1", "hello": "Hello Dummy 1"},
    {"name": "Dummy 2", "hello": "Hello Dummy 2"}
  ]
};


headvances.data.DummyStore = new Ext.data.Store({
  model: 'Dummy',
  data: headvances.data.DummyData.dummies 
}) ;


Ext.regModel('SearchRecordList', {
  fields: ['id', 'url', 'title', 'description']
});

