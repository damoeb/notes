notes.model = {
    folder: Backbone.Model.extend({
        url: '/notes/rest/folder',
        initialize: function () {
            this.listener = []
        },
        onChange: function (callback) {
            this.listener.push(callback);
        },
        change: function () {
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
    }),

    document: Backbone.Model.extend({
        defaults: {
            title: '',
            text: '',
            event: 'UPDATE'
        },
        url: '/notes/rest/document/'
    })
};