/*global notes:false */

'use strict';

(function (notes) {

    notes.app = new function () {

        var _documentId = 0;
        var _databaseId = 0;
        var _isContextOnly = false;

        this.searchContextOnly = function (isContextOnly) {
            if (typeof isContextOnly !== 'undefined') {
                _isContextOnly = isContextOnly;

                console.log('ContextOnly ' + isContextOnly);

                var label = 'All';
                if (isContextOnly) {
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
    };

})(notes);

