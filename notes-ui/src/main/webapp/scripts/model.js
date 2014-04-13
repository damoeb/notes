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
            id: null,
            modified: null,
            title: '',
            star: false,
            tags: []
        },
        urlRoot: REST_SERVICE + '/document/basic/'
    }),

    TextDocument: Backbone.Model.extend({
        defaults: {
            id: null,
            modified: null,
            title: '',
            star: false,
            text: '',
            tags: []
        },
        urlRoot: REST_SERVICE + '/document/'
    })

};