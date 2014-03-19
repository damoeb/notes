/*global notes:false */
/*global REST_SERVICE:false */

'use strict';

(function (notes) {

    notes.databases = {

        _databaseId: null,

        init: function () {
            this.reload();
        },

        reload: function () {
            var $target = $('#databases');

            notes.util.jsonCall('GET', REST_SERVICE + '/database', null, null, function (database) {

                var $tree = $('<ol/>', {class: 'tree'}).appendTo(
                    $target
                );

                notes.folders.defaultFolderId(database.defaultFolderId);

                notes.util.jsonCall('GET', REST_SERVICE + '/database/${dbId}/roots', {'${dbId}': database.id}, null, function (folders) {

                    var rootFolders = folders;

                    if (rootFolders && rootFolders.length > 0) {

                        $.each(rootFolders, function (index, rootFolder) {
                            $('<li/>')
                                .appendTo($tree)
                                .folder({
                                    model: new notes.model.Folder(rootFolder)
                                });
                        });
                    }
                });

            });
        },

        id: function (id) {
            if (typeof id !== 'undefined') {
                this._databaseId = id;
            } else {
                return this._databaseId;
            }
        }
    }
})(notes);