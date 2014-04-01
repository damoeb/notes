/*global notes:false */

'use strict';

(function (notes) {

    notes.messages = {

        error: function (message) {
            noty({type: 'error', text: message, timeout: false, force: true});
        },

        success: function (message) {
            noty({type: 'success', text: message, timeout: 2000});
        },

        information: function (message) {
            noty({type: 'information', text: message});
        }
    };

})(notes);

