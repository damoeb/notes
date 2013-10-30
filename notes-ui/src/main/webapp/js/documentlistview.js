$.widget("notes.documentListView", {
    options: {
    },
    _init: function () {
        this._reset();
    },
    _reset: function () {
        this.element.empty();
    },
    _create: function () {
        var $this = this;
        $this._reset();

        notes.util.jsonCall('GET', '/notes/rest/folder/${folderId}/documents', {'${folderId}': $this.options.folderId}, null, function (documents) {

        });
    }

});
