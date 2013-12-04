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

    put: function ($folder) {
        this.descendants[$folder.model().get('id')] = $folder;
    },

    get$Folder: function (folderId) {
        return this.descendants[folderId];
    },

    reloadTree: function () {
        this.$tree.tree('reload');
    },

    reload: function () {
        var $this = this;

        var $target = $this.element.empty()
            .append(
                $('<div/>', {text: 'Databases', class: 'group-header'})
            );

        notes.util.jsonCall('GET', $this.url, null, null, function (list) {

            $.each(list, function (index, json) {

                var model = new notes.model.Database(json);

                var $item = $('<div/>', {class: 'group-item'}).append(
                        $('<div/>', {class: 'group-icon ui-icon ui-icon-clipboard'})
                    ).append(
                        $('<div/>', {text: model.get('name'), class: 'group-label'})
                    ).append(
                        $('<div/>', {class: 'clear'})
                    ).appendTo(
                        $target
                    );

                var $tree = $('<div/>', {class: 'tree'}).appendTo(
                    $target
                );

                $item.click(function () {

                    // todo set current
                    $target.find('.active').removeClass('active');
                    $(this).addClass('active');

                    if ($this.$tree != null) {
                        $this.$tree.tree('destroy');
                    }

                    $this.$tree = $tree.tree({
                        databaseId: model.get('id')
                    });
                });

                if ($this.options.databaseId == model.get('id')) {
                    // todo load tree
                    $item.addClass('active');
                }
            });

        });
    }
});
