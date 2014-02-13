/*global notes:false */
/*global REST_SERVICE:false */

'use strict';

$.widget('notes.search', {
    _create: function () {

    },
    refresh: function (query) {

        var $this = this;

        if (query.trim().length < 2) {
            console.log('skip search');
            return;
        }

        $this.query = query;
        $this.start = 0;
        $this.rows = 100;

        $this._query();
    },
    _query: function () {

        var $this = this;

        var params = {
            '${query}': $this.query,
            '${start}': $this.start,
            '${database}': notes.app.databaseId(),
            '${rows}': $this.rows
        };

        notes.util.jsonCall('GET', '/notes/rest/search/?query=${query}&database=${database}&start=${start}&rows=${rows}', params, null,
            function (documents) {

            }
        );

    }
});