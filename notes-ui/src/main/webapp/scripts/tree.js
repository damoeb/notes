/*global notes:false */
/*global REST_SERVICE:false */

'use strict';

$.widget('notes.tree', {

    options: {
        databaseId: null
    },

    _create: function () {

        var $this = this;

        var databaseId = $this.options.databaseId;
        if (typeof(databaseId) === 'undefined') {
            throw 'databaseId is null';
        }

        $this.reload();
    },

    reload: function () {

        var $this = this;

        var $root = $this.element.empty();

        var rootFolders = null;
        var openFolders = null;

        var modelLoaded = false;
        var rootsLoaded = false;
        var openLoaded = false;

        var fnSetup = function () {

            if (modelLoaded && rootsLoaded && openFolders) {

                $this.model.set('openFolders', openFolders);

                if (rootFolders && rootFolders.length > 0) {

                    $.each(rootFolders, function (index, rootFolder) {
                        $('<a/>', {class: 'list-group-item', href: '#'})
                            .appendTo($root)
                            .folder({
                                model: new notes.model.Folder(rootFolder),
                                opened: openFolders
                            });
                    });
                }
            }
        };

        notes.util.jsonCall('GET', REST_SERVICE + '/database/${dbId}', {'${dbId}': $this.options.databaseId}, null, function (databaseJson) {

            $this.model = new notes.model.Database(databaseJson);
            modelLoaded = true;
            fnSetup();
        });

        notes.util.jsonCall('GET', REST_SERVICE + '/database/${dbId}/roots', {'${dbId}': $this.options.databaseId}, null, function (folders) {

            rootFolders = folders;
            rootsLoaded = true;
            fnSetup();
        });

        notes.util.jsonCall('GET', REST_SERVICE + '/database/${dbId}/open', {'${dbId}': $this.options.databaseId}, null, function (folders) {

            openFolders = folders;
            openLoaded = true;
            fnSetup();
        });
    },

    getModel: function () {
        return this.model;
    }

});
