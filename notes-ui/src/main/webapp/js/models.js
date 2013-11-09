notes.model = {
    folder: Backbone.Model.extend({
        url: '/notes/rest/folder',
        initialize: function () {
            this.listener = []
        },
        change: function (callback) {
            this.listener.push(callback);
        },
        set: function (attributes, options) {
            Backbone.Model.prototype.set.apply(this, arguments);

            var $this = this;
            if (typeof(this.listener) !== 'undefined') {
                $.each(this.listener, function (index, callback) {
                    callback.call($this);
                });
            }
        }
    }),

    database: Backbone.Model.extend({
        url: '/notes/rest/database'
    })
};