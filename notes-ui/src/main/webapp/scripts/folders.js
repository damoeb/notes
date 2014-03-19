/*global notes:false */

'use strict';

(function (notes) {

    notes.folders = {

        _activeFolderId: 0,
        _defaultFolderId: 0,
        _descendants: {},

        activeFolderId: function (id) {
            if (typeof id !== 'undefined') {
                this._activeFolderId = id;
                // todo sync activefolderid
//                new notes.model.Database({activeFolderId:id}).save()
            } else {
                return this._activeFolderId;
            }
        },

        defaultFolderId: function (id) {
            if (typeof id !== 'undefined') {
                this._defaultFolderId = id;
            } else {
                return this._defaultFolderId;
            }
        },

        add$Folder: function ($folder) {
            this._descendants[$folder.getModel().get('id')] = $folder;
        },

        get$Folder: function (folderId) {
            return this._descendants[folderId];
        }
    };

})(notes);

