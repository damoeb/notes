/*global notes:false */
/*global REST_SERVICE:false */

'use strict';

$.widget('notes.database', {

    options: {
        onSelect: null,
        sync: true
    },

    _init: function () {
        this.reload();
    },

    reload: function () {

        console.log('load database tree');

        var $this = this;

        var $target = this.element.empty();

        var $tree = $('<ol/>', {class: 'tree'}).appendTo(
            $target
        );

        notes.util.jsonCall('GET', REST_SERVICE + '/database/${dbId}/roots', {'${dbId}': notes.folders.databaseId()}, null, function (folders) {

            var rootFolders = folders;

            if (rootFolders && rootFolders.length > 0) {

                $.each(rootFolders, function (index, rootFolder) {
                    $('<li/>')
                        .appendTo($tree)
                        .folder({
                            model: new notes.model.Folder(rootFolder),
                            onSelect: $this.options.onSelect,
                            sync: $this.options.sync
                        });
                });
            }
        });
    }

});