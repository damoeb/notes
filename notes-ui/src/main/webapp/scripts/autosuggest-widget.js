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
                    notes.util.sortJSONArrayDESC(response.result, 'lastUsed')
                    return response.result;
                }
            },
            remote: {
                url: '/notes/rest/search/suggest/?q=%QUERY',
                filter: function (response) {
                    notes.util.sortJSONArrayDESC(response.result, 'lastUsed');
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
        }).on('typeahead:selected',function (event, query) {
                var val = query.value;
                console.log('trigger search ' + val);
                notes.queries.find(val);
            }).keypress(function (e) {
                if (e.which == 13) {
                    console.log('trigger search');
                    $(this).typeahead('close');
                    notes.queries.find();
                }
            });
    }

});