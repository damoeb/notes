$.widget("notes.database", {

    options: {
        databaseId: 0
    },

    _init: function () {
        var $this = this;
        $this.container = {};
        $this.descendants = {};
    },

    _create: function () {

        var $this = this;

        var databaseId = $this.options.databaseId;
        if (typeof(databaseId) == 'undefined') {
            throw 'databaseId is null'
        }

        $this.reload();
    },

    reload: function () {

        var $this = this;

        $this.element.empty();

        var rootFolders = null;

        var modelLoaded = false;
        var rootsLoaded = false;

        var fnSetup = function () {

            if (modelLoaded && rootsLoaded) {

                // -- render root ------------------------------------------------------------------------------------------

                var $root = $('<div/>').appendTo($this.element);

                var $rootSettings = $('<div/>', {class: 'ui-icon ui-icon-gear', style: 'float:right'}).click(function () {
                    notes.dialog.folder.settings(new notes.model.Folder({
                        databaseId: $this.getModel().get('id')
                    }));
                });

                $('<div/>', {class: 'group-item active'}).append(
                        $('<div/>', {class: 'group-icon ui-icon ui-icon-clipboard'})
                    ).append(
                        $('<div/>', {text: $this.getModel().get('name'), class: 'group-label'})
                    ).append(
                        $rootSettings
                    ).append(
                        $('<div/>', {class: 'clear'})
                    ).appendTo(
                        $root
                    );


                // -- render children --------------------------------------------------------------------------------------

                if (rootFolders && rootFolders.length > 0) {

                    $.each(rootFolders, function (index, rootFolder) {
                        $('<div/>')
                            .appendTo($root)
                            .folder({
                                model: new notes.model.Folder(rootFolder),
                                opened: $this.getModel().get('openFolders')
                            });
                    });
                }
            }
        };

        notes.util.jsonCall('GET', '/notes/rest/database/${dbId}', {'${dbId}': $this.options.databaseId}, null, function (databaseJson) {

            $this.model = new notes.model.Database(databaseJson);
            modelLoaded = true;
            fnSetup();
        });

        notes.util.jsonCall('GET', '/notes/rest/database/${dbId}/roots', {'${dbId}': $this.options.databaseId}, null, function (folders) {

            rootFolders = folders;
            rootsLoaded = true;
            fnSetup();
        });
    },

    getModel: function () {
        return this.model;
    },

    selectedFolder: function (folderId) {
        var $this = this;
        /*
         if (folderId) {
         $this.model.set('selectedFolderId', folderId);

         // todo improve
         $this.model.save();

         } else {
         return $this.model.get('selectedFolderId');
         }
         */

        return 1;
    },

    pushFolder: function (folder) {
        var $this = this;

        $this.descendants[folder.model().get('id')] = folder;
    },

    folder: function (folderId) {
        var $this = this;
        return $this.descendants[folderId];
    },
    _destroy: function () {
        // todo implement widget method
    }

});
