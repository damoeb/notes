/*global notes:false */
/*global REST_SERVICE:false */

'use strict';

(function (notes) {

    notes.queries = {

        _databaseId: null,

        init: function () {

            var $this = this;

            $('#search-input').autosuggest();

            $('#action-search').unbind('click').click(function (e) {
                $this.find();
            });

            $('#search-view').find('.action-close').unbind('click').click(function () {
                $('#search-view').hide();
                $('#folder-view').show();
                $('#document-view').hide();
                $('#document-and-folder-view').show();
            });
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
                '${database}': notes.folders.databaseId(),
                '${rows}': rows,
                '${context}': notes.folders.activeFolderId()
            };

            notes.util.jsonCall('GET', '/notes/rest/search/?query=${query}&database=${database}&start=${start}&rows=${rows}&context=${context}', params, null,
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