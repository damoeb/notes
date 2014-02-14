/*global notes:false */
/*global REST_SERVICE:false */

'use strict';

$.widget('notes.search', {

    _init: function () {
        this.template = _.template($('#document-in-hits-view').html());
    },
    refresh: function (query) {

        var $this = this;

        if (query.trim().length < 2) {
            console.log('skip search ' + query);
            return;
        }

        $this.query = query;
        $this.start = 0;
        $this.rows = 100;

        $this._query();

    },
    _query: function () {

        var $this = this;

        console.log('query=' + $this.query + ' start=' + $this.start + ' rows=' + $this.rows);

        var $target = $this.element.find('.hit-list').empty();

        var params = {
            '${query}': $this.query,
            '${start}': $this.start,
            '${database}': notes.app.databaseId(),
            '${rows}': $this.rows
        };

        notes.util.jsonCall('GET', '/notes/rest/search/?query=${query}&database=${database}&start=${start}&rows=${rows}', params, null,
            function (result) {
                console.log(result.docs.length + ' hits');
                $.each(result.docs, function (index, document) {
                    $target.append($this.template(document));
                });
            }
        );

    }
});