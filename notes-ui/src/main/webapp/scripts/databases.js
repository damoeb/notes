/*global notes:false */
/*global REST_SERVICE:false */

'use strict';

$.widget('notes.databases', {

    _create: function () {
        var $self = this;

        $self.reload();
    },

    _init: function () {
        var $self = this;
        $self.$tree = null;
    },

    reloadTree: function () {
        this.$tree.tree('reload');
    },

    reload: function () {
        var $self = this;

        var $target = $self.element.empty();

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
    }


});
