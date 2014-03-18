/*global notes:false */
/*global REST_SERVICE:false */

'use strict';

$.widget('notes.search', {

    _init: function () {
        this.template = _.template($('#document-in-hits-view').html());

        this.element.find('.action-close').unbind('click').click(function () {
            $('#search-view').hide();
            $('#folder-view').show();
            $('#document-view').hide();
            $('#document-and-folder-view').show();
        });

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

        $('#search-view').show();
//        $('#folder-view').hide();
//        $('#document-view').hide();
        $('#document-and-folder-view').hide();

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
            '${rows}': $this.rows,
            '${context}': notes.app.activeFolderId(),
            '${contextOnly}': notes.app.searchContextOnly()
        };

        notes.util.jsonCall('GET', '/notes/rest/search/?query=${query}&database=${database}&start=${start}&rows=${rows}&context=${context}&contextOnly=${contextOnly}', params, null,
            function (result) {
                console.log(result.docs.length + ' hits');

                $this.element.find('.numFound').text(result.numFound);
                $this.element.find('.elapsedTime').text(Math.round(result.elapsedTime / 10) / 100);

                $.each(result.docs, function (index, document) {
                    $target.append($this.template(document));
                });
            }
        );

    }
});