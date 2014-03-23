/*global notes:false */
/*global REST_SERVICE:false */

'use strict';

(function (notes) {

    notes.editors = {

        edit: function (documentId) {
            var $this = $('#editors');

            if (!documentId) {
                throw 'document id is null.';
            }

            $('#document-view').show();
            $('#folder-view').hide();
            $('#document-and-folder-view').show();
            $('#search-view').hide();

            notes.documents.currentDocumentId(documentId);

            notes.util.jsonCall('GET', REST_SERVICE + '/document/${documentId}', {'${documentId}': documentId}, null, function (document) {

                var $content = $('<div/>');

                notes.folders.activeFolderId(document.folderId);

                switch (document.kind.toLowerCase().trim()) {
                    case 'text':
                    case 'bookmark':
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
                    default:
                        var model = new notes.model.BasicDocument(document);
                        console.error('no editor found for ' + document.kind);
                        break;
                }

                $this.empty().append($content);

            });
        },

        create: function (title) {
            var $this = $('#editors');

            $('#document-view').show();
            $('#folder-view').hide();

            var model = new notes.model.TextDocument({
                folderId: notes.folders.activeFolderId(),
                title: title
            });

            var $content = $('<div/>').texteditor({model: model, editMode: true});

            $this.empty().append($content);

        }
    }

})(notes);