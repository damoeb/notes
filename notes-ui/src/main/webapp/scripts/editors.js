/*global notes:false */
/*global REST_SERVICE:false */

'use strict';

$.widget('notes.editors', {

    editDocument: function (documentId) {

        var $this = this;

        if (!documentId) {
            throw 'document id is null.';
        }

        $('#document-view').show();
        $('#folder-view').hide();

        notes.app.documentId(documentId);

        notes.util.jsonCall('GET', REST_SERVICE + '/document/${documentId}', {'${documentId}': documentId}, null, function (document) {

            var model = new notes.model.Document(document);
            model.set('event', 'UPDATE');

            var $content = $('<div/>');

            var hash = model.get('hash');

            location.replace('#doc:' + hash);

            switch (document.kind.toLowerCase().trim()) {
                case 'text':
                    $content.texteditor({model: model});
                    break;
                case 'pdf':
                    $content.pdfeditor({model: model});
                    break;
                default:
                    console.error('no editor found for ' + document.kind);
                    break;
            }

            $this.element.empty().append($content);

        });
    },

    newDocument: function (title) {
        var $this = this;

        $('#document-view').show();
        $('#folder-view').hide();

        var model = new notes.model.Document({
            folderId: notes.app.activeFolderId(),
            title: title
        });

        var $content = $('<div/>').texteditor({model: model, editMode: true});

        $this.element.empty().append($content);
    }
});
