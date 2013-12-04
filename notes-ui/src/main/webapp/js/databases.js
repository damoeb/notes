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
    },

    current: function () {
        // todo: return current db model
        throw 'current not implemented'
    },

    activeFolderId: function () {
        // todo: set and get activeFolderId in current database
        throw 'activeFolderId not implemented'
    },

    put: function ($folder) {
        this.descendants[$folder.model().get('id')] = $folder;
    },

    reload: function () {
        var $this = this;

        var $editButton = $('<div/>', {class: 'ui-icon ui-icon-gear', style: 'float:right'}).click(function () {
            notes.dialog.database.settings();
        });
        var $target = $this.element.empty()
            .append(
                $('<div/>', {text: 'Databases', class: 'group-header'}).append(
                    $editButton
                )
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

                    $tree.tree({databaseId: model.get('id')});
                });

                if ($this.options.databaseId == model.get('id')) {
                    $item.addClass('active');
                }
            });

        });
    }
});
