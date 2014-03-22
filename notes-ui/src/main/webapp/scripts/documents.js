/*global notes:false */

'use strict';

(function (notes) {

    notes.documents = {

        _documentId: null,

        currentDocumentId: function (id) {
            if (typeof id !== 'undefined') {
                this._documentId = id;
            } else {
                return this._documentId;
            }
        },

        fetch: function (folderId) {

            notes.folders.activeFolderId(folderId);

            // todo save document when closed
            $('#document-list').documentList('refresh', folderId);

            $('#document-view').hide();
            $('#folder-view').show();
            $('#document-and-folder-view').show();
            $('#search-view').hide();
        },

        moveTo: function (document, folder) {

            var newFolderId = folder.get('id');
            var oldFolderId = document.folderId;

            var params = {
                '${doc}': document.id,
                '${folderId}': newFolderId
            };

            var onSuccess = function () {
                //todo refresh new and old folder
                $('#folder-' + newFolderId).folder('incrDocumentCount');
                $('#folder-' + oldFolderId).folder('decrDocumentCount');
                // remove in list
                noty({text: 'Moved to ' + notes.folders.get$Folder(newFolderId).getModel().get('name')});
            };

            var onError = function () {
                noty({type: 'error', text: 'An error occurred'});
            };

            notes.util.jsonCall('POST', REST_SERVICE + '/document/move/${doc}/${folderId}', params, null, onSuccess, onError);

            // todo implement
//                new notes.model.Document({
//                    id: $draggable.data('documentId'),
//                    folderId: model.get('id'),
//                    event: 'MOVE'
//                }).save(null, {
//                        success: function () {
//                            // refresh ui
//                            $('#databases').databases('reloadTree');
//                        }
//                    });
        }
    };

})(notes);

