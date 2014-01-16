/*global notes:false */
/*global Backbone:false */

'use strict';

notes.model = {
    Folder: Backbone.Model.extend({
        urlRoot: REST_SERVICE + '/folder',
        initialize: function () {
            this.listener = [];
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

    Database: Backbone.Model.extend({
        urlRoot: REST_SERVICE + '/database'
    }),

    Document: Backbone.Model.extend({
        defaults: {
            title: '',
            text: ''
        },
        urlRoot: REST_SERVICE + '/document/'
    }),

    Bookmark: Backbone.Model.extend({
        urlRoot: REST_SERVICE + '/bookmark'
    })

};