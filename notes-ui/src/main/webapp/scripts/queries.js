/*global notes:false */
/*global REST_SERVICE:false */

'use strict';

(function (notes) {

    notes.queries = {

        _databaseId: null,
        _isContextOnly: false,

        init: function () {

            $('#search-view').find('.action-close').unbind('click').click(function () {
                $('#search-view').hide();
                $('#folder-view').show();
                $('#document-view').hide();
                $('#document-and-folder-view').show();
            });
        },

        contextOnly: function (isContextOnly) {
            if (typeof isContextOnly !== 'undefined') {
                this._isContextOnly = isContextOnly;

                console.log('ContextOnly ' + isContextOnly);

                var label = 'All';
                if (isContextOnly) {
                    // todo show folder name
                    label = 'Context';
                }
                $('#search-context-label').text(label);
            } else {
                return this._isContextOnly;
            }
        },

        find: function (query) {

            if (typeof query === 'undefined') {
                query = $('#search-input').val();
            } else {
                $('#search-input').val(query);
            }

            if (query.trim().length < 2) {
                console.log('skip search ' + query);
                return;
            }

            var start = 0;
            var rows = 100;

            $('#search-view').show();
            $('#document-and-folder-view').hide();

            var template = _.template($('#document-in-hits-view').html());

            console.log('query=' + query + ' start=' + start + ' rows=' + rows);

            var $container = $('#search-view');
            var $target = $container.find('.hit-list').empty();

            var params = {
                '${query}': query,
                '${start}': start,
                '${database}': notes.databases.id(),
                '${rows}': rows,
                '${context}': notes.folders.activeFolderId(),
                '${contextOnly}': notes.app.searchContextOnly()
            };

            notes.util.jsonCall('GET', '/notes/rest/search/?query=${query}&database=${database}&start=${start}&rows=${rows}&context=${context}&contextOnly=${contextOnly}', params, null,
                function (result) {
                    console.log(result.docs.length + ' hits');

                    $container.find('.numFound').text(result.numFound);
                    $container.find('.elapsedTime').text(Math.round(result.elapsedTime / 10) / 100);

                    $.each(result.docs, function (index, document) {
                        $target.append(template(document));
                    });
                }
            );

        }

    }
})(notes);