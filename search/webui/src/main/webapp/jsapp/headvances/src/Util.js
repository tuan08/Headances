headvances.views.Util = {
  newBackButton : function(scope, prevCard) {
    var button = {
      ui: 'back',
      text: 'Back',
      scope: scope,
      handler: function() {
        this.ownerCt.setActiveItem(prevCard, {
          type: 'slide',
          reverse: true,
          scope: this,
          after: function() { this.destroy(); }
        });
      }
    };
    return button;
  },

  newBackIconButton : function(scope, prevCard) {
    var button = {
      iconMask: true,
      iconCls: 'reply',
      scope: scope,
      handler: function() {
        this.ownerCt.setActiveItem(prevCard, {
          type: 'slide',
          reverse: true,
          scope: this,
          after: function() { this.destroy(); }
        });
      }
    };
    return button;
  }
};
