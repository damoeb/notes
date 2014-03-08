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
        $('#search-view').hide();

        notes.app.documentId(documentId);

        notes.util.jsonCall('GET', REST_SERVICE + '/document/${documentId}', {'${documentId}': documentId}, null, function (document) {

            var $content = $('<div/>');

            notes.app.activeFolderId(document.folderId);

            switch (document.kind.toLowerCase().trim()) {
                case 'text':
                    console.log('edit text-document');
                    $content.texteditor({
                        model: new notes.model.TextDocument(document)
                    });
                    break;
                case 'pdf':
                    console.log('edit pdf-document');
                    $content.pdfeditor({
                        model: new notes.model.BasicDocument(document)
                    });
                    break;
                case 'bookmark':
                    console.log('edit bookmark');
                    $content.bookmark({
                        model: new notes.model.Bookmark(document)
                    });
                    break;
                default:
                    var model = new notes.model.BasicDocument(document);
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

        var model = new notes.model.TextDocument({
            folderId: notes.app.activeFolderId(),
            title: title
        });

        var $content = $('<div/>').texteditor({model: model, editMode: true});

        $this.element.empty().append($content);
    }
});
