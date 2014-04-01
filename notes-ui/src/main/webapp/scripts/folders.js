/*global notes:false */

'use strict';

(function (notes) {

    notes.folders = {

        _activeFolderId: 0,
        _trashFolderId: 0,
        _defaultFolderId: 0,
        _databaseId: 0,
        _descendants: {},

        activeFolderId: function (id) {
            if (typeof id !== 'undefined') {
                this._activeFolderId = parseInt(id);

                console.log('active folder ' + id)
                $('#databases .active').removeClass('active');
                $('.folder-' + id).addClass('active');

//                new notes.model.Database({activeFolderId:id}).save()
                // todo sync activefolderid
            } else {
                return this._activeFolderId;
            }
        },


        databaseId: function (id) {
            if (typeof id !== 'undefined') {
                this._databaseId = id;
            } else {
                return this._databaseId;
            }
        },

        trashFolderId: function (id) {
            if (typeof id !== 'undefined') {
                this._trashFolderId = parseInt(id);
            } else {
                return this._trashFolderId;
            }
        },

        defaultFolderId: function (id) {
            if (typeof id !== 'undefined') {
                this._defaultFolderId = parseInt(id);
            } else {
                return this._defaultFolderId;
            }
        },

        storeFolderModel: function (model) {
            this._descendants[model.get('id')] = model;
        },

        getFolderModel: function (folderId) {
            return this._descendants[folderId];
        },

        createFolderDialog: function (isChild) {

            console.log('create folder');

            var $this = this;

            var $modal = $('#create-folder-dialog').modal();

            var $name = $modal.find('.folder-name').val('').focus().select();

            var parentId = isChild ? $this.activeFolderId() : null;

            var fnSubmit = function () {

                var model = new notes.model.Folder({
                    name: $name.val(),
                    parentId: parentId,
                    databaseId: notes.folders.databaseId()
                });
                model.save(null, {
                    success: function () {
                        $('#databases').database('reload');
                    }
                });

                $modal.modal('hide');
            };
            $name.keypress(function (e) {
                if (e.which == 13) {
                    fnSubmit();
                }
            });
            $modal.find('.submit').click(fnSubmit);
        },

        open: function (folderId) {

            // load documents
            notes.documents.fetch(folderId);

            // show in tree
//            var $folder = this.get$Folder(folderId);
//            if (typeof  $folder === 'undefined') {
//                console.log('Folder ' + folderId + ' does not exist');
//                // todo fetch missing tree nodes
//
//            } else {
//                // todo open folder
//            }
        },

        rename: function (folderId, name) {
//            todo implement
            console.log('Rename ' + folderId + ' to ' + name);
        },

        renameDialog: function () {

            var $this = this;

            var folderId = this.activeFolderId();

            console.log('rename folder ' + folderId);

            var $modal = $('#rename-folder-dialog').modal();
            var $name = $modal.find('.folder-name').val($this.getFolderModel(folderId).get('name')).focus().select();

            var fnSubmit = function () {
                $this.rename(folderId, $name.val());
                $modal.modal('hide');
            };
            $name.keypress(function (e) {
                if (e.which == 13) {
                    fnSubmit();
                }
            });
            $modal.find('.submit').click(fnSubmit);
        }

    };

})(notes);

