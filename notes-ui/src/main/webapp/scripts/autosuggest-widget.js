/*global notes:false */
/*global _:false */

'use strict';

$.widget('notes.autosuggest', {

    _init: function () {

        var $this = this;

        var queries = new Bloodhound({
            datumTokenizer: Bloodhound.tokenizers.obj.whitespace('value'),
            ttl: 1,
            queryTokenizer: Bloodhound.tokenizers.whitespace,
            prefetch: {
                url: '/notes/rest/search/history',
                filter: function (response) {
//                    notes.util.sortJSONArrayDESC(response.result, 'lastUsed')
                    return response.result;
                }
            },
            remote: {
                url: '/notes/rest/search/suggest/?q=%QUERY',
                filter: function (response) {
//                    notes.util.sortJSONArrayDESC(response.result, 'lastUsed');
                    return response.result;
                }
            }
        });

        queries.initialize();

        $this.element.typeahead({
            hint: true,
            highlight: true,
            minLength: 1
        }, {
            name: 'queries',
            displayKey: 'value',
            source: queries.ttAdapter()
        });


//        var suggestionLayer = $('<ul class="dropdown-menu" style="width: 100%"></ul>');
//        $this.element.parent().append(suggestionLayer);
//        $this.suggestionLayer = suggestionLayer;

//        var onSuccess = function (history) {
//
//            notes.util.sortJSONArrayDESC(history, 'lastUsed')
//
//            console.log('logged queries: ' + history.length);
//
//            var reformatted = [];
//            for(var i=0; i<history.length; i++) {
//                var item = history[i];
//                reformatted.push(item.query);
//            }
//
//
//            $this.history = reformatted;
//
//            $this.option('source', reformatted);
//        };
//
//        var onError = function () {
//            console.error('Cannot load query-history');
//        };
//
//        notes.util.jsonCall('GET', REST_SERVICE + '/search/history', null, null, onSuccess, onError);
//
//
//        $this.element.unbind('focus').focus(function (e) {
//            $this.option('source', $this.history);
//            console.log('show suggestion layer');
//            $this.search('');
//        });
//
//        return this._super();
//    },
    }

});