/*global notes:false */

'use strict';

(function (notes) {

    notes.app = new function () {

        var _activeFolderId = 0;
        var _defaultFolderId = 0;
        var _documentId = 0;
        var _databaseId = 0;
        var _isContextOnly = false;
        var _descendants = {};

        this.activeFolderId = function (id) {
            if (typeof id !== 'undefined') {
                _activeFolderId = id;
                // todo sync activefolderid
//                new notes.model.Database({activeFolderId:id}).save()
            } else {
                return _activeFolderId;
            }
        };

        this.defaultFolderId = function (id) {
            if (typeof id !== 'undefined') {
                _defaultFolderId = id;
            } else {
                return _defaultFolderId;
            }
        };

        this.searchContextOnly = function (isContextOnly) {
            if (typeof isContextOnly !== 'undefined') {
                _isContextOnly = isContextOnly;

                console.log('ContextOnly ' + isContextOnly);

                var label = 'All';
                if(isContextOnly) {
                    // todo show folder name
                    label = 'Context';
                }
                $('#search-context-label').text(label);
            } else {
                return _isContextOnly;
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

