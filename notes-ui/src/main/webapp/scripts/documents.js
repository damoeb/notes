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

        moveTo: function ($document, folder) {
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

