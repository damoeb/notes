/*global notes:false */
/*global Backbone:false */
/*global REST_SERVICE:false */

'use strict';

notes.model = {
    Folder: Backbone.Model.extend({
        urlRoot: REST_SERVICE + '/folder'
    }),

    Database: Backbone.Model.extend({
        urlRoot: REST_SERVICE + '/database'
    }),

    Document: Backbone.Model.extend({
        defaults: {
            modified: null,
            title: '',
            text: '',
            star: false
        },
        urlRoot: REST_SERVICE + '/document/'
    }),

    Bookmark: Backbone.Model.extend({
        urlRoot: REST_SERVICE + '/bookmark'
    })

};