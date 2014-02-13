/*global notes:false */
/*global REST_SERVICE:false */

'use strict';

(function (notes) {

    $('#search-input').keypress(function (e) {
        if (e.which == 13) {
            notes.search();
        }
    });

    $('#action-search').click(function (e) {
        notes.search();
    });


    notes.search = function () {

        var query = $('#search-input').val();

        if (query.trim().length < 2) {
            console.log('skip search');
            return;
        }

        var params = {
            '${query}': query,
            '${start}': 0,
            '${database}': notes.app.databaseId(),
            '${rows}': 100
        };

        console.log('execute query');
        console.log(params);

        notes.util.jsonCall('GET', '/notes/rest/search/?query=${query}&database=${database}&start=${start}&rows=${rows}', params, null,
            function (documents) {
                console.log(documents.length)
            }
        );

    }

})(notes);
