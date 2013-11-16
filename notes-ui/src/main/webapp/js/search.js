$.widget("notes.search", {

    options: {
        query: null
    },

    _init: function () {
        var $this = this;
    },

    _create: function () {

        var $this = this;

        var query = $this.options.query;
        if (typeof(query) == 'undefined') {
            throw 'query is null'
        }

        $this.reload();
    },

    reload: function () {

        var $this = this;

        $this.element.empty();

        notes.util.jsonCall('GET', '/notes/rest/search/${query}', {'${query}': $this.options.query}, null, function (documents) {
            // todo implement
        });
    }
});
