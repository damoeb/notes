$.widget("notes.databases", {

    url: '/notes/rest/database/list',

    options: {
        databaseId: null
    },

    _create: function () {
        var $self = this;

        $self.reload();
    },

    _init: function () {
        var $self = this;
        $self.descendants = {};
        $self.$tree = null;
    },

    addOpenFolder: function (folderId) {
        var $self = this;
        console.log('addOpenFolder ' + folderId);
        $self._getModel().get('openFolders').push({id: folderId});
        $self._getModel().save();
    },

    removeOpenFolder: function (folderId) {
        var $self = this;
        console.log('removeOpenFolder ' + folderId);
        var filteredFolders = [];
        var unfilteredFolders = $self._getModel().get('openFolders');
        for (var i = 0; i < unfilteredFolders.length; i++) {
            if (unfilteredFolders[i]['id'] !== folderId) {
                filteredFolders.push(unfilteredFolders[i]);
            }
        }
        $self._getModel().set('openFolders', filteredFolders);
        $self._getModel().save();
    },

    _getModel: function () {
        return this.$tree.tree('getModel');
    },

    setActiveFolderId: function (folderId) {
        console.log('active folder is ' + folderId);
        this.activeFolderId = folderId;
    },

    getActiveFolderId: function () {
        return this.activeFolderId;
    },

    getDatabaseId: function () {
        return this.databaseId;
    },

    put: function ($folder) {
        this.descendants[$folder.getModel().get('id')] = $folder;
    },

    get$Folder: function (folderId) {
        return this.descendants[folderId];
    },

    reloadTree: function () {
        this.$tree.tree('reload');
    },

    reload: function () {
        var $self = this;

        var $target = $self.element.empty();

        notes.util.jsonCall('GET', $self.url, null, null, function (list) {

            $.each(list, function (index, json) {

                var model = new notes.model.Database(json);

                var $tree = $('<div/>', {class: 'tree'}).appendTo(
                    $target
                );

                $self.$tree = $tree.tree({
                    databaseId: model.get('id')
                });
            });

        });
    }
});
