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

    BasicDocument: Backbone.Model.extend({
        defaults: {
            modified: null,
            title: '',
            star: false
        },
        urlRoot: REST_SERVICE + '/document/basic/'
    }),

    TextDocument: Backbone.Model.extend({
        defaults: {
            modified: null,
            title: '',
            star: false,
            text: ''
        },
        urlRoot: REST_SERVICE + '/document/text/'
    }),

    Bookmark: Backbone.Model.extend({
        urlRoot: REST_SERVICE + '/bookmark'
    })

};