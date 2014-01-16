/*global notes:false */
/*global REST_SERVICE:false */

'use strict';

$.widget('notes.search', {

    options: {
        query: null,
        folderId: null,
        databaseId: null,
        start: 0,
        rows: 100
    },

    _create: function () {

        var $this = this;

        var params = {
            '${query}': $this.options.query,
            '${start}': $this.options.start,
            '${rows}': $this.options.rows
        };

        notes.util.jsonCall('GET', REST_SERVICE + '/search/?query=${query}&database=${database}&start=${start}&rows=${rows}', params, null, null
        );
//        notes.util.jsonCall('GET', '/notes/rest/search/?query=${query}&database=${database}&start=${start}&rows=${rows}', params, null,
//            function (documents) {
//                // todo implement
//            }
//        );
    }
});
