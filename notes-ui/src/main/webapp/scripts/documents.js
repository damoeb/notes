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
            $('#document-list').documentList({
                folderId: folderId
            });

            $('#document-view').hide();
            $('#dashboard-view').hide();
            $('#folder-view').show();
            $('#document-and-folder-view').show();
            $('#search-view').hide();
        },

        trashOne: function (id) {
            console.log('trash one');

            var callback = function (success) {

                if (success) {
                    notes.messages.information('Moved to trash');
                    // show doc list
                }
            };

            this._moveTo([id], notes.folders.trashFolderId(), callback);
        },

        trash: function (ids) {

            if (ids.length == 0) {

                console.log('trash current folder');
                // todo implement

            } else {

                console.log('trash selected ids: ' + ids);

                var callback = function (success) {

                    if (success) {
                        notes.messages.information('Moved to trash');

                    }
                };

                this._moveTo(ids, notes.folders.trashFolderId(), callback);
            }

        },

        createDialog: function () {
            var $modal = $('#create-document-dialog').modal();
            var $title = $modal.find('.document-title').val('').focus();
            var fnSubmit = function () {
                notes.documents.create($title.val());
                $modal.modal('hide');
            };
            $title.keypress(function (e) {
                if (e.which == 13) {
                    fnSubmit();
                }
            });
            $modal.find('.submit').click(fnSubmit);
        },

        moveDialog: function (ids) {

            if (ids.length == 0) {

                console.log('move current folder');
                // todo implement

            } else {

                console.log('move selected ids: ' + ids);

                var $this = this;

                if (ids.length > 0) {

                    var $modal = $('#move-documents-dialog').modal();
                    $modal.find('.database').database({
                        onSelect: function (model) {
                            var toFolderId = model.get('id');
                            console.log('select ' + toFolderId);
                            $this.moveTo(ids, toFolderId);
                            $modal.modal('hide');
                        },
                        sync: false
                    });
                }
            }

        },

        moveTo: function (documentIds, folderId) {

            var callback = function (success) {
                if (success) {
                    notes.messages.success('Moved to <a href="#folder:' + folderId + '">' + notes.folders.getFolderModel(folderId).get('name') + '</a>');
                }
            };

            this._moveTo(documentIds, folderId, callback);
        },

        _moveTo: function (documentIds, folderId, callback) {

            var newFolderId = folderId;
            var oldFolderId = notes.folders.activeFolderId();

            var payload = {
                'documentIds': documentIds,
                'toFolderId': newFolderId
            };

            var onSuccess = function () {

//              todo bad style to fetch parent first
                $('.folder-' + newFolderId).parent().folder('updateDocCount', documentIds.length);
                $('.folder-' + oldFolderId).parent().folder('updateDocCount', -documentIds.length);

                for (var i = 0; i < documentIds.length; i++) {
                    $('.document-' + documentIds[i]).remove();
                }

                callback(true);
            };

            var onError = function () {
                callback(false);
                notes.messages.error('An error occurred');
            };

            notes.util.jsonCall('POST', REST_SERVICE + '/document/move', null, JSON.stringify(payload), onSuccess, onError);
        },

        destroy: function (ids) {

            if (ids.length == 0) {

                console.log('destroy current folder');

                notes.folders.destroy(notes.folders.activeFolderId());

            } else {

                console.log('destroy selection irrecoverably');

                var affectedFolderId = notes.folders.activeFolderId();

                var payload = {
                    'documentIds': ids
                };

                var onSuccess = function () {

                    // todo bad style to fetch parent first
                    $('.folder-' + affectedFolderId).parent().folder('updateDocCount', -ids.length);

                    for (var i = 0; i < ids.length; i++) {
                        $('.document-' + ids[i]).remove();
                    }
                };

                var onError = function () {
                    callback(false);
                    notes.messages.error('An error occurred');
                };

                notes.util.jsonCall('POST', REST_SERVICE + '/document/delete', null, JSON.stringify(payload), onSuccess, onError);
            }
        }

    };

})(notes);

