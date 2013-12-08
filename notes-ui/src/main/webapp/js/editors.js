$.widget("notes.editors", {

    options: {
        syncInterval: 15000 // msec
    },

    _create: function () {
        var $this = this;

//        // -- events
//        setInterval(function () {
//
//            // todo $this.syncModel();
//
//        }, $this.options.syncInterval);

    },

    edit: function (documentId, unloadCallback) {

        var $this = this;

        if (!documentId) {
            throw 'document id is null.'
        }

        notes.util.jsonCall('GET', '/notes/rest/document/${documentId}', {'${documentId}': documentId}, null, function (document) {

            var settings = {
                kind: document.kind,
                unloadCallback: unloadCallback
            };

            var model = new notes.model.Document(document);
            model.set('event', 'UPDATE');

            $this.loadDocument(settings, model);
        });
    },

    createDocument: function () {
        var $this = this;
        var kindString = 'text';

        var settings = {kind: kindString};
        $this.loadDocument(settings, new notes.model.Document({
            folderId: $('#databases').databases('getActiveFolderId')
        }));
    },

    loadDocument: function (settings, model) {
        var $this = this;

        var $content = $('<div/>');

        switch (settings.kind.toLowerCase().trim()) {
            case 'text':
                $content.texteditor({model: model});
                break;
            case 'pdf':
                $content.pdfeditor({model: model});
                break;
        }

        $this.element.empty().append($content);

    }
})
