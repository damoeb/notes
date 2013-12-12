$.widget("notes.databases", {

    url: '/notes/rest/database/list',

    options: {
        databaseId: null
    },

    _create: function () {
        var $this = this;

        $this.reload();
    },

    _init: function () {
        var $this = this;
        $this.descendants = {};
        $this.$tree = null;
    },

    setActiveFolderId: function (folderId) {
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
        var $this = this;

        var $target = $this.element.empty();

        notes.util.jsonCall('GET', $this.url, null, null, function (list) {

            $.each(list, function (index, json) {

                var model = new notes.model.Database(json);

                var $tree = $('<div/>', {class: 'tree'}).appendTo(
                    $target
                );

                $this.$tree = $tree.tree({
                    databaseId: model.get('id')
                });
            });

        });
    }
});
