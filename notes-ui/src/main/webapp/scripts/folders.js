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

        createFolder: function () {
//            todo check
//            notes.dialog.folder.newFolder(new notes.model.Folder({id:notes.folders.activeFolderId()}))
            notes.dialog.folder.newFolder(
                new notes.model.Folder(
                    {
                        id: this.activeFolderId()
                    }
                )
            );
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
        }
    };

})(notes);

