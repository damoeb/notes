$.widget('notes.editors', {

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

        $this.element.empty();
        $('#document-list').hide();

        notes.app.documentId(documentId);

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

    createDocument: function (title) {
        var $this = this;
        var kindString = 'text';

        var settings = {kind: kindString};

        $this.element.empty();
        $('#document-list').hide();

        $this.loadDocument(settings, new notes.model.Document({
            folderId: notes.app.activeFolderId(),
            title: title
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

        $this.element.append($content);

    }
})
