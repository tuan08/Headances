AppView = Backbone.View.extend({
  el: $("body"),

  initialize: function () {
    this.friends = new Friends( null, { view: this });
    //Create a friends collection when the view is initialized.
  },

  events: {
    "click #add-friend":  "showPrompt",
  },

  showPrompt: function () {
    var friend_name = prompt("Who is your friend?");
    var friend_model = new Friend({ name: friend_name });
    //Add a new friend model to our friend collection
    this.friends.add( friend_model );
  },

  addFriendLi: function (model) {
	//The parameter passed is a reference to the model that was added
    if(model == null || model.get('name') == null) return ;
	$("#friends-list").append("<li>" + model.get('name') + "</li>");
    //Use .get to receive attributes of the model
  },
  
  done: function (model) {
  }
});
