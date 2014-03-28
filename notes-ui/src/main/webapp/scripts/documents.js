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

        trashSelected: function () {
            var ids = this.getSelectedIds();

            if (ids.length > 0) {
                // on success
                for (var i = 0; i < ids.length; i++) {
                    $('.document-' + ids[i]).remove();
                }
            }
        },

        moveDialog: function () {

            if (this.getSelectedIds().length > 0) {
                // todo render tree
                $('#move-documents-dialog').modal().find('.modal-body');
            }

        },

        moveSelected: function (targetFolder) {
            this.getSelectedIds();
        },

        getSelectedIds: function () {
            var ids = [];
            var $fields = $('.document input[type="checkbox"]:checked');
            for (var i = 0; i < $fields.length; i++) {
                ids.push(parseInt($($fields[i]).attr('doc-id')));
            }
            return ids;
        },

        selectAll: function (select) {
            $('.document input[type="checkbox"]').removeAttr('checked');
            if (select) {
                $('.document input[type="checkbox"]').click();
            }
        },

        create: function (titleOrUrl) {

            var isUrl = titleOrUrl.indexOf('http://') == 0 || titleOrUrl.indexOf('https://') == 0;
            if (isUrl) {
                // redirect to patched website
                location.href = '/notes/rest/proxy/?url=' + encodeURI(titleOrUrl);
            } else {
                notes.editors.create(titleOrUrl);
            }
        },

        fetch: function (folderId) {

            notes.folders.activeFolderId(folderId);

            // todo save document when closed
            $('#document-list').documentList('refresh', folderId);

            $('#document-view').hide();
            $('#dashboard-view').hide();
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

//              todo bad style to fetch parent first
                $('.folder-' + newFolderId).parent().folder('updateDocCount', 1);
                $('.folder-' + oldFolderId).parent().folder('updateDocCount', -1);

                $('.document-' + document.id).remove();

                var message = 'Moved to <a href="#folder:' + newFolderId + '">' + notes.folders.getFolderModel(newFolderId).get('name') + '</a>';

                noty({type: 'success', text: message});
            };

            var onError = function () {
                noty({type: 'error', text: 'An error occurred'});
            };

            notes.util.jsonCall('POST', REST_SERVICE + '/document/move/${doc}/${folderId}', params, null, onSuccess, onError);
        }
    };

})(notes);

