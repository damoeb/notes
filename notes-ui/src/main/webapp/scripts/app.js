/*global notes:false */

'use strict';

(function (notes) {

    notes.app = new function () {

        var _activeFolderId = 0;
        var _documentId = 0;
        var _databaseId = 0;
        var _descendants = {};

        this.activeFolderId = function (id) {
            if (typeof id !== 'undefined') {
                _activeFolderId = id;
            } else {
                return _activeFolderId;
            }
        };

        this.documentId = function (id) {
            if (typeof id !== 'undefined') {
                _documentId = id;
            } else {
                return _documentId;
            }
        };

        this.databaseId = function (id) {
            if (typeof id !== 'undefined') {
                _databaseId = id;
            } else {
                return _databaseId;
            }
        };

        this.add$Folder = function ($folder) {
            _descendants[$folder.getModel().get('id')] = $folder;
        };

        this.get$Folder = function (folderId) {
            return _descendants[folderId];
        };
    };

})(notes);

