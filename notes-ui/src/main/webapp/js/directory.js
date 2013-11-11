$.widget("notes.directory", {

    options: {
        databaseId: 0
    },

    _init: function () {
        var $this = this;
        $this.container = {};
        $this.selectedFolderId = null;
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

        notes.util.jsonCall('GET', '/notes/rest/database/${dbId}', {'${dbId}': $this.options.databaseId}, null, function (databaseJson) {

            // remove folder information
            var minDatabaseData = {
                id: databaseJson.id,
                ownerId: databaseJson.ownerId,
                name: databaseJson.name,
                selectedFolderId: databaseJson.selectedFolderId
            };
            $this.model = new notes.model.database(minDatabaseData);


            // -- render root ------------------------------------------------------------------------------------------
            var root = $('<div/>').appendTo($this.element);


            var rootSettings = $('<div/>', {class: 'ui-icon ui-icon-gear', style: 'float:right'}).click(function () {
                notes.dialog.folder.settings(new notes.model.folder({
                    databaseId: databaseJson.id
                }));
            });

            $('<div/>', {class: 'group-item active'}).append(
                    $('<div/>', {class: 'group-icon ui-icon ui-icon-clipboard'})
                ).append(
                    $('<div/>', {text: databaseJson.name, class: 'group-label'})
                ).append(
                    rootSettings
                ).append(
                    $('<div/>', {class: 'clear'})
                ).appendTo(
                    root
                );


            // -- render children --------------------------------------------------------------------------------------

            if (databaseJson.folders && databaseJson.folders.length > 0) {

                $.each(databaseJson.folders, function (index, folderJson) {
                    $('<div/>')
                        .appendTo(root)
                        .folder({
                            model: new notes.model.folder(folderJson),
                            selectedId: databaseJson.selectedFolderId
                        });
                });
            }
        });
    },

    selectedFolder: function (folderId) {
        var $this = this;
        if (folderId) {
            $this.model.set('selectedFolderId', folderId);
            $this.model.save();

        } else {
            return $this.model.get('selectedFolderId');
        }
    },

    pushFolder: function (folder) {
        var $this = this;

        $this.descendants[folder.model().get('id')] = folder;
    },

    folder: function (folderId) {
        var $this = this;
        return $this.descendants[folderId];
    }

});
