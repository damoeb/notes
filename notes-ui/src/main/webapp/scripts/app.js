/*global notes:false */

'use strict';

(function (notes) {

    notes.app = new function () {

        var _documentId = 0;

        this.documentId = function (id) {
            if (typeof id !== 'undefined') {
                _documentId = id;
            } else {
                return _documentId;
            }
        };
    };

})(notes);

