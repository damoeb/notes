$.widget("notes.tree", {

    options: {
        databaseId: null
    },

    _create: function () {

        var $this = this;

        var databaseId = $this.options.databaseId;
        if (typeof(databaseId) === 'undefined') {
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

                var $root = $('<div/>').appendTo($this.element);

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

    _destroy: function () {
        console.log('destroy tree');
        // todo implement widget method
    }

});
